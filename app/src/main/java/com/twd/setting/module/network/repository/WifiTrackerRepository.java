package com.twd.setting.module.network.repository;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.NetworkRequest.Builder;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.Log;

import androidx.lifecycle.DefaultLifecycleObserver;
//import androidx.lifecycle.DefaultLifecycleObserver.-CC;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.twd.setting.R;
//import com.twd.setting.SettingConfig;
import com.twd.setting.module.network.model.WifiAccessPoint;
//import com.twd.setting.utils.HLog;
import com.twd.setting.utils.HLog;
import com.twd.setting.utils.ThreadUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class WifiTrackerRepository
        implements DefaultLifecycleObserver {
    private static final String TAG = "WifiTrackerRepository";
    private static final long MAX_SCAN_RESULT_AGE_MILLIS = 25000L;
    private static final int WIFI_RESCAN_INTERVAL_MS = 5000;//10000;
    private final AtomicBoolean mConnected = new AtomicBoolean(false);
    private final ConnectivityManager mConnectivityManager;
    private Context mContext;
    private final IntentFilter mFilter;
    private final List<WifiAccessPoint> mInternalAccessPoints = new ArrayList<WifiAccessPoint>();
    private WifiInfo mLastInfo;
    private NetworkInfo mLastNetworkInfo;
    private final WifiListenerExecutor mListener;
    private final Object mLock = new Object();
    private WifiTrackerNetworkCallback mNetworkCallback;
    private NetworkRequest mNetworkRequest;
    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                updateWifiState(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN));
                return;
            }
            if ("android.net.wifi.SCAN_RESULTS".equals(action)) {
//WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION.equals(action)
//                   || WifiManager.LINK_CONFIGURATION_CHANGED_ACTION.equals(action)
                //            WifiTrackerRepository.access$202(WifiTrackerRepository.this, false);
                Log.d(TAG, "------------> WifiTrackerRepository onReceive  WifiManager.SCAN_RESULTS_AVAILABLE_ACTION");

                fetchScansAndConfigsAndUpdateAccessPoints();
                return;
            }
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                updateNetworkInfo(info);
                fetchScansAndConfigsAndUpdateAccessPoints();
                return;
            }
            if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
                NetworkInfo info = getCurrentNetworkInfo();
                updateNetworkInfo(info);
            }
        }
    };
    private boolean mRegistered;
    private final HashMap<String, ScanResult> mScanResultCache = new HashMap();
    Scanner mScanner;
    private boolean mStaleScanResults = true;
    private final WifiManager mWifiManager;
    Handler mWorkHandler;
    private HandlerThread mWorkThread;

    WifiTrackerRepository(Context context, WifiListener wifiListener, WifiManager wifiManager, ConnectivityManager connectivityManager, IntentFilter filter) {
        mContext = context;
        mWifiManager = wifiManager;
        mListener = new WifiListenerExecutor(wifiListener);
        mConnectivityManager = connectivityManager;
        mFilter = filter;
        if (Build.VERSION.SDK_INT >= 21) {
            mNetworkRequest = new NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN).addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build();
        }

        final HandlerThread workThread = new HandlerThread(TAG
                + "{" + Integer.toHexString(System.identityHashCode(this)) + "}",
                10);
        workThread.start();
        setWorkThread(workThread);
    }

    public WifiTrackerRepository(Context paramContext, WifiListener paramWifiListener, Lifecycle paramLifecycle, boolean paramBoolean1, boolean paramBoolean2) {
        this(paramContext, paramWifiListener, (WifiManager) paramContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE), (ConnectivityManager) paramContext.getSystemService(Context.CONNECTIVITY_SERVICE), newIntentFilter());
        paramLifecycle.addObserver(this);
    }

    @Deprecated
    public WifiTrackerRepository(Context paramContext, WifiListener paramWifiListener, boolean paramBoolean1, boolean paramBoolean2) {
        this(paramContext, paramWifiListener, (WifiManager) paramContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE), (ConnectivityManager) paramContext.getSystemService(Context.CONNECTIVITY_SERVICE), newIntentFilter());
    }

    //private static final boolean DBG()
    //{
    //  return SettingConfig.IS_DEBUG;
    //}

    private void clearAccessPointsAndConditionallyUpdate() {
        synchronized (mLock) {
            if (!mInternalAccessPoints.isEmpty()) {
                mInternalAccessPoints.clear();
                conditionallyNotifyListeners();
            }
        }
    }

    private void conditionallyNotifyListeners() {
        if (!mStaleScanResults) {
            mListener.onAccessPointsChanged();
        }

    }

    private void evictOldScans() {
        long l = SystemClock.elapsedRealtime();
        Iterator localIterator = mScanResultCache.values().iterator();
        while (localIterator.hasNext()) {
            if (l - ((ScanResult) localIterator.next()).timestamp / 1000L > 25000L) {
                localIterator.remove();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void fetchScansAndConfigsAndUpdateAccessPoints() {
        updateAccessPoints(mWifiManager.getScanResults(), mWifiManager.getConfiguredNetworks());
    }

    private void forceUpdate() {
        mLastInfo = mWifiManager.getConnectionInfo();
        mLastNetworkInfo = getCurrentNetworkInfo();
        fetchScansAndConfigsAndUpdateAccessPoints();
    }

    private NetworkInfo getCurrentNetworkInfo() {
        NetworkInfo networkInfo;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Method method = WifiManager.class.getDeclaredMethod("getCurrentNetwork", new Class[0]);
                networkInfo = mConnectivityManager.getNetworkInfo((Network) (method).invoke(mWifiManager, new Object[0]));
                return networkInfo;
            } catch (Exception localException) {
                Log.d(TAG, "In forceUpdate(), WifiManager.getCurrentNetwork() was catch an exception.");
                localException.printStackTrace();
            }
        } else if (Build.VERSION.SDK_INT >= 21) {
            networkInfo = mConnectivityManager.getActiveNetworkInfo();
            if ((networkInfo != null) && (networkInfo.isConnected()) && (networkInfo.getType() == 1)) {
                return networkInfo;
            }
        }
        return null;
    }

    private WifiConfiguration getWifiConfigurationForNetworkId(int networkId, final List<WifiConfiguration> configs) {
        if (configs != null) {
            for (WifiConfiguration config : configs) {
                try {
                    Field localField1 = WifiConfiguration.class.getField("selfAdded");
                    Field localField2 = WifiConfiguration.class.getField("numAssociation");
                    boolean config_selfAdded = ((Boolean) localField1.get(config)).booleanValue();
                    int config_numAssociation = Integer.parseInt(String.valueOf(localField2.get(config)));
                    if (mLastInfo != null && networkId == config.networkId && !(config_selfAdded && config_numAssociation == 0)) {
                        Log.d(TAG,"networkId:"+networkId+",SSID:"+config.SSID);
                        return config;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return null;
    }

    private static IntentFilter newIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.SCAN_RESULTS");
        filter.addAction("android.net.wifi.NETWORK_IDS_CHANGED");
        filter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        filter.addAction("android.net.wifi.RSSI_CHANGED");
        return filter;
    }

    private void pauseScanning() {
        if (mScanner != null) {
            mScanner.pause();
            mScanner = null;
        }
        mStaleScanResults = true;
    }

    private void updateAccessPoints(final List<ScanResult> newScanResults, List<WifiConfiguration> configs) {
        int i;
		if (newScanResults == null) {
            i = 0;
        } else {
            i = newScanResults.size();
        }
        //final Map<String, WifiConfiguration> configsByKey = new ArrayMap(configs.size());
		Map<String, WifiConfiguration> configsByKey = new ArrayMap(i);
        if (configs != null) {
            for (WifiConfiguration config : configs) {
                configsByKey.put(WifiAccessPoint.getKey(config), config);
            }
        }
        ArrayMap<String, List<ScanResult>> scanResultsByApKey = updateScanResultCache(newScanResults);
        WifiConfiguration connectionConfig = null;
        if (mLastInfo != null) {
            connectionConfig = getWifiConfigurationForNetworkId(mLastInfo.getNetworkId(), configs);
        }
        synchronized (mLock) {
            ArrayList<WifiAccessPoint> cachedAccessPoints = new ArrayList<>(mInternalAccessPoints);

            ArrayList<WifiAccessPoint> accessPoints = new ArrayList<>();

            for (Map.Entry<String, List<ScanResult>> entry : scanResultsByApKey.entrySet()) {
                //for (ScanResult result : entry.getValue()) {
                //    NetworkKey key = NetworkKey.createFromScanResult(result);
                //    if (key != null && !mRequestedScores.contains(key)) {
                //        scoresToRequest.add(key);
                //    }
                //}

                WifiAccessPoint accessPoint =
                        getCachedOrCreate(entry.getValue(), cachedAccessPoints);
                if (mLastInfo != null && mLastNetworkInfo != null) {
                    accessPoint.update(connectionConfig, mLastInfo, mLastNetworkInfo);
                }

                // Update the matching config if there is one, to populate saved network info
                accessPoint.update(configsByKey.get(entry.getKey()));

                accessPoints.add(accessPoint);
            }
            if (accessPoints.isEmpty() && connectionConfig != null) {
                WifiAccessPoint activeAp = new WifiAccessPoint(mContext, connectionConfig);
                activeAp.update(connectionConfig, mLastInfo, mLastNetworkInfo);
                accessPoints.add(activeAp);
                //scoresToRequest.add(NetworkKey.createFromWifiInfo(mLastInfo));
            }
            Collections.sort(accessPoints);

            mInternalAccessPoints.clear();
            mInternalAccessPoints.addAll(accessPoints);
			conditionallyNotifyListeners();
/*
            ArrayList arrayList = new ArrayList(mInternalAccessPoints);
            ArrayList localObject1 = new ArrayList();
            Iterator iterator_entry = ((ArrayMap) localObject4).entrySet().iterator();
            while (iterator_entry.hasNext()) {
                Map.Entry localEntry = (Map.Entry) iterator_entry.next();
                WifiAccessPoint localWifiAccessPoint = getCachedOrCreate((List) localEntry.getValue(), arrayList);
                if (mLastInfo != null) {
                    if (mLastNetworkInfo != null) {
                        localWifiAccessPoint.update(configuration, mLastInfo, mLastNetworkInfo);
                    }
                }
                localWifiAccessPoint.update((WifiConfiguration) ((Map) localObject2).get(localEntry.getKey()));
                ((ArrayList) localObject1).add(localWifiAccessPoint);
            }
            if ((((ArrayList) localObject1).isEmpty()) && (paramList != null)) {
                localObject2 = new WifiAccessPoint(mContext, paramList);
                ((WifiAccessPoint) localObject2).update(configuration, mLastInfo, mLastNetworkInfo);
                ((ArrayList) localObject1).add(localObject2);
            }
            Collections.sort((List) localObject1);
            //if (SettingConfig.IS_DEBUG)
            {
                Log.d(TAG, "------ Dumping SSIDs that were not seen on this scan ------");
                Iterator iterator = mInternalAccessPoints.iterator();
                while (iterator.hasNext()) {
                    localObject2 = (WifiAccessPoint) iterator.next();
                    if (((WifiAccessPoint) localObject2).getSsid() != null) {
                        localObject2 = ((WifiAccessPoint) localObject2).getSsidStr();
                        localObject3 = ((ArrayList) localObject1).iterator();
                        do {
                            if (!((Iterator) localObject3).hasNext()) {
                                break;
                            }
                            localObject4 = (WifiAccessPoint) ((Iterator) localObject3).next();
                        } while ((((WifiAccessPoint) localObject4).getSsidStr() == null) || (!((WifiAccessPoint) localObject4).getSsidStr().equals(localObject2)));
                        i = 1;
                        if (i == 0) {
                            Log.d(TAG, "Did not find "+(String) localObject2+" in this scan");
                        }
                    }
                }
                Log.d(TAG, "---- Done dumping SSIDs that were not seen on this scan ----");
            }
            mInternalAccessPoints.clear();
            mInternalAccessPoints.addAll((Collection) localObject1);
			conditionallyNotifyListeners();
            */
        }
        

    }


    @SuppressLint("MissingPermission")
    private void updateNetworkInfo(NetworkInfo networkInfo) {
        if (!mWifiManager.isWifiEnabled()) {
            clearAccessPointsAndConditionallyUpdate();
            return;
        }
        if (networkInfo != null) {
            mLastNetworkInfo = networkInfo;
            //if (DBG()) {
            Log.d(TAG, "mLastNetworkInfo set: " + mLastNetworkInfo);
            //}

            if (networkInfo.isConnected() != mConnected.getAndSet(networkInfo.isConnected())) {
                mListener.onConnectedChanged();
            }
        }
        WifiConfiguration connectionConfig = null;
        mLastInfo = mWifiManager.getConnectionInfo();
        Log.d(TAG, "mLastInfo set as: " + mLastInfo+", networkId:"+mLastInfo.getNetworkId());
        if (mLastInfo != null) {
            connectionConfig = getWifiConfigurationForNetworkId(mLastInfo.getNetworkId(),
                    mWifiManager.getConfiguredNetworks());
        }
        boolean updated = false;
        boolean reorder = false;

        synchronized (mLock) {
            for (int i = mInternalAccessPoints.size() - 1; i >= 0; --i) {
                WifiAccessPoint ap = mInternalAccessPoints.get(i);
                boolean previouslyConnected = ap.isActive();
                if (ap.update(connectionConfig, mLastInfo, mLastNetworkInfo)) {
                    updated = true;
                    if (previouslyConnected != ap.isActive()) reorder = true;
                }
                //if (ap.update(mScoreCache, mNetworkScoringUiEnabled, mMaxSpeedLabelScoreCacheAge)) {
                //    reorder = true;
                //    updated = true;
                //}
            }

            if (reorder) {
                Collections.sort(mInternalAccessPoints);
            }
            if (updated) {
                conditionallyNotifyListeners();
            }
        }

    }

    private ArrayMap<String, List<ScanResult>> updateScanResultCache(final List<ScanResult> newResults) {
        if (Build.VERSION.SDK_INT >= 24) {
            for (ScanResult newResult : newResults) {
                if (newResult.SSID == null || newResult.SSID.isEmpty()) {
                    continue;
                }
                mScanResultCache.put(newResult.BSSID, newResult);
            }

            if (!mStaleScanResults) {
                evictOldScans();
            }
        } else {
            mScanResultCache.clear();
            for (ScanResult newResult : newResults) {
                if (newResult.SSID == null || newResult.SSID.isEmpty()) {
                    continue;
                }
                mScanResultCache.put(newResult.BSSID, newResult);
            }
        }
        ArrayMap<String, List<ScanResult>> scanResultsByApKey = new ArrayMap<>();
        for (ScanResult result : mScanResultCache.values()) {
            // Ignore hidden and ad-hoc networks.
            if (result.SSID == null || result.SSID.length() == 0 ||
                    result.capabilities.contains("[IBSS]")) {
                continue;
            }

            String apKey = WifiAccessPoint.getKey(result);
            List<ScanResult> resultList;
            if (scanResultsByApKey.containsKey(apKey)) {
                resultList = scanResultsByApKey.get(apKey);
            } else {
                resultList = new ArrayList<>();
                scanResultsByApKey.put(apKey, resultList);
            }

            resultList.add(result);
        }

        return scanResultsByApKey;
    }

    private void updateWifiState(int state) {
        if (state == WifiManager.WIFI_STATE_ENABLED) {
            if (mScanner != null) {
                mScanner.resume();
            }
        } else {
            clearAccessPointsAndConditionallyUpdate();
            mLastInfo = null;
            mLastNetworkInfo = null;
            if (mScanner != null) {
                mScanner.pause();
            }
            mStaleScanResults = true;
        }
        mListener.onWifiStateChanged(state);
    }

    public List<WifiAccessPoint> getAccessPoints() {
        synchronized (mLock) {
            return new ArrayList<WifiAccessPoint>(mInternalAccessPoints);
        }
    }

    WifiAccessPoint getCachedOrCreate(List<ScanResult> scanResults, List<WifiAccessPoint> cache) {
        final int N = cache.size();
        for (int i = 0; i < N; i++) {
            if (cache.get(i).getKey().equals(WifiAccessPoint.getKey(scanResults.get(0)))) {
                WifiAccessPoint ret = cache.remove(i);
                ret.setScanResults(scanResults);
                return ret;
            }
        }
        final WifiAccessPoint accessPoint = new WifiAccessPoint(mContext, scanResults);
        return accessPoint;
    }

    public WifiManager getManager() {
        return mWifiManager;
    }

    public boolean isConnected() {
        return mConnected.get();
    }

    public boolean isWifiEnabled() {
        return mWifiManager.isWifiEnabled();
    }

    public void onDestroy(LifecycleOwner paramLifecycleOwner) {
        mWorkThread.quit();
        // DefaultLifecycleObserver.-CC.$default$onDestroy(this, paramLifecycleOwner);
        DefaultLifecycleObserver.super.onDestroy(paramLifecycleOwner);
    }

    public void onStart(LifecycleOwner paramLifecycleOwner) {
        forceUpdate();
        resumeScanning();
        if (!mRegistered) {
            mContext.registerReceiver(mReceiver, mFilter, null, mWorkHandler);
            if (Build.VERSION.SDK_INT >= 26) {
                mNetworkCallback = new WifiTrackerNetworkCallback();
                mConnectivityManager.registerNetworkCallback(mNetworkRequest, mNetworkCallback, mWorkHandler);
            }
            mRegistered = true;
        }
    }

    public void onStop(LifecycleOwner paramLifecycleOwner) {
        if (mRegistered) {
            mContext.unregisterReceiver(mReceiver);
            if (Build.VERSION.SDK_INT >= 26) {
                mConnectivityManager.unregisterNetworkCallback(mNetworkCallback);
            }
            mRegistered = false;
        }
        pauseScanning();
        mWorkHandler.removeCallbacksAndMessages(null);
        //DefaultLifecycleObserver.-CC.$default$onStop(this, paramLifecycleOwner);
        DefaultLifecycleObserver.super.onStop(paramLifecycleOwner);
    }

    public void resumeScanning() {
        if (mScanner == null) {
            mScanner = new Scanner();
        }
        if (mWifiManager.isWifiEnabled()) {
            mScanner.resume();
        }
    }

    void setWorkThread(HandlerThread workThread) {
        mWorkThread = workThread;
        mWorkHandler = new Handler(workThread.getLooper());
    }

    class Scanner
            extends Handler {
        static final int MSG_SCAN = 0;
        private int mRetry = 0;

        public void handleMessage(Message message) {
            Log.d(TAG,"Scanner  handleMessage  "+message);
            if (message.what != MSG_SCAN) {
                return;
            }
            if (mWifiManager.startScan()) {
                mRetry = 0;
            } else if (++mRetry >= 3){
                mRetry = 0;
                Log.d(TAG, WifiTrackerRepository.this.mContext.getResources().getString(R.string.wifi_fail_to_scan));
                return;
            }
            sendEmptyMessageDelayed(MSG_SCAN, WIFI_RESCAN_INTERVAL_MS);
        }

        boolean isScanning() {
            return hasMessages(MSG_SCAN);
        }

        void pause() {
            mRetry = 0;
            removeMessages(MSG_SCAN);
        }

        void resume() {
            if (!hasMessages(MSG_SCAN)) {
                sendEmptyMessage(MSG_SCAN);
            }
        }
    }

    public  interface WifiListener {
         void onAccessPointsChanged();

         void onConnectedChanged();

         void onWifiStateChanged(int paramInt);
    }

    class WifiListenerExecutor
            implements WifiListener {
        private final WifiListener mDelegatee;

        public WifiListenerExecutor(WifiListener listener) {
            mDelegatee = listener;
        }

        private void runAndLog(Runnable r, String verboseLog) {
            ThreadUtils.postOnMainThread(() -> {
                if (mRegistered) {
                    //if (isVerboseLoggingEnabled()) {
                        Log.i(TAG, verboseLog);
                    //}
                    r.run();
                }
            });
        }

        public void onAccessPointsChanged() {
            runAndLog(mDelegatee::onAccessPointsChanged, "Invoking onAccessPointsChanged callback");
        }

        public void onConnectedChanged() {
            runAndLog(mDelegatee::onConnectedChanged, "Invoking onConnectedChanged callback");
        }

        public void onWifiStateChanged(int state) {
            runAndLog(() -> mDelegatee.onWifiStateChanged(state),
                   String.format("Invoking onWifiStateChanged callback with state %d", state));
        }
    }

    private final class WifiTrackerNetworkCallback
            extends ConnectivityManager.NetworkCallback {
        public void onCapabilitiesChanged(Network network, NetworkCapabilities nc) {
            if (Build.VERSION.SDK_INT >= 23) {
                try {
                    if (network.equals(WifiManager.class.getDeclaredMethod("getCurrentNetwork", new Class[0]).invoke(WifiTrackerRepository.this.mWifiManager, new Object[0]))) {
                        updateNetworkInfo(null);
                    }
                } catch (Exception e) {
                    HLog.d(WifiTrackerRepository.TAG, "In WifiTrackerNetworkCallback, WifiManager.getCurrentNetwork() was catch an exception.");
                    e.printStackTrace();
                }
            }
        }
    }
}

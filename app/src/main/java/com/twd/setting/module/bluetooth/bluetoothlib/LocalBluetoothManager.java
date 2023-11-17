package com.twd.setting.module.bluetooth.bluetoothlib;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothUuid;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanFilter.Builder;
import android.bluetooth.le.ScanSettings;
//import android.bluetooth.le.ScanSettings.Builder;
import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.UserHandle;
import android.util.Log;

import com.twd.setting.R;
import com.twd.setting.utils.BluetoothUtils;
//import com.twd.setting.utils.HLog;
import java.util.ArrayList;
import java.util.List;

public class LocalBluetoothManager {
    private static final String _TAG = "LocalBluetoothManager";
    private static LocalBluetoothManager sInstance;
    private BluetoothLeScanner bleScanner;
    private final BluetoothCallbackHelper callbackHelper;
    private final CachedBluetoothDeviceManager mCachedDeviceManager;
    private final Context mContext;
    private final BluetoothEventManager mEventManager;
    private boolean mLeScanning = false;
    private final BluetoothAdapter mLocalAdapter;
    private final LocalBluetoothProfileManager mProfileManager;

    private LocalBluetoothManager(BluetoothAdapter paramBluetoothAdapter, Context paramContext, Handler paramHandler, UserHandle paramUserHandle) {
        Context localContext = paramContext.getApplicationContext();
        mContext = paramContext.getApplicationContext();
        ;
        BluetoothHandsetFilter.REMOTE_CONTROL_NAME = paramContext.getString(R.string.str_remote_control_name);
        mLocalAdapter = paramBluetoothAdapter;
        //paramContext = new CachedBluetoothDeviceManager(this);
        mCachedDeviceManager = new CachedBluetoothDeviceManager(this);
        mEventManager = new BluetoothEventManager(mLocalAdapter, mCachedDeviceManager, localContext, (BluetoothEventManager.Handler) paramHandler, paramUserHandle);
        //this.mEventManager = paramBluetoothAdapter;
        mProfileManager = new LocalBluetoothProfileManager(localContext, mEventManager, mCachedDeviceManager);
        //this.mProfileManager = paramHandler;
        callbackHelper = new BluetoothCallbackHelper(mProfileManager, mCachedDeviceManager);
        //this.callbackHelper = paramContext;
        mProfileManager.updateLocalProfiles();
        mEventManager.registerCallback(callbackHelper.getBluetoothCallback());
        mProfileManager.addServiceListener(callbackHelper.getProfileServiceListener());
    }

    public static LocalBluetoothManager getInstance(Context paramContext, BluetoothManagerCallback paramBluetoothManagerCallback) {
        try {
            if (sInstance == null) {
                BluetoothAdapter localBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (localBluetoothAdapter == null) {
                    return null;
                }
                sInstance = new LocalBluetoothManager(localBluetoothAdapter, paramContext, null, null);
                if (paramBluetoothManagerCallback != null) {
                    paramBluetoothManagerCallback.onBluetoothManagerInitialized(paramContext.getApplicationContext(), sInstance);
                }
            }
            return sInstance;
        } catch (Exception e) {
        }

        return null;
    }

    private List<ScanFilter> getScanFilter() {
        ArrayList localArrayList = new ArrayList();
        //   localArrayList.add(new ScanFilter.Builder().setServiceUuid(BluetoothUuid.Hogp).build());
        //  localArrayList.add(new ScanFilter.Builder().setServiceUuid(BluetoothUuid.Hid).build());
        return localArrayList;
    }

    private ScanSettings getScanSettings() {
        ScanSettings.Builder localBuilder = new ScanSettings.Builder().setScanMode(2);
        if (Build.VERSION.SDK_INT >= 23) {
            localBuilder.setCallbackType(1);
            localBuilder.setMatchMode(2);
        }
        if (mLocalAdapter.isOffloadedScanBatchingSupported()) {
            localBuilder.setReportDelay(0L);
        }
        return localBuilder.build();
    }

    private boolean isSupportBleMode() {
        return (Build.VERSION.SDK_INT >= 23) && (BluetoothUtils.isSupportBluetoothLE(this.mContext));
    }

    @SuppressLint("MissingPermission")
    public boolean disableBluetooth() {
        return mLocalAdapter.disable();
    }

    @SuppressLint("MissingPermission")
    public boolean enableBluetooth() {
        return mLocalAdapter.enable();
    }

    public CachedBluetoothDeviceManager getCachedDeviceManager() {
        return mCachedDeviceManager;
    }

    public Context getContext() {
        return mContext;
    }

    public BluetoothEventManager getEventManager() {
        return mEventManager;
    }

    public BluetoothAdapter getLocalAdapter() {
        return mLocalAdapter;
    }

    public LocalBluetoothProfileManager getProfileManager() {
        return mProfileManager;
    }

    public boolean isAdapterEnabled() {
        return (mLocalAdapter != null) && (mLocalAdapter.isEnabled());
    }

    @SuppressLint("MissingPermission")
    public boolean isDiscovering(boolean paramBoolean) {
        if ((paramBoolean) && (isSupportBleMode())) {
            return mLeScanning;
        } else {
            return mLocalAdapter.isDiscovering();
        }
    }

    public void onDestroy() {
        stopScanning(true);
        stopScanning(false);
        mEventManager.unregisterCallback(callbackHelper.getBluetoothCallback());
        mProfileManager.removeServiceListener(callbackHelper.getProfileServiceListener());
        mEventManager.clear();
        callbackHelper.clear();
        sInstance = null;
    }

    public void registerResultCallback(OnBluetoothResultCallback paramOnBluetoothResultCallback) {
        this.callbackHelper.registerResultCallback(paramOnBluetoothResultCallback);
    }

    @SuppressLint("MissingPermission")
    public boolean startScanning(boolean paramBoolean) {
        if ((paramBoolean) && (isSupportBleMode())) {
            if (Build.VERSION.SDK_INT >= 21) {
                if (bleScanner == null) {
                    bleScanner = mLocalAdapter.getBluetoothLeScanner();
                }
                mLeScanning = true;
                bleScanner.startScan(getScanFilter(), getScanSettings(), mEventManager.getLeScanCallbackHandler21());
                StringBuilder str = new StringBuilder();
                str.append("le 开启扫描了 api21  mLeScanning=");
                str.append(this.mLeScanning);
                Log.d("LocalBluetoothManager", str.toString());
                if (mLeScanning) {
                    if (callbackHelper != null) {
                        callbackHelper.scanningStateChanged(true);
                    }
                }
                return true;
            }
            mLeScanning = mLocalAdapter.startLeScan(mEventManager.getLeScanCallbackHandler());
            Object localObject = new StringBuilder();
            ((StringBuilder) localObject).append("le 开启扫描了 mLeScanning=");
            ((StringBuilder) localObject).append(this.mLeScanning);
            Log.d("LocalBluetoothManager", ((StringBuilder) localObject).toString());
            if (mLeScanning) {
                if (callbackHelper != null) {
                    callbackHelper.scanningStateChanged(true);
                }
            }
            return mLeScanning;
        }
        if (!mLocalAdapter.isDiscovering()) {
            return mLocalAdapter.startDiscovery();
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    public void stopScanning(boolean paramBoolean) {
        Object localObject = this.mLocalAdapter;
        if (mLocalAdapter != null) {
            if (!mLocalAdapter.isEnabled()) {
                return;
            }
            if ((paramBoolean) && (isSupportBleMode())) {
                if (Build.VERSION.SDK_INT >= 21) {
                    if (bleScanner != null) {
                        StringBuilder str = new StringBuilder();
                        str.append("le 停止扫描了 21  mLeScanning=");
                        str.append(this.mLeScanning);
                        Log.d("LocalBluetoothManager", str.toString());
                        bleScanner.stopScan(mEventManager.getLeScanCallbackHandler21());
                    }
                    if (mLeScanning) {
                        mLeScanning = false;
                        if (callbackHelper != null) {
                            callbackHelper.scanningStateChanged(false);
                        }
                    }
                } else {
                    StringBuilder str = new StringBuilder();
                    str.append("le 停止扫描了  mLeScanning=");
                    str.append(this.mLeScanning);
                    Log.d("LocalBluetoothManager", str.toString());
                    mLocalAdapter.stopLeScan(mEventManager.getLeScanCallbackHandler());
                    if (mLeScanning) {
                        mLeScanning = false;
                        if (callbackHelper != null) {
                            callbackHelper.scanningStateChanged(false);
                        }
                    }
                }
                return;
            }
            if (mLocalAdapter.isDiscovering()) {
                mLocalAdapter.cancelDiscovery();
            }
        }
    }

    public void unregisterResultCallback(OnBluetoothResultCallback paramOnBluetoothResultCallback) {
        this.callbackHelper.unregisterResultCallback(paramOnBluetoothResultCallback);
    }

    public static abstract interface BluetoothManagerCallback {
        public abstract void onBluetoothManagerInitialized(Context paramContext, LocalBluetoothManager paramLocalBluetoothManager);
    }
}

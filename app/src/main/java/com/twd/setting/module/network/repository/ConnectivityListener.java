package com.twd.setting.module.network.repository;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
//import android.net.EthernetManager;
import android.net.LinkAddress;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiSsid;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.DefaultLifecycleObserver;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.twd.setting.module.network.model.WifiAccessPoint;
//import com.twd.setting.utils.HLog;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class ConnectivityListener
        implements WifiTrackerRepository.WifiListener, DefaultLifecycleObserver {
    private static final String ETH0_CARRIER_PATH = "/sys/class/net/eth0/carrier";
    private static final String LOG_TAG = "ConnectivityListener";
    public static int TYPE_NONE;
    private final ConnectivityManager mConnectivityManager;
    private final Context mContext;
    // private EthernetManager mEthernetManager;
    private final Listener mListener;
    private final BroadcastReceiver mNetworkReceiver;
    private int mNetworkType;
    private boolean mStarted;
    private WifiNetworkListener mWifiListener;
    private final WifiManager mWifiManager;
    private int mWifiSignalStrength;
    private String mWifiSsid;
    private WifiTrackerRepository mWifiTrackerRepository;

    @Deprecated
    public ConnectivityListener(Context paramContext, Listener paramListener) {
        this(paramContext, paramListener, null);
    }

    public ConnectivityListener(Context paramContext, Listener paramListener, Lifecycle paramLifecycle) {
        try {
            TYPE_NONE = ((Integer) ConnectivityManager.class.getField("TYPE_NONE").get(ConnectivityManager.class)).intValue();
        } catch (IllegalAccessException illegalAccessException) {
        } catch (NoSuchFieldException noSuchFieldException) {
            TYPE_NONE = -1;
            noSuchFieldException.printStackTrace();
        }

        mNetworkReceiver = new BroadcastReceiver() {
            public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent) {
                Log.d("ConnectivityListener", "mNetworkReceiver receives broadcast"+paramAnonymousIntent);
                if (("android.net.wifi.WIFI_STATE_CHANGED".equals(paramAnonymousIntent.getAction()))|| ("android.net.wifi.STATE_CHANGED".equals(paramAnonymousIntent.getAction()))) {
                    int i = paramAnonymousIntent.getIntExtra("wifi_state", 4);
                    Log.d("ConnectivityListener", "mNetworkReceiver receives broadcast  wifi_state:"+i);
                    if(i==0){
                        Log.d("ConnectivityListener", "Wi-Fi正在关闭中");
                    } else if (i==1) {
                        Log.d("ConnectivityListener", "Wi-Fi已关闭");
                    } else if (i==2) {
                        Log.d("ConnectivityListener", "Wi-Fi正在启用中");
                    } else if (i==3) {
                        Log.d("ConnectivityListener", "Wi-Fi已启用");
                    }else {
                        Log.d("ConnectivityListener", "其他状态");
                    }
                }else if("android.net.conn.CONNECTIVITY_CHANGE".equals(paramAnonymousIntent.getAction())){
                    Log.d("ConnectivityListener", "android.net.conn.CONNECTIVITY_CHANGE");
                }
                ConnectivityListener.this.updateConnectivityStatus();
                if (mListener != null) {
                    mListener.onConnectivityChange();
                }
            }
        };
        mContext = paramContext;
        mConnectivityManager = ((ConnectivityManager) paramContext.getSystemService(Context.CONNECTIVITY_SERVICE));
        mWifiManager = ((WifiManager) paramContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE));

        mListener = paramListener;
        if (paramLifecycle != null) {
            mWifiTrackerRepository = new WifiTrackerRepository(mContext, this, paramLifecycle, true, true);
            paramLifecycle.addObserver(this);
        }else {
            mWifiTrackerRepository = new WifiTrackerRepository(mContext, this, true, true);
        }
    }

    private Network getFirstEthernet() {
        Network[] arrayOfNetwork = this.mConnectivityManager.getAllNetworks();
        int j = arrayOfNetwork.length;
        int i = 0;
        while (i < j) {
            Network localNetwork = arrayOfNetwork[i];
            NetworkInfo localNetworkInfo = this.mConnectivityManager.getNetworkInfo(localNetwork);
            if ((localNetworkInfo != null) && (localNetworkInfo.getType() == 9)) {
                return localNetwork;
            }
            i += 1;
        }
        return null;
    }

    private void updateConnectivityStatus() {
        NetworkInfo networkInfo = this.mConnectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            mNetworkType = TYPE_NONE;
            return;
        }
        int type =  networkInfo.getType();
        int i = 0;
        if(type == 0){
            mNetworkType = 0;
        } else if (type == 1) {
            mNetworkType = 1;
            String ssid = getSsid();
            if (!TextUtils.equals(mWifiSsid,  ssid)) {
                mWifiSsid =  ssid;
            }
            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                i = WifiManager.calculateSignalLevel( wifiInfo.getRssi(), 4);
            }
            if (mWifiSignalStrength != i) {
                mWifiSignalStrength = i;
            }
        } else if (type == 9) {
            mNetworkType = 9;
        }else {
            mNetworkType = TYPE_NONE;
        }
    }

    @Deprecated
    public void destroy() {
        mWifiTrackerRepository.onDestroy(null);
    }

    public List<WifiAccessPoint> getAvailableNetworks() {
        List<WifiAccessPoint> list = mWifiTrackerRepository.getAccessPoints();
        for(int i = 0;i< list.size();i++){
            WifiAccessPoint point = (WifiAccessPoint) list.get(i);
   //         Log.d("TAG","SSID:"+point.getSsid()+",state:"+(point.getNetworkInfo()==null ?"null":point.getNetworkInfo().getState()));
        }
        return mWifiTrackerRepository.getAccessPoints();
    }


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public String getSsid() {
        WifiInfo wifiinfo = mWifiManager.getConnectionInfo();
        String ssid = "<unknown ssid>";
        if (wifiinfo != null) {
            try {
                WifiSsid wifiSsid = (WifiSsid) WifiInfo.class.getDeclaredMethod("getWifiSsid", new Class[0]).invoke(wifiinfo, new Object[0]);
                if (wifiSsid != null) {
                    ssid = wifiSsid.toString();
                    if (!TextUtils.isEmpty(ssid)) {
                        StringBuilder localStringBuilder = new StringBuilder();
                        localStringBuilder.append("\"");
                        localStringBuilder.append(ssid);
                        localStringBuilder.append("\"");
                        String str = localStringBuilder.toString();
                        if ((!TextUtils.equals(str, "<unknown ssid>")) && (!TextUtils.isEmpty(str))) {
                            if (str != null) {
                                return WifiAccessPoint.removeDoubleQuotes(str);
                            }
                        } else {
                            return "";
                        }
                    }
                }
                return  ssid;
            } catch (IllegalAccessException localIllegalAccessException) {
            } catch (InvocationTargetException localInvocationTargetException) {
            } catch (NoSuchMethodException localNoSuchMethodException) {
                ssid =  wifiinfo.getSSID();
                localNoSuchMethodException.printStackTrace();
            }

        }
        return ssid;
        //return null;
    }

    public String getWifiIpAddress() {
        if (isWifiConnected()) {
            int i = mWifiManager.getConnectionInfo().getIpAddress();
            return String.format(Locale.US, "%d.%d.%d.%d", new Object[]{Integer.valueOf(i & 0xFF), Integer.valueOf(i >> 8 & 0xFF), Integer.valueOf(i >> 16 & 0xFF), Integer.valueOf(i >> 24 & 0xFF)});
        }
        return "";
    }

    @SuppressLint("HardwareIds")
    public String getWifiMacAddress() {
        if (isWifiConnected()) {
            return mWifiManager.getConnectionInfo().getMacAddress();
        }
        return "";
    }

    public int getWifiSignalStrength(int paramInt) {
        return WifiManager.calculateSignalLevel(mWifiManager.getConnectionInfo().getRssi(), paramInt);
    }

    public boolean isCellConnected() {
        return mNetworkType == 0;
    }

    public boolean isWifiConnected() {
        return mNetworkType == 1;
    }

    public boolean isWifiEnabledOrEnabling() {
        return (mWifiManager.getWifiState() == 3) || (mWifiManager.getWifiState() == 2);
    }

    public void onAccessPointsChanged() {
        Log.d(LOG_TAG,"onAccessPointsChanged");
        if (mWifiListener != null) {
            mWifiListener.onWifiListChanged();
        }
    }

    public void onConnectedChanged() {
        Log.d(LOG_TAG,"onConnectedChanged");
        updateConnectivityStatus();
        if (mListener != null) {
            mListener.onConnectivityChange();
        }else{
            Log.d(LOG_TAG,"mListener is null");
        }
    }

    public void onDestroy(LifecycleOwner paramLifecycleOwner) {
        mWifiListener = null;
        //DefaultLifecycleObserver. - CC.$default$onDestroy(this, paramLifecycleOwner);
        DefaultLifecycleObserver.super.onDestroy(paramLifecycleOwner);
    }

    public void onStart(LifecycleOwner paramLifecycleOwner) {
        if (!mStarted) {
            mStarted = true;
            updateConnectivityStatus();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.net.wifi.STATE_CHANGE");
            intentFilter.addAction("android.net.wifi.RSSI_CHANGED");
            intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            mContext.registerReceiver(mNetworkReceiver, intentFilter);
        }
    }

    public void onStop(LifecycleOwner paramLifecycleOwner) {
        if (mStarted) {
            mStarted = false;
            mContext.unregisterReceiver(mNetworkReceiver);
        }
    }

    public void onWifiStateChanged(int paramInt) {
        updateConnectivityStatus();
        if (mListener != null) {
            mListener.onConnectivityChange();
        }
    }

    public void setWifiEnabled(boolean paramBoolean) {
        mWifiManager.setWifiEnabled(paramBoolean);
    }

    public void setWifiListener(WifiNetworkListener paramWifiNetworkListener) {
        mWifiListener = paramWifiNetworkListener;
    }

    @Deprecated
    public void start() {
        if (!mStarted) {
            mWifiTrackerRepository.onStart(null);
        }
        onStart(null);
    }

    @Deprecated
    public void stop() {
        if (mStarted) {
            mWifiTrackerRepository.onStop(null);
        }
        onStop(null);
    }

    public  interface Listener {
         void onConnectivityChange();
    }

    public  interface WifiNetworkListener {
         void onWifiListChanged();
    }
}

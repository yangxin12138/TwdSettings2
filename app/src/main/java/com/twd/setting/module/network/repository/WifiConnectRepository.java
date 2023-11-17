package com.twd.setting.module.network.repository;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.NetworkRequest;
import android.net.NetworkRequest.Builder;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Log;
//import com.twd.setting.utils.HLog;
import com.twd.setting.utils.HLog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

public class WifiConnectRepository {
    private final String TAG = "WifiConnectRepository";
    private WifiInfo connectedWifiInfo;
    private String connectingSSID;
    private final WifiManager wifiManager;

    public WifiConnectRepository(Context paramContext, final INetworkStateChange paramINetworkStateChange) {
        wifiManager = ((WifiManager) paramContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE));
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG,"WifiConnectRepository  onReceive:");
                NetworkInfo networkInfo = intent.getParcelableExtra("networkInfo");
                if (networkInfo != null) {
                    String type = intent.getType();
                    //if(type != 1){
                    //    return;
                    //}
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTING) {
                        if (connectedWifiInfo != null) {
                            //         type = 1;
                        } else {
                            //        type = 0;
                        }
                        String ssid = wifiInfo.getSSID().substring(1, wifiInfo.getSSID().length() - 1);
                        int j = 0;
                        if (connectingSSID == null) {
                            if (!TextUtils.equals("<unknown ssid>", wifiInfo.getSSID())) {
                                j = 0;
                                if (!TextUtils.isEmpty(wifiInfo.getSSID())) {
                                    j = 1;
                                }
                            }
                        } else {
                            boolean bool = TextUtils.equals(ssid, connectingSSID);
                            //if ((j == 0) && (!(bool ^ true))) {
                            //    if (i != 0) {
                            paramINetworkStateChange.onNetworkStateChange(networkInfo.getState());
                            //    }
                            //}
                        }
                    }
                }
            }
        };
 /*       Object localObject = new BroadcastReceiver() {
            public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent) {
                paramAnonymousIntent = (NetworkInfo) paramAnonymousIntent.getParcelableExtra("networkInfo");
                if (paramAnonymousIntent != null) {
                    int i = paramAnonymousIntent.getType();
                    int k = 1;
                    if (i != 1) {
                        return;
                    }
                    paramAnonymousContext = WifiConnectRepository.this.wifiManager.getConnectionInfo();
                    paramAnonymousIntent = paramAnonymousIntent.getState();
                    if (paramAnonymousIntent == NetworkInfo.State.CONNECTING) {
                        Object localObject = WifiConnectRepository.this.connectedWifiInfo;
                        int m = 0;
                        if (localObject != null) {
                            WifiConnectRepository.access$102(WifiConnectRepository.this, null);
                            i = 1;
                        } else {
                            i = 0;
                        }
                        localObject = paramAnonymousContext.getSSID().substring(1, paramAnonymousContext.getSSID().length() - 1);
                        int j = m;
                        if (WifiConnectRepository.this.connectingSSID == null) {
                            j = m;
                            if (!TextUtils.equals("<unknown ssid>", paramAnonymousContext.getSSID())) {
                                j = m;
                                if (!TextUtils.isEmpty(paramAnonymousContext.getSSID())) {
                                    j = 1;
                                }
                            }
                        }
                        boolean bool = TextUtils.equals((CharSequence) localObject, WifiConnectRepository.this.connectingSSID);
                        if ((j == 0) && (!(bool ^ true))) {
                            break label195;
                        }
                        WifiConnectRepository.access$202(WifiConnectRepository.this, (String) localObject);
                        i = k;
                        label195:
                        if (i != 0) {
                            paramINetworkStateChange.onNetworkStateChange(paramAnonymousIntent);
                        }
                    }
                }
            }
        };

  */
        paramContext.getApplicationContext().registerReceiver(broadcastReceiver, new IntentFilter("android.net.wifi.STATE_CHANGE"));
        if (Build.VERSION.SDK_INT >= 23) {
            NetworkRequest networkRequest = new NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).addTransportType(NetworkCapabilities.TRANSPORT_WIFI).addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR).build();

            NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    Log.d(TAG,"ConnectivityManager.NetworkCallback  onAvailable");
                    WifiInfo wifiInfo = WifiConnectRepository.this.wifiManager.getConnectionInfo();
                    int m = 0;
                    int k = 1;
                    int i;
                    if ((connectedWifiInfo == null) && (!TextUtils.equals("<unknown ssid>", wifiInfo.getSSID())) && (!TextUtils.isEmpty(wifiInfo.getSSID()))) {
                        i = 1;
                    } else {
                        i = 0;
                    }
                    int j;
                    if ((connectedWifiInfo != null) && (!TextUtils.equals(wifiInfo.getSSID(), connectedWifiInfo.getSSID()))) {
                        j = 1;
                    } else {
                        j = 0;
                    }
                    if (i == 0) {
                        i = m;
                        if (j == 0) {
                        }
                    } else {
                        //    WifiConnectRepository.access$102(WifiConnectRepository.this, paramAnonymousNetwork);
                        i = 1;
                    }
                    if (WifiConnectRepository.this.connectingSSID != null) {
                        //    WifiConnectRepository.access$202(WifiConnectRepository.this, null);
                        i = k;
                    }
                    if (i != 0) {
                        paramINetworkStateChange.onNetworkStateChange(NetworkInfo.State.DISCONNECTED);
                    }
                }

                public void onCapabilitiesChanged(Network paramAnonymousNetwork, NetworkCapabilities paramAnonymousNetworkCapabilities) {
                    super.onCapabilitiesChanged(paramAnonymousNetwork, paramAnonymousNetworkCapabilities);
                    Log.d(TAG,"ConnectivityManager.NetworkCallback  onCapabilitiesChanged");
                }

                public void onLost(Network paramAnonymousNetwork) {
                    super.onLost(paramAnonymousNetwork);
                    Log.d(TAG,"ConnectivityManager.NetworkCallback  onLost");
                    int i;
                    if (WifiConnectRepository.this.connectedWifiInfo != null) {
                        //    WifiConnectRepository.access$102(WifiConnectRepository.this, null);
                        i = 1;
                    } else {
                        i = 0;
                    }
                    if (i != 0) {
                        paramINetworkStateChange.onNetworkStateChange(NetworkInfo.State.DISCONNECTED);
                    }
                }
            };
            ((ConnectivityManager) paramContext.getApplicationContext().getSystemService(ConnectivityManager.class)).requestNetwork(networkRequest, networkCallback);
        }
    }

    public boolean addNetwork(String paramString1, String paramString2, String paramString3) {
        int i;
        if (forgetWifi(paramString1)) {
            i = wifiManager.addNetwork(createWifiInfo(paramString1, paramString2, paramString3));
        } else if (getExitsWifiConfig(paramString1) != null) {
            i = getExitsWifiConfig(paramString1).networkId;
        } else {
            i = wifiManager.addNetwork(createWifiInfo(paramString1, paramString2, paramString3));
        }
        return wifiManager.enableNetwork(i, true);
    }

    @SuppressLint("MissingPermission")
    public int connectWiFi(ScanResult paramScanResult, String paramString) {
        try {
            Log.d(TAG,"connectWifi "+paramString);
            HLog.d(TAG, "Item clicked, SSID "+paramScanResult.SSID+" Security : "+paramScanResult.capabilities);

            WifiConfiguration wifiConfiguration = new WifiConfiguration();
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("\"");
            localStringBuilder.append(paramScanResult.SSID);
            localStringBuilder.append("\"");
            wifiConfiguration.SSID = localStringBuilder.toString();
            wifiConfiguration.status = 2;
            wifiConfiguration.priority = 40;
            if (paramScanResult.capabilities.toUpperCase().contains("WEP")) {
                Log.d(TAG, "Configuring WEP");
                wifiConfiguration.allowedKeyManagement.set(0);
                wifiConfiguration.allowedProtocols.set(1);
                wifiConfiguration.allowedProtocols.set(0);
                wifiConfiguration.allowedAuthAlgorithms.set(0);
                wifiConfiguration.allowedAuthAlgorithms.set(1);
                wifiConfiguration.allowedPairwiseCiphers.set(2);
                wifiConfiguration.allowedPairwiseCiphers.set(1);
                wifiConfiguration.allowedGroupCiphers.set(0);
                wifiConfiguration.allowedGroupCiphers.set(1);
                if (paramString.matches("^[0-9a-fA-F]+$")) {
                    wifiConfiguration.wepKeys[0] = paramString;
                } else {
                    wifiConfiguration.wepKeys[0] = "\"".concat(paramString).concat("\"");
                }
                wifiConfiguration.wepTxKeyIndex = 0;
            } else if (paramScanResult.capabilities.toUpperCase().contains("WPA")) {
                Log.d(TAG, "Configuring WPA");
                wifiConfiguration.allowedProtocols.set(1);
                wifiConfiguration.allowedProtocols.set(0);
                wifiConfiguration.allowedKeyManagement.set(1);
                wifiConfiguration.allowedPairwiseCiphers.set(2);
                wifiConfiguration.allowedPairwiseCiphers.set(1);
                wifiConfiguration.allowedGroupCiphers.set(0);
                wifiConfiguration.allowedGroupCiphers.set(1);
                wifiConfiguration.allowedGroupCiphers.set(3);
                wifiConfiguration.allowedGroupCiphers.set(2);
                StringBuilder str = new StringBuilder();
                str.append("\"");
                str.append(paramString);
                str.append("\"");
                wifiConfiguration.preSharedKey = str.toString();
            } else {
                Log.d(TAG, "Configuring OPEN network");
                wifiConfiguration.allowedKeyManagement.set(0);
                wifiConfiguration.allowedProtocols.set(1);
                wifiConfiguration.allowedProtocols.set(0);
                wifiConfiguration.allowedAuthAlgorithms.clear();
                wifiConfiguration.allowedPairwiseCiphers.set(2);
                wifiConfiguration.allowedPairwiseCiphers.set(1);
                wifiConfiguration.allowedGroupCiphers.set(0);
                wifiConfiguration.allowedGroupCiphers.set(1);
                wifiConfiguration.allowedGroupCiphers.set(3);
                wifiConfiguration.allowedGroupCiphers.set(2);
            }
            int i = wifiManager.addNetwork(wifiConfiguration);
            Log.d(TAG, "Add result " + i);
            Iterator iterator = wifiManager.getConfiguredNetworks().iterator();
            while (iterator.hasNext()) {
                WifiConfiguration configuration = (WifiConfiguration) iterator.next();
                if (configuration.SSID != null) {
                    localStringBuilder = new StringBuilder();
                    localStringBuilder.append("\"");
                    localStringBuilder.append(paramScanResult);
                    localStringBuilder.append("\"");
                    if ((configuration.SSID).equals(localStringBuilder.toString())) {
                        Log.d(TAG, "WifiConfiguration SSID " + configuration.SSID);
                        Log.d(TAG, "isDisconnected : " + wifiManager.disconnect());
                        Log.d(TAG, "isEnabled : " + wifiManager.enableNetwork(configuration.networkId, true));
                        Log.d(TAG, "isReconnected : " + wifiManager.reconnect());
                    }
                }
            }
            return i;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public WifiConfiguration createWifiInfo(String paramString1, String paramString2, String paramString3) {
        Log.d(TAG, "createWifiInfo : " + paramString1+","+paramString2+","+paramString3);
        WifiConfiguration localWifiConfiguration = new WifiConfiguration();
        localWifiConfiguration.allowedAuthAlgorithms.clear();
        localWifiConfiguration.allowedGroupCiphers.clear();
        localWifiConfiguration.allowedKeyManagement.clear();
        localWifiConfiguration.allowedPairwiseCiphers.clear();
        localWifiConfiguration.allowedProtocols.clear();
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("\"");
        localStringBuilder.append(paramString1);
        localStringBuilder.append("\"");
        localWifiConfiguration.SSID = localStringBuilder.toString();
        if (TextUtils.equals("open", paramString3)) {
            localWifiConfiguration.hiddenSSID = true;
            localWifiConfiguration.allowedKeyManagement.set(0);
            return localWifiConfiguration;
        }
        if (TextUtils.equals("WEP", paramString3)) {
            localWifiConfiguration.hiddenSSID = true;
            String[] strs = localWifiConfiguration.wepKeys;
            StringBuilder str = new StringBuilder();
            str.append("\"");
            str.append(paramString2);
            str.append("\"");
            strs[0] = str.toString();
            localWifiConfiguration.allowedAuthAlgorithms.set(1);
            localWifiConfiguration.allowedGroupCiphers.set(3);
            localWifiConfiguration.allowedGroupCiphers.set(2);
            localWifiConfiguration.allowedGroupCiphers.set(0);
            localWifiConfiguration.allowedGroupCiphers.set(1);
            localWifiConfiguration.allowedKeyManagement.set(0);
            localWifiConfiguration.wepTxKeyIndex = 0;
            return localWifiConfiguration;
        }
        if (TextUtils.equals("WPA/WPA2 PSK", paramString3)) {
            StringBuilder str = new StringBuilder();
            str.append("\"");
            str.append(paramString2);
            str.append("\"");
            localWifiConfiguration.preSharedKey = str.toString();
            localWifiConfiguration.hiddenSSID = true;
            localWifiConfiguration.allowedAuthAlgorithms.set(0);
            localWifiConfiguration.allowedGroupCiphers.set(2);
            localWifiConfiguration.allowedKeyManagement.set(1);
            localWifiConfiguration.allowedPairwiseCiphers.set(1);
            localWifiConfiguration.allowedGroupCiphers.set(3);
            localWifiConfiguration.allowedPairwiseCiphers.set(2);
            localWifiConfiguration.status = 2;
        }
        return localWifiConfiguration;
    }

    public boolean forgetWifi(String paramString) {
        WifiConfiguration wifiConfiguration = getExitsWifiConfig(paramString);
        if (wifiConfiguration != null) {
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("tempConfig.networkId=");
            localStringBuilder.append(wifiConfiguration.networkId);
            HLog.d("howard", localStringBuilder.toString());
            return wifiManager.removeNetwork(wifiConfiguration.networkId);
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    public List<String> getConfigSSIDs() {
        List<WifiConfiguration> lists = wifiManager.getConfiguredNetworks();
        ArrayList localArrayList = new ArrayList();
        if (lists != null) {
            Iterator iterator = lists.iterator();
            while (iterator.hasNext()) {
                String str = ((WifiConfiguration) iterator.next()).SSID;
                if ((str.length() > 2) && (str.charAt(0) == '"') && (str.charAt(str.length() - 1) == '"')) {
                    localArrayList.add(str.substring(1, str.length() - 1));
                }
            }
        }
        return localArrayList;
    }

    public WifiInfo getConnectedWifiInfo() {
        return connectedWifiInfo;
    }

    public String getConnectingSSID() {
        return connectingSSID;
    }

    @SuppressLint("MissingPermission")
    public WifiConfiguration getExistConfig(String paramString) {
        if ((paramString.length() <= 2) || (paramString.charAt(0) != '"') || (paramString.charAt(paramString.length() - 1) != '"')) {
            StringBuilder localObject = new StringBuilder();
            ((StringBuilder) localObject).append("\"");
            ((StringBuilder) localObject).append(paramString);
            ((StringBuilder) localObject).append("\"");
            paramString = ((StringBuilder) localObject).toString();
        }
        Object localObject = wifiManager.getConfiguredNetworks();
        if ((localObject != null) && (((List) localObject).size() > 0)) {
            localObject = ((List) localObject).iterator();
            while (((Iterator) localObject).hasNext()) {
                WifiConfiguration localWifiConfiguration = (WifiConfiguration) ((Iterator) localObject).next();
                if (TextUtils.equals(localWifiConfiguration.SSID, paramString)) {
                    return localWifiConfiguration;
                }
            }
        }
        return null;
    }

    @SuppressLint("MissingPermission")
    public WifiConfiguration getExitsWifiConfig(String paramString) {
        Iterator localIterator = wifiManager.getConfiguredNetworks().iterator();
        while (localIterator.hasNext()) {
            WifiConfiguration localWifiConfiguration = (WifiConfiguration) localIterator.next();
            String str = localWifiConfiguration.SSID;
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("\"");
            localStringBuilder.append(paramString);
            localStringBuilder.append("\"");
            if (str.equals(localStringBuilder.toString())) {
                return localWifiConfiguration;
            }
        }
        return null;
    }

    public boolean removeConfigured(WifiConfiguration paramWifiConfiguration) {
        if (paramWifiConfiguration != null) {
            return wifiManager.removeNetwork(paramWifiConfiguration.networkId);
        }
        return false;
    }

    public void setConnectedWifiInfo(WifiInfo paramWifiInfo) {
        connectedWifiInfo = paramWifiInfo;
    }

    public void setConnectingSSID(String paramString) {
        connectingSSID = paramString;
    }

    public static abstract interface INetworkStateChange {
        public abstract void onNetworkStateChange(NetworkInfo.State paramState);
    }
}

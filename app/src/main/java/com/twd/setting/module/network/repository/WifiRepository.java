package com.twd.setting.module.network.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WifiRepository {
    private final String LOG_TAG = "WifiRepository";
    private WifiConnectRepository connectRepository;
    private WifiScanRepository scanRepository;
    private WifiManager wifiManager;

    public WifiRepository(Context paramContext, WifiScanRepository paramWifiScanRepository, WifiConnectRepository paramWifiConnectRepository) {
        scanRepository = paramWifiScanRepository;
        connectRepository = paramWifiConnectRepository;
        wifiManager = ((WifiManager) paramContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE));
    }

    public static String getSsidByWifiInfo(WifiInfo paramWifiInfo) {
        if (paramWifiInfo != null) {
            String ssid = paramWifiInfo.getSSID();
            if ((ssid.length() > 2) && (ssid.charAt(0) == '"') && (ssid.charAt(ssid.length() - 1) == '"')) {
                return ssid.substring(1, ssid.length() - 1);
            }
        }
        return "";
    }

    public boolean addNetwork(String paramString1, String paramString2, String paramString3) {
        return connectRepository.addNetwork(paramString1, paramString2, paramString3);
    }

    public int connect(ScanResult paramScanResult, String paramString) {
        return connectRepository.connectWiFi(paramScanResult, paramString);
    }

    public boolean containName(List<ScanResult> paramList, String paramString) {
        Iterator iterator = paramList.iterator();
        while (iterator.hasNext()) {
            ScanResult localScanResult = (ScanResult) iterator.next();
            if ((!TextUtils.isEmpty(localScanResult.SSID)) && (TextUtils.equals(localScanResult.SSID, paramString))) {
                return true;
            }
        }
        return false;
    }

    public List<String> getConfigSSIDs() {
        return connectRepository.getConfigSSIDs();
    }

    public WifiInfo getConnectedWifiInfo() {
        return connectRepository.getConnectedWifiInfo();
    }

    public String getConnectingSSID() {
        return connectRepository.getConnectingSSID();
    }

    public List<ScanResult> getRawScanResults() {
        return scanRepository.getRawScanResults();
    }

    @SuppressLint("MissingPermission")
    public boolean isConfigured(String paramString) {
        Iterator localIterator = wifiManager.getConfiguredNetworks().iterator();
        while (localIterator.hasNext()) {
            String str = ((WifiConfiguration) localIterator.next()).SSID;
            if ((str.length() > 2) && (str.charAt(0) == '"') && (str.charAt(str.length() - 1) == '"') && (TextUtils.equals(str.substring(1, str.length() - 1), paramString))) {
                return true;
            }
        }
        return false;
    }

    public List<ScanResult> refactorScanResults(List<ScanResult> paramList) {
        ArrayList localObject2 = new ArrayList();
        Iterator iterator = paramList.iterator();
        while (iterator.hasNext()) {
            ScanResult scanResult = (ScanResult) iterator.next();
            if ((!TextUtils.isEmpty(scanResult.SSID)) && (!containName(localObject2, scanResult.SSID))) {
                localObject2.add(scanResult);
            }
        }
        String str = getConnectingSSID();
        WifiInfo wifiInfo = getConnectedWifiInfo();
        Object localObject1 = null;
        if (wifiInfo == null) {
            str = null;
        } else {
            str = wifiInfo.getSSID().substring(1, wifiInfo.getSSID().length() - 1);
        }
        ArrayList localArrayList1 = new ArrayList();
        ArrayList localArrayList2 = new ArrayList();
        ArrayList localArrayList0 = new ArrayList();
        Iterator localIterator = ((ArrayList) localObject2).iterator();
        while (localIterator.hasNext()) {
            ScanResult scanResult = (ScanResult) localIterator.next();
            if (/*(TextUtils.equals(scanResult.SSID, paramList)) || */(TextUtils.equals(scanResult.SSID, str))) {
                localArrayList0 = localObject2;
            } else if (isConfigured(scanResult.SSID)) {
                localArrayList1.add(localObject2);
            } else {
                localArrayList2.add(localObject2);
            }
        }
        ArrayList arrayList = new ArrayList();
        if (localObject1 != null) {
            arrayList.add(0, localArrayList0);
        }
        arrayList.addAll(localArrayList1);
        arrayList.addAll(localArrayList2);
        return arrayList;
    }

    public boolean removeConfiguredWifi(String paramString) {
        return connectRepository.removeConfigured(connectRepository.getExistConfig(paramString));
    }

    public void scanWifi() {
        scanRepository.scanWifi();
    }

    public void setConnectedWifiInfo(WifiInfo paramWifiInfo) {
        connectRepository.setConnectedWifiInfo(paramWifiInfo);
    }

    public void setConnectingSSID(String paramString) {
        connectRepository.setConnectingSSID(paramString);
    }
}

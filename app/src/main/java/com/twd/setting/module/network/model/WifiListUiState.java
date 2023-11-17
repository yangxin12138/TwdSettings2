package com.twd.setting.module.network.model;

import android.net.wifi.WifiInfo;

import java.util.List;

public class WifiListUiState {
    private List<String> configuredSSIDs;
    private WifiInfo connectedWifiInfo;
    private String connectingSSID;
    private List<WifiAccessPoint> wifiAccessPoints;

    public List<String> getConfiguredSSIDs() {
        return configuredSSIDs;
    }

    public WifiInfo getConnectedWifiInfo() {
        return connectedWifiInfo;
    }

    public String getConnectingSSID() {
        return connectingSSID;
    }

    public List<WifiAccessPoint> getWifiAccessPoints() {
        return wifiAccessPoints;
    }

    public void setConfiguredSSIDs(List<String> paramList) {
        configuredSSIDs = paramList;
    }

    public void setConnectedWifiInfo(WifiInfo paramWifiInfo) {
        connectedWifiInfo = paramWifiInfo;
    }

    public void setConnectingSSID(String paramString) {
        connectingSSID = paramString;
    }

    public void setWifiAccessPoints(List<WifiAccessPoint> paramList) {
        wifiAccessPoints = paramList;
    }
}

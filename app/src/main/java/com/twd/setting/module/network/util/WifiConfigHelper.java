package com.twd.setting.module.network.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.twd.setting.R;
import com.twd.setting.module.network.model.WifiAccessPoint;

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WifiConfigHelper {
    private static final boolean DEBUG = false;
    private static final Pattern EXCLUSION_PATTERN = Pattern.compile("$|^(\\*)?\\.?[a-zA-Z0-9\\_]+(\\-[a-zA-Z0-9\\_]+)*(\\.[a-zA-Z0-9\\_]+(\\-[a-zA-Z0-9\\_]+)*)*$");
    private static final String EXCLUSION_REGEXP = "$|^(\\*)?\\.?[a-zA-Z0-9\\_]+(\\-[a-zA-Z0-9\\_]+)*(\\.[a-zA-Z0-9\\_]+(\\-[a-zA-Z0-9\\_]+)*)*$";
    private static final String HC = "a-zA-Z0-9\\_";
    private static final Pattern HOSTNAME_PATTERN = Pattern.compile("^$|^[a-zA-Z0-9\\_]+(\\-[a-zA-Z0-9\\_]+)*(\\.[a-zA-Z0-9\\_]+(\\-[a-zA-Z0-9\\_]+)*)*$");
    private static final String HOSTNAME_REGEXP = "^$|^[a-zA-Z0-9\\_]+(\\-[a-zA-Z0-9\\_]+)*(\\.[a-zA-Z0-9\\_]+(\\-[a-zA-Z0-9\\_]+)*)*$";
    private static final String TAG = "WifiConfigHelper";

    public static WifiConfiguration getConfiguration(Context paramContext, String paramString, int paramInt) {
        WifiConfiguration localWifiConfiguration = getFromConfiguredNetworks(paramContext, paramString, paramInt);
        if (localWifiConfiguration == null) {
            localWifiConfiguration = new WifiConfiguration();
            setConfigSsid(localWifiConfiguration, paramString);
            setConfigKeyManagementBySecurity(localWifiConfiguration, paramInt);
        }
        return localWifiConfiguration;
    }

    @SuppressLint("MissingPermission")
    private static WifiConfiguration getFromConfiguredNetworks(Context paramContext, String paramString, int paramInt) {
        List wifiManager = ((WifiManager) paramContext.getSystemService(Context.WIFI_SERVICE)).getConfiguredNetworks();
        if (wifiManager != null) {
            Iterator iterator = wifiManager.iterator();
            while (iterator.hasNext()) {
                WifiConfiguration localWifiConfiguration = (WifiConfiguration) iterator.next();
                if ((localWifiConfiguration != null) && (localWifiConfiguration.SSID != null) && (TextUtils.equals(WifiAccessPoint.removeDoubleQuotes(localWifiConfiguration.SSID), paramString)) && (WifiSecurityUtil.getSecurity(localWifiConfiguration) == paramInt)) {
                    return localWifiConfiguration;
                }
            }
        }
        return null;
    }

    @SuppressLint("MissingPermission")
    public static WifiConfiguration getWifiConfiguration(WifiManager paramWifiManager, int paramInt) {
        List wifiManager = paramWifiManager.getConfiguredNetworks();
        if (wifiManager != null) {
            Iterator iterator = wifiManager.iterator();
            while (iterator.hasNext()) {
                WifiConfiguration localWifiConfiguration = (WifiConfiguration) iterator.next();
                if (localWifiConfiguration.networkId == paramInt) {
                    return localWifiConfiguration;
                }
            }
        }
        return null;
    }

    public static boolean isNetworkSaved(WifiConfiguration paramWifiConfiguration) {
        return (paramWifiConfiguration != null) && (paramWifiConfiguration.networkId > -1);
    }

    public static boolean saveConfiguration(Context paramContext, WifiConfiguration paramWifiConfiguration) {
        if (paramWifiConfiguration == null) {
            return false;
        }
        WifiManager wifiManager = (WifiManager) paramContext.getSystemService(Context.WIFI_SERVICE);
        int i = wifiManager.addNetwork(paramWifiConfiguration);
        if (i == -1) {
            return false;
        }
        if (!wifiManager.enableNetwork(i, false)) {
            return false;
        }
        return wifiManager.saveConfiguration();
    }

    public static void setConfigKeyManagementBySecurity(WifiConfiguration paramWifiConfiguration, int paramInt) {
        paramWifiConfiguration.allowedKeyManagement.clear();
        paramWifiConfiguration.allowedAuthAlgorithms.clear();
        if (paramInt != 0) {
            if (paramInt != 1) {
                if (paramInt != 2) {
                    if (paramInt != 3) {
                        return;
                    }
                    paramWifiConfiguration.allowedKeyManagement.set(2);
                    paramWifiConfiguration.allowedKeyManagement.set(3);
                    return;
                }
                paramWifiConfiguration.allowedKeyManagement.set(1);
                return;
            }
            paramWifiConfiguration.allowedKeyManagement.set(0);
            paramWifiConfiguration.allowedAuthAlgorithms.set(0);
            paramWifiConfiguration.allowedAuthAlgorithms.set(1);
            return;
        }
        paramWifiConfiguration.allowedKeyManagement.set(0);
    }

    public static void setConfigSsid(WifiConfiguration paramWifiConfiguration, String paramString) {
        paramWifiConfiguration.SSID = WifiAccessPoint.convertToQuotedString(paramString);
    }

    public static int validate(String paramString1, String paramString2, String paramString3) {
        Matcher localMatcher = HOSTNAME_PATTERN.matcher(paramString1);
        String[] strs = paramString3.split(",");
        if (!localMatcher.matches()) {
            return R.string.proxy_error_invalid_host;
        }
        int j = strs.length;
        int i = 0;
        while (i < j) {
            String str = strs[i];
            if (!EXCLUSION_PATTERN.matcher(str).matches()) {
                return R.string.proxy_error_invalid_exclusion_list;
            }
            i += 1;
        }
        if ((paramString1.length() > 0) && (paramString2.length() == 0)) {
            return R.string.proxy_error_empty_port;
        }
        if (paramString2.length() > 0) {
            if (paramString1.length() == 0) {
                return R.string.proxy_error_empty_host_set_port;
            }
        }
        try {
            i = Integer.parseInt(paramString2);
            if ((i <= 0) || (i > 65535)) {
                return R.string.proxy_error_invalid_port;
            }
            return 0;
        } catch (NumberFormatException numberFormatException) {

        }
        return R.string.proxy_error_invalid_port;
    }
}

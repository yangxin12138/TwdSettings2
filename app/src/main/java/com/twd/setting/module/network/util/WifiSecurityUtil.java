package com.twd.setting.module.network.util;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;

import com.twd.setting.R;

import java.util.BitSet;

public class WifiSecurityUtil {
    public static String getName(Context paramContext, int paramInt) {
        if (paramInt != 0) {
            if (paramInt != 1) {
                if (paramInt != 2) {
                    if (paramInt != 3) {
                        return null;
                    }
                    return paramContext.getString(R.string.wifi_security_type_eap);
                }
                return paramContext.getString(R.string.wifi_security_type_wpa);
            }
            return paramContext.getString(R.string.wifi_security_type_wep);
        }
        return paramContext.getString(R.string.wifi_security_type_none);
    }

    public static int getSecurity(ScanResult paramScanResult) {
        if (paramScanResult.capabilities.contains("WEP")) {
            return 1;
        }
        if (paramScanResult.capabilities.contains("PSK")) {
            return 2;
        }
        if (paramScanResult.capabilities.contains("EAP")) {
            return 3;
        }
        return 0;
    }

    public static int getSecurity(WifiConfiguration paramWifiConfiguration) {
        if (paramWifiConfiguration.allowedKeyManagement.get(1)) {
            return 2;
        }
        if (!paramWifiConfiguration.allowedKeyManagement.get(2)) {
            if (paramWifiConfiguration.allowedKeyManagement.get(3)) {
                return 3;
            }
            if (paramWifiConfiguration.wepKeys[0] != null) {
                return 1;
            }
            return 0;
        }
        return 3;
    }

    public static boolean isOpen(int paramInt) {
        return paramInt == 0;
    }
}

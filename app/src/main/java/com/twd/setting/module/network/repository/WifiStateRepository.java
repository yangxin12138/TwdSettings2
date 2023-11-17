package com.twd.setting.module.network.repository;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.twd.setting.utils.HLog;
//import com.twd.setting.utils.HLog;

public class WifiStateRepository {
    private final String LOG_TAG = "WifiStateRepository";
    private WifiManager wifiManager;

    public WifiStateRepository(Context paramContext, final IWifiStateChange paramIWifiStateChange) {
        this.wifiManager = ((WifiManager) paramContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE));
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent) {
                int i = paramAnonymousIntent.getIntExtra("wifi_state", -1);
                if (i != -1) {
                    paramIWifiStateChange.onWifiStateChange(i);
                }
                if (i != 0) {
                    if (i != 1) {
                        if (i != 2) {
                            if (i != 3) {
                                if (i != 4) {
                                    return;
                                }
                                HLog.d(WifiStateRepository.this.LOG_TAG, "wifi state --> unknown");
                                return;
                            }
                            HLog.d(WifiStateRepository.this.LOG_TAG, "wifi state --> enabled");
                            return;
                        }
                        HLog.d(WifiStateRepository.this.LOG_TAG, "wifi state --> enabling");
                        return;
                    }
                    HLog.d(WifiStateRepository.this.LOG_TAG, "wifi state --> disabled");
                    return;
                }
                HLog.d(WifiStateRepository.this.LOG_TAG, "wifi state --> disabling");
            }
        };
        paramContext.getApplicationContext().registerReceiver(broadcastReceiver, new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED"));
    }

    public void disableWifi() {
        if (this.wifiManager.isWifiEnabled()) {
            this.wifiManager.setWifiEnabled(false);
            return;
        }
        if (this.wifiManager.getWifiState() == 0) {
            HLog.d(this.LOG_TAG, "wifi 正在关闭中，请稍后...");
            return;
        }
        if (this.wifiManager.getWifiState() == 1) {
            HLog.d(this.LOG_TAG, "wifi 已经关闭");
        }
    }

    public void enableWifi() {
        if (!this.wifiManager.isWifiEnabled()) {
            this.wifiManager.setWifiEnabled(true);
            return;
        }
        if (this.wifiManager.getWifiState() == 2) {
            HLog.d(this.LOG_TAG, "wifi 正在打开中，请稍后...");
            return;
        }
        if (this.wifiManager.getWifiState() == 3) {
            HLog.d(this.LOG_TAG, "wifi 已经打开");
        }
    }

    public static abstract interface IWifiStateChange {
        public abstract void onWifiStateChange(int paramInt);
    }
}

package com.twd.setting.module.network.repository;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
//import com.twd.setting.utils.HLog;
import com.twd.setting.utils.HLog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WifiScanRepository {
    private static final int WIFI_RESCAN_INTERVAL_MS = 10000;
    private final String LOG_TAG = "WifiScanRepository";
    private final IWifiScanResult iWifiScanResult;
    private Context mContext;
    private final WifiManager mWifiManager;
    private final List<ScanResult> scanResults;

    public WifiScanRepository(Context paramContext, final IWifiScanResult paramIWifiScanResult) {
        mContext = paramContext;
        iWifiScanResult = paramIWifiScanResult;
        mWifiManager = ((WifiManager) paramContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE));
        scanResults = new ArrayList();
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @SuppressLint("MissingPermission")
            public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent) {
                boolean bool = paramAnonymousIntent.getBooleanExtra("resultsUpdated", false);
                StringBuilder str = new StringBuilder();
                str.append("the result of Wi-Fi scanning: ");
                if (bool) {
                    str.append("success");
                } else {
                    str.append("failure");
                }
                Log.d(LOG_TAG, str.toString());
                if (!bool) {
                    paramIWifiScanResult.onComplete(scanResults);
                    return;
                }
                List<ScanResult> list = mWifiManager.getScanResults();
                StringBuilder str2 = new StringBuilder();
                str2.append("raw scan results, size: ");
                str2.append(list.size());
                StringBuilder stringBuilder = new StringBuilder(str2.toString());
                stringBuilder.append(", ssid --> ");
                Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    ScanResult localScanResult = (ScanResult) iterator.next();
                    if ((localScanResult.SSID != null) && (localScanResult.SSID.length() != 0) && (!localScanResult.capabilities.contains("[IBSS]"))) {
                        stringBuilder.append(localScanResult.SSID);
                        stringBuilder.append(", ");
                    } else {
                        HLog.d(LOG_TAG, "hidden and ad-hoc networks, position: " + list.indexOf(localScanResult));
                    }
                }
                HLog.d(LOG_TAG, stringBuilder.toString());
                scanResults.clear();
                scanResults.addAll(list);
                paramIWifiScanResult.onComplete(WifiScanRepository.this.scanResults);
            }
        };
        paramContext.getApplicationContext().registerReceiver(broadcastReceiver, new IntentFilter("android.net.wifi.SCAN_RESULTS"));
    }

    public List<ScanResult> getRawScanResults() {
        return scanResults;
    }

    public void scanWifi() {
        if (!mWifiManager.startScan()) {
            iWifiScanResult.onComplete(scanResults);
        }
    }

    public static abstract interface IWifiScanResult {
        public abstract void onComplete(List<ScanResult> paramList);
    }
}

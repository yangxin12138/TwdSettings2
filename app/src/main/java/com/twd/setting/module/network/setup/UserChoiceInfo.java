package com.twd.setting.module.network.setup;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.text.TextUtils;

import androidx.lifecycle.ViewModel;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;

public class UserChoiceInfo
        extends ViewModel {
    public static final int HOTSPOTAPBAND = 8;
    public static final int HOTSPOTNAME = 5;
    public static final int HOTSPOTPASSWORD = 7;
    public static final int HOTSPOTSECURITY = 6;
    public static final int PASSWORD = 2;
    public static final int SECURITY = 3;
    public static final int SELECT_WIFI = 1;
    public static final int SSID = 4;
    private ScanResult mChosenNetwork;
    private String mConnectedNetwork;
    private HashMap<Integer, CharSequence> mDataSummary = new HashMap();
    private boolean mIsPasswordHidden = false;
    private WifiConfiguration mWifiConfiguration = new WifiConfiguration();
    private int mWifiSecurity;

    public boolean choiceChosen(CharSequence paramCharSequence, int paramInt) {
        if (!mDataSummary.containsKey(Integer.valueOf(paramInt))) {
            return false;
        }
        return TextUtils.equals(paramCharSequence,  mDataSummary.get(Integer.valueOf(paramInt)));
    }

    public boolean containsPage(int paramInt) {
        return mDataSummary.containsKey(Integer.valueOf(paramInt));
    }

    public String get(int paramInt) {
        if (!mDataSummary.containsKey(Integer.valueOf(paramInt))) {
            return "";
        }
        return (mDataSummary.get(Integer.valueOf(paramInt))).toString();
    }

    public ScanResult getChosenNetwork() {
        return mChosenNetwork;
    }

    public String getConnectedNetwork() {
        return mConnectedNetwork;
    }

    public CharSequence getPageSummary(int paramInt) {
        if (!mDataSummary.containsKey(Integer.valueOf(paramInt))) {
            return null;
        }
        return  mDataSummary.get(Integer.valueOf(paramInt));
    }

    public WifiConfiguration getWifiConfiguration() {
        return mWifiConfiguration;
    }

    public int getWifiSecurity() {
        return mWifiSecurity;
    }

    public void init() {
        mDataSummary = new HashMap();
        mWifiConfiguration = new WifiConfiguration();
        mWifiSecurity = 0;
        mChosenNetwork = null;
        mIsPasswordHidden = false;
    }

    public boolean isPasswordHidden() {
        return mIsPasswordHidden;
    }

    public void put(int paramInt, String paramString) {
        mDataSummary.put(Integer.valueOf(paramInt), paramString);
    }

    public void removePageSummary(int paramInt) {
        mDataSummary.remove(Integer.valueOf(paramInt));
    }

    public void setChosenNetwork(ScanResult paramScanResult) {
        mChosenNetwork = paramScanResult;
    }

    public void setConnectedNetwork(String paramString) {
        mConnectedNetwork = paramString;
    }

    public void setPasswordHidden(boolean paramBoolean) {
        mIsPasswordHidden = paramBoolean;
    }

    public void setWifiConfiguration(WifiConfiguration paramWifiConfiguration) {
        mWifiConfiguration = paramWifiConfiguration;
    }

    public void setWifiSecurity(int paramInt) {
        mWifiSecurity = paramInt;
    }

    @Retention(RetentionPolicy.SOURCE)
    public static @interface PAGE {
    }
}


package com.twd.setting.module.network.setup;

import android.net.IpConfiguration;
import android.net.LinkAddress;
import android.net.ProxyInfo;
import android.net.StaticIpConfiguration;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModel;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

public class AdvancedOptionsFlowInfo
        extends ViewModel {
    private static final String TAG = "AdvancedOptionsFlowInfo";
    public static final int ADVANCED_OPTIONS = 1;
    public static final int DNS1 = 10;
    public static final int DNS2 = 11;
    public static final int GATEWAY = 9;
    public static final int IP_ADDRESS = 7;
    public static final int IP_SETTINGS = 6;
    public static final int NETWORK_PREFIX_LENGTH = 8;
    public static final int PPPOE_INTERFACE = 12;
    public static final int PPPOE_PASSWORD = 14;
    public static final int PPPOE_USERNAME = 13;
    public static final int PROXY_BYPASS = 5;
    public static final int PROXY_HOSTNAME = 3;
    public static final int PROXY_PORT = 4;
    public static final int PROXY_SETTINGS = 2;
    private boolean mCanStart = false;
    private IpConfiguration mIpConfiguration;
    private HashMap<Integer, CharSequence> mPageSummary = new HashMap();
    private String mPrintableSsid;
    private boolean mSettingsFlow;

    public boolean canStart() {
        return mCanStart;
    }

    public boolean choiceChosen(CharSequence paramCharSequence, int paramInt) {
        if (!mPageSummary.containsKey(Integer.valueOf(paramInt))) {
            return false;
        }
        return TextUtils.equals(paramCharSequence, (CharSequence) mPageSummary.get(Integer.valueOf(paramInt)));
    }

    public boolean containsPage(int paramInt) {
        return mPageSummary.containsKey(Integer.valueOf(paramInt));
    }

    public String get(int paramInt) {
        if (!mPageSummary.containsKey(Integer.valueOf(paramInt))) {
            return "";
        }
        return ((CharSequence) mPageSummary.get(Integer.valueOf(paramInt))).toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public InetAddress getInitialDns(int paramInt) {
        Object localObject = mIpConfiguration;
 /*       if ((mIpConfiguration != null) && (mIpConfiguration.getStaticIpConfiguration() != null) && (mIpConfiguration.getStaticIpConfiguration().dnsServers == null)) {

        try {
            localObject = (InetAddress) this.mIpConfiguration.getStaticIpConfiguration().dnsServers.get(paramInt);
            return (InetAddress) localObject;
        } catch (IndexOutOfBoundsException localIndexOutOfBoundsException) {
        }
        }

  */

        return null;
    }

    public InetAddress getInitialGateway() {
  /*      if ((mIpConfiguration != null) && (mIpConfiguration.getStaticIpConfiguration() != null)) {
            return this.mIpConfiguration.getStaticIpConfiguration().gateway;
        }

   */

        return null;
    }

    public LinkAddress getInitialLinkAddress() {
 /*       if ((mIpConfiguration != null) && (mIpConfiguration.getStaticIpConfiguration() != null)) {
            return this.mIpConfiguration.getStaticIpConfiguration().ipAddress;
        }

  */
        return null;
    }

    public ProxyInfo getInitialProxyInfo() {
 /*       if (mIpConfiguration != null) {
            return mIpConfiguration.getHttpProxy();
        }

  */
        return null;
    }

    public IpConfiguration getIpConfiguration() {
        return mIpConfiguration;
    }

    public String getPrintableSsid() {
        return mPrintableSsid;
    }

    public boolean isSettingsFlow() {
        return mSettingsFlow;
    }

    public void put(int paramInt, CharSequence paramCharSequence) {
        mPageSummary.put(Integer.valueOf(paramInt), paramCharSequence);
    }

    public void remove(int paramInt) {
        mPageSummary.remove(Integer.valueOf(paramInt));
    }

    public void setCanStart(boolean paramBoolean) {
        mCanStart = paramBoolean;
    }

    public void setIpConfiguration(IpConfiguration paramIpConfiguration) {
        mIpConfiguration = paramIpConfiguration;
    }

    public void setPrintableSsid(String paramString) {
        mPrintableSsid = paramString;
    }

    public void setSettingsFlow(boolean paramBoolean) {
        mSettingsFlow = paramBoolean;
    }

    @Retention(RetentionPolicy.SOURCE)
    public static @interface PAGE {
    }
}


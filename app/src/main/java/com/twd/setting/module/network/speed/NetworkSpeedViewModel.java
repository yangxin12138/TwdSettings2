package com.twd.setting.module.network.speed;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkInfo;
import android.text.TextUtils;

import androidx.lifecycle.AndroidViewModel;

import com.twd.setting.utils.HLog;
import com.twd.setting.utils.StringUtil;

import java.net.InetAddress;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetworkSpeedViewModel
        extends AndroidViewModel {
    public static final int NET_TYPE_ETHERNET = 2;
    public static final int NET_TYPE_NONE = 0;
    public static final int NET_TYPE_WIFI = 1;
    private final String LOG_TAG = "NetworkSpeedViewModel";
    private ConnectivityManager connMgr;

    public NetworkSpeedViewModel(Application paramApplication) {
        super(paramApplication);
        connMgr = ((ConnectivityManager) paramApplication.getSystemService(Context.CONNECTIVITY_SERVICE));
    }

    private boolean isCorrectIp(String paramString) {
        return Pattern.compile("([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}").matcher(paramString).matches();
    }

    public String getIpAddress(Network paramNetwork) {
        if (paramNetwork == null) {
            return "0.0.0.0";
        }
        List<LinkAddress> list = connMgr.getLinkProperties(paramNetwork).getLinkAddresses();
        if ((list != null) && (!list.isEmpty())) {
            int i = 0;
            while (i < list.size()) {
                InetAddress localInetAddress = list.get(i).getAddress();
                if (isCorrectIp(localInetAddress.getHostAddress())) {
                    return localInetAddress.getHostAddress();
                }
                i += 1;
            }
        }
        return "0.0.0.0";
    }

    public String getNetFluency(String paramString) {
        if (paramString.contains("GB/s")) {
            return "4K";
        }
        if (paramString.contains("MB/s")) {
            String matchFloat = StringUtil.matchFloat(paramString);
            if (TextUtils.isEmpty(matchFloat)) {
                HLog.d(this.LOG_TAG, "获取网速值失败 --- 传入的字符串speed中没有浮点数");
                return "";
            }
        } else if (paramString.contains("KB/s")) {
            return "流畅";
        } else if (paramString.contains("B/s")) {
            return "流畅";
        }
        return "流畅";

 /*       try {
            Float parseFloat = Float.parseFloat(paramString);
            if (parseFloat >= 2.0F) {
                return "超清";
            }
            return "流畅";
        } catch (NumberFormatException numberFormatException) {
            float f;
            do {
                for (; ; ) {
                }
                if (f < 4.0F) {
                    return "高清";
                }
            } while (f >= 6.0F);
        }
        if (f < 20.0F) {
            return "蓝光";
        }
        }

        HLog.d(this.LOG_TAG, "getNetFluency() --> catch NumberFormatException");
        return "流畅";
        return "超清";
        return "4K";
        return "蓝光";

  */
    }

    public int getNetType(NetworkInfo paramNetworkInfo) {
        if ((paramNetworkInfo != null) && (paramNetworkInfo.isConnected())) {
            if (paramNetworkInfo.getType() == 1) {
                return 1;
            }
            if (paramNetworkInfo.getType() == 9) {
                return 2;
            }
        }
        return 0;
    }

    public NetworkSpeedTester newHandler(String paramString, NetworkSpeedTester.INetSpeedTest paramINetSpeedTest) {
        return new NetworkSpeedTester(getApplication().getMainLooper(), paramString, paramINetSpeedTest);
    }
}


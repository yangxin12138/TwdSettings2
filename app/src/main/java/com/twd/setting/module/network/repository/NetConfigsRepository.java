package com.twd.setting.module.network.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.LinkAddress;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import com.twd.setting.utils.NetworkUtils;
//import com.twd.setting.utils.HLog;
import com.twd.setting.utils.HLog;
import com.twd.setting.widgets.ToastTools;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class NetConfigsRepository {
    private final String TAG = "NetConfigsRepository";
    private Context context;
    private DhcpInfo dhcpInfo;
    private WifiManager wifiManager;

    public NetConfigsRepository(Context paramContext) {
        context = paramContext;
        wifiManager = ((WifiManager) paramContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE));
    }

    public static String calcMaskByPrefixLength(int paramInt) {
        int[] arrayOfInt = new int[4];
        int i = 0;
        while (i < 4) {
            arrayOfInt[(3 - i)] = (-1 << 32 - paramInt >> i * 8 & 0xFF);
            i += 1;
        }
        Object localObject = new StringBuilder();
        ((StringBuilder) localObject).append("");
        ((StringBuilder) localObject).append(arrayOfInt[0]);
        localObject = ((StringBuilder) localObject).toString();
        paramInt = 1;
        while (paramInt < 4) {
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append((String) localObject);
            localStringBuilder.append(".");
            localStringBuilder.append(arrayOfInt[paramInt]);
            localObject = localStringBuilder.toString();
            paramInt += 1;
        }
        return (String) localObject;
    }

    @SuppressLint("MissingPermission")
    public void changeWifiConfiguration(boolean paramBoolean, String paramString1, int paramInt, String paramString2, String paramString3, String paramString4) {
        WifiManager wifiManager = (WifiManager) this.context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            return;
        }
        Object localObject2 = null;
        WifiInfo localObject3 = wifiManager.getConnectionInfo();
        Object localObject4 = wifiManager.getConfiguredNetworks();
        WifiConfiguration wifiConfiguration = null;
        if (localObject4 != null) {
            Iterator iterator = ((List) localObject4).iterator();
            do {
                if (!iterator.hasNext()) {
                    break;
                }
                wifiConfiguration = (WifiConfiguration) iterator.next();
            } while (wifiConfiguration.networkId != localObject3.getNetworkId());
        }
        if (wifiConfiguration == null) {
            return;
        }
        try {
            Class class_m = wifiConfiguration.getClass().getMethod("getIpAssignment", new Class[0]).invoke(wifiConfiguration, new Object[0]).getClass();
            Object obj = wifiConfiguration.getClass().getMethod("getStaticIpConfiguration", new Class[0]).invoke(wifiConfiguration, new Object[0]);
            if (paramBoolean) {
                class_m.getClass().getMethod("setIpAssignment", new Class[]{class_m}).invoke(class_m, new Object[]{Enum.valueOf((Class) localObject2, "DHCP")});
                if (localObject3 != null) {
                    localObject3.getClass().getMethod("clear", new Class[0]).invoke(localObject3, new Object[0]);
                }
            } else {
                class_m.getClass().getMethod("setIpAssignment", new Class[]{class_m}).invoke(class_m, new Object[]{Enum.valueOf((Class) localObject2, "STATIC")});
                localObject2 = localObject3;
                if (localObject3 == null) {
                    localObject2 = Class.forName("android.net.StaticIpConfiguration").newInstance();
                }
                LinkAddress linkAddress = (LinkAddress) LinkAddress.class.getConstructor(new Class[]{InetAddress.class, Integer.TYPE}).newInstance(new Object[]{InetAddress.getByName(paramString1), Integer.valueOf(paramInt)});
                linkAddress.getClass().getField("ipAddress").set(linkAddress, paramString1);
                linkAddress.getClass().getField("gateway").set(linkAddress, InetAddress.getByName(paramString4));
                List list = (List) linkAddress.getClass().getField("dnsServers").get(linkAddress);
                list.clear();
                list.add(InetAddress.getByName(paramString2));
                list.add(InetAddress.getByName(paramString3));
                class_m.getClass().getMethod("setStaticIpConfiguration", new Class[]{localObject2.getClass()}).invoke(class_m, new Object[]{localObject2});
            }
            paramInt = wifiManager.addNetwork((WifiConfiguration) wifiConfiguration);
            wifiManager.disableNetwork(paramInt);
            wifiManager.enableNetwork(paramInt, true);
            ToastTools.Instance().showToast(context, "保存成功");
            return;
        } catch (Exception e) {
            HLog.d(TAG, "e --> " + e.getMessage());
            ToastTools.Instance().showToast(this.context, "保存失败");
        }
    }

    public short countPrefixLength(String paramString) {

    byte[] prefix = NetworkUtils.numericToInetAddress(paramString).getAddress();
    int k = prefix.length;
    int i = 0;
    short s = 0;
    while (i < k)
    {
      int j = prefix[i];
      while (j != 0)
      {
        j = (byte)(j << 1);
        s = (short)(s + 1);
      }
      i += 1;
    }
    return s;


    }

    public String getDNS1() {
        //return String.valueOf(wifiManager.getDhcpInfo().dns1);
        dhcpInfo = wifiManager.getDhcpInfo();
        return NetworkUtils.intToInetAddress(dhcpInfo.dns1).getHostAddress();
    }

    public String getDNS2() {
        //return String.valueOf(wifiManager.getDhcpInfo().dns2);
        dhcpInfo = wifiManager.getDhcpInfo();
        return NetworkUtils.intToInetAddress(dhcpInfo.dns2).getHostAddress();
    }

    public String getGateway() {
        //return String.valueOf(wifiManager.getDhcpInfo().gateway);
        dhcpInfo = wifiManager.getDhcpInfo();
        return NetworkUtils.intToInetAddress(dhcpInfo.gateway).getHostAddress();
    }

    public String getIpAddress() {
        //return String.valueOf(wifiManager.getDhcpInfo().ipAddress);
        DhcpInfo localDhcpInfo = wifiManager.getDhcpInfo();
        this.dhcpInfo = localDhcpInfo;
        return NetworkUtils.intToInetAddress(localDhcpInfo.ipAddress).getHostAddress();
    }

    public String getSubnetMask() {
        String ipAddress="";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();

                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        ipAddress = inetAddress.getHostAddress().toString();
                        System.out.println("ipaddress=" + ipAddress + " formatter ip" + Formatter.formatIpAddress(inetAddress.hashCode()));
                        return ipAddress;
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
/*

        try {
            InterfaceAddress localInterfaceAddress;
            do {
                Enumeration enumeration = NetworkInterface.getNetworkInterfaces();
                NetworkInterface localObject2 = null;
                while (!((Iterator) enumeration).hasNext()) {
                    do {
                        if (!((Enumeration) enumeration).hasMoreElements()) {
                            break;
                        }
                        localObject2 = (NetworkInterface) ((Enumeration) enumeration).nextElement();
                    } while (!((NetworkInterface) localObject2).isUp());
                    //  localObject2 = ((NetworkInterface)localObject2).getInterfaceAddresses().iterator();
                }
                localInterfaceAddress = (InterfaceAddress) ((Iterator) enumeration).next();
            } while ((!(localInterfaceAddress.getAddress() instanceof Inet4Address)) || ("127.0.0.1".equals(localInterfaceAddress.getAddress().getHostAddress())));
            Object localObject1 = calcMaskByPrefixLength(localInterfaceAddress.getNetworkPrefixLength());
            return (String) localObject1;
        } catch (SocketException localSocketException) {
            localSocketException.printStackTrace();
        }

 */
        return "0.0.0.0";
    }

    @SuppressLint("MissingPermission")
    public void removeWifiBySsid(String paramString) {
        Log.d(TAG, "try to removeWifiBySsid, targetSsid=" + paramString);
        Iterator iterator = wifiManager.getConfiguredNetworks().iterator();
        while (iterator.hasNext()) {
            WifiConfiguration wifiConfiguration = (WifiConfiguration) iterator.next();
            String str = wifiConfiguration.SSID;
            Log.d(TAG, "removeWifiBySsid ssid=" + str);
            if (TextUtils.equals(str, paramString)) {
                Log.d(TAG, "removeWifiBySsid success, SSID = " + wifiConfiguration.SSID + " netId = " + wifiConfiguration.networkId);
                wifiManager.removeNetwork(wifiConfiguration.networkId);
                wifiManager.saveConfiguration();
            }
        }
    }
}

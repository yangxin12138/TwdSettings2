package com.twd.setting.module.network.ether;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.RouteInfo;
import android.os.Build;
import android.os.Build.VERSION;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.text.TextUtils;

import com.twd.setting.widgets.ToastTools;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EthernetConfigsRepository {
    public static final int ALL_MATCH = 4;
    public static final int DNS_NOT_MATCH = 3;
    public static final int GATEWAY_NOT_MATCH = 2;
    public static final int IP_NOT_MATCH = 0;
    private static final String LOG_TAG = "EthernetConfigsRepository";
    public static final int NETMASK_NOT_MATCH = 1;
    private ConnectivityManager connMgr;
    private Context context;

    public EthernetConfigsRepository(Context paramContext) {
        context = paramContext;
        connMgr = ((ConnectivityManager) paramContext.getSystemService(Context.CONNECTIVITY_SERVICE));
    }

    private String calcMaskByPrefixLength(int paramInt) {
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

    private static Map<String, Object> getIpConfigurationEnum(Class<?> paramClass) {
        HashMap localHashMap = new HashMap();
        Class<?>[] declaredClasses = paramClass.getDeclaredClasses();
        int k = declaredClasses.length;
        int i = 0;
        while (i < k) {
            Object localObject1 = declaredClasses[i];
            Object[] arrayOfObject = ((Class) localObject1).getEnumConstants();
            if (arrayOfObject != null) {
                int m = arrayOfObject.length;
                int j = 0;
                while (j < m) {
                    Object localObject2 = arrayOfObject[j];
                    StringBuilder localStringBuilder = new StringBuilder();
                    localStringBuilder.append(((Class) localObject1).getSimpleName());
                    localStringBuilder.append(".");
                    localStringBuilder.append(localObject2.toString());
                    localHashMap.put(localStringBuilder.toString(), localObject2);
                    j += 1;
                }
            }
            i += 1;
        }
        return localHashMap;
    }

    private static int getPrefixLength(String paramString) {
        String[] strs = paramString.split(".");
        int m = strs.length;
        int i = 0;
        int k;
        int j = 0;
        for (j = 0; i < m; j = k) {
            k = j;
            if (strs[i].equals("255")) {
                k = j + 1;
            }
            i += 1;
        }
        return j * 8;
    }

    private boolean isCorrectIp(String paramString) {
        return Pattern.compile("([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}").matcher(paramString).matches();
    }

    private static Object newIpConfiguration(Object paramObject)
            throws Exception {
        Class localClass = Class.forName("android.net.IpConfiguration");
        Object localObject = localClass.newInstance();
        localClass.getField("staticIpConfiguration").set(localObject, paramObject);
        paramObject = getIpConfigurationEnum(localClass);
        localClass.getField("ipAssignment").set(localObject, ((Map) paramObject).get("IpAssignment.STATIC"));
        localClass.getField("proxySettings").set(localObject, ((Map) paramObject).get("ProxySettings.STATIC"));
        return localObject;
    }

    private static Object newLinkAddress(String paramString1, String paramString2)
            throws Exception {
        return Class.forName("android.net.LinkAddress").getDeclaredConstructor(new Class[]{InetAddress.class, Integer.TYPE}).newInstance(new Object[]{InetAddress.getByName(paramString1), Integer.valueOf(getPrefixLength(paramString2))});
    }

    private static Object newStaticIpConfiguration(String paramString1, String paramString2, String paramString3, String paramString4)
            throws Exception {
        Object localObject2 = Class.forName("android.net.StaticIpConfiguration");
        Object localObject1 = ((Class) localObject2).newInstance();
        Field localField1 = ((Class) localObject2).getField("ipAddress");
        Field localField2 = ((Class) localObject2).getField("gateway");
        Field localField3 = ((Class) localObject2).getField("domains");
        localObject2 = ((Class) localObject2).getField("dnsServers");
        localField1.set(localObject1, newLinkAddress(paramString1, paramString3));
        localField2.set(localObject1, InetAddress.getByName(paramString2));
        localField3.set(localObject1, paramString3);
        ((ArrayList) ((Field) localObject2).get(localObject1)).add(InetAddress.getByName(paramString4));
        return localObject1;
    }

    private static void saveIpSettings(Context paramContext, String paramString1, String paramString2, String paramString3, String paramString4) {
        ContentResolver contentResolver = paramContext.getContentResolver();
        Settings.Global.putString(contentResolver, "ethernet_static_ip", paramString1);
        Settings.Global.putString(contentResolver, "ethernet_static_mask", paramString2);
        Settings.Global.putString(contentResolver, "ethernet_static_gateway", paramString3);
        Settings.Global.putString(contentResolver, "ethernet_static_dns1", paramString4);
    }

    @SuppressLint("WrongConstant")
    public static boolean setDynamicIp(Context paramContext) {
        try {
            Class localClass1 = Class.forName("android.net.EthernetManager");
            Object ethernet = paramContext.getSystemService("ethernet");
            Class localClass2 = Class.forName("android.net.IpConfiguration");
            Object localObject = localClass2.newInstance();
            Map localMap = getIpConfigurationEnum(localClass2);
            localClass2.getField("ipAssignment").set(localObject, localMap.get("IpAssignment.DHCP"));
            localClass2.getField("proxySettings").set(localObject, localMap.get("ProxySettings.NONE"));
            localClass1.getDeclaredMethod("setConfiguration", new Class[]{localObject.getClass()}).invoke(ethernet, new Object[]{localObject});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressLint("WrongConstant")
    public static boolean setEthernetStaticIp(Context paramContext, String paramString1, String paramString2, String paramString3, String paramString4) {
        try {
            Object localObject1 = Class.forName("android.net.EthernetManager");
            Object localObject2 = paramContext.getSystemService("ethernet");
            Object localObject3 = newIpConfiguration(newStaticIpConfiguration(paramString1, paramString3, paramString2, paramString4));
            int i = Build.VERSION.SDK_INT;
            if (i >= 28) {
                localObject1 = ((Class) localObject1).getDeclaredMethod("setConfiguration", new Class[]{String.class, localObject3.getClass()});
            } else {
                localObject1 = ((Class) localObject1).getDeclaredMethod("setConfiguration", new Class[]{localObject3.getClass()});
            }
            saveIpSettings(paramContext, paramString1, paramString2, paramString3, paramString4);
            if (Build.VERSION.SDK_INT >= 28) {
                ((Method) localObject1).invoke(localObject2, new Object[]{"eth0", localObject3});
            } else {
                ((Method) localObject1).invoke(localObject2, new Object[]{localObject3});
            }
            ToastTools.Instance().showToast(paramContext, "保存成功");
            return true;
        } catch (Exception e) {
            ToastTools.Instance().showToast(paramContext, "保存失败");
            e.printStackTrace();
        }
        return false;
    }

    public int checkAllNetworkConfigs(String paramString1, String paramString2, String paramString3, String paramString4) {
        if (!isCorrectIp(paramString1)) {
            return 0;
        }
        if (!isCorrectIp(paramString2)) {
            return 1;
        }
        if (!isCorrectIp(paramString3)) {
            return 2;
        }
        if (!isCorrectIp(paramString4)) {
            return 3;
        }
        return 4;
    }

    public String getDNS1(Network paramNetwork) {
        if (paramNetwork == null) {
            return "0.0.0.0";
        }
        List list = connMgr.getLinkProperties(paramNetwork).getDnsServers();
        if ((list != null) && (list.size() >= 1)) {
            return ((InetAddress) list.get(0)).toString();
        }
        return "0.0.0.0";
    }

    public String getDNS2(Network paramNetwork) {
        if (paramNetwork == null) {
            return "0.0.0.0";
        }
        List list = this.connMgr.getLinkProperties(paramNetwork).getDnsServers();
        if ((list != null) && (list.size() >= 2)) {
            return ((InetAddress) list.get(1)).toString();
        }
        return "0.0.0.0";
    }

    public String getGateway(Network paramNetwork) {
        if (paramNetwork == null) {
            return "0.0.0.0";
        }
        List list = this.connMgr.getLinkProperties(paramNetwork).getRoutes();
        if ((list != null) && (!list.isEmpty())) {
            int i = 0;
            while (i < list.size()) {
                String str = ((RouteInfo) list.get(i)).getGateway().getHostAddress();
                if (isCorrectIp(str)) {
                    return str;
                }
                i += 1;
            }
        }
        return "0.0.0.0";
    }

    public String getIpAddress(Network paramNetwork) {
        if (paramNetwork == null) {
            return "0.0.0.0";
        }
        List list = this.connMgr.getLinkProperties(paramNetwork).getLinkAddresses();
        if ((list != null) && (!list.isEmpty())) {
            int i = 0;
            while (i < list.size()) {
                InetAddress localInetAddress = ((LinkAddress) list.get(i)).getAddress();
                if (isCorrectIp(localInetAddress.getHostAddress())) {
                    return localInetAddress.getHostAddress();
                }
                i += 1;
            }
        }
        return "0.0.0.0";
    }

    public String getSubnetMask(Network paramNetwork) {
        if (paramNetwork == null) {
            return "0.0.0.0";
        }
        String name = this.connMgr.getLinkProperties(paramNetwork).getInterfaceName();
 /*   try
    {
      InterfaceAddress localInterfaceAddress;
      do
      {
        Enumeration localEnumeration = NetworkInterface.getNetworkInterfaces();
        Object localObject;
        while (!((Iterator)localObject).hasNext())
        {
          do
          {
            if (!localEnumeration.hasMoreElements()) {
              break;
            }
            localObject = (NetworkInterface)localEnumeration.nextElement();
          } while ((!((NetworkInterface)localObject).isUp()) && (!paramNetwork.equals(((NetworkInterface)localObject).getDisplayName())));
          localObject = ((NetworkInterface)localObject).getInterfaceAddresses().iterator();
        }
        localInterfaceAddress = (InterfaceAddress)((Iterator)localObject).next();
      } while ((!(localInterfaceAddress.getAddress() instanceof Inet4Address)) || ("127.0.0.1".equals(localInterfaceAddress.getAddress().getHostAddress())));
      paramNetwork = calcMaskByPrefixLength(localInterfaceAddress.getNetworkPrefixLength());
      return paramNetwork;
    }
    catch (SocketException e)
    {
      e.printStackTrace();
    }

  */
        return "0.0.0.0";
    }

    public boolean isEtherConnected() {
        Object localObject = this.connMgr.getNetworkInfo(9);
        if (localObject != null) {
            localObject = ((NetworkInfo) localObject).getState();
            if ((localObject != null) && ((localObject == NetworkInfo.State.CONNECTED) || (localObject == NetworkInfo.State.CONNECTING))) {
                return true;
            }
        }
        return false;
    }

    public boolean setEthernetConfigs(boolean paramBoolean, String paramString1, String paramString2, String paramString3, String paramString4) {
        if (checkAllNetworkConfigs(paramString1, paramString2, paramString3, paramString4) != 4) {
            StringBuilder localStringBuilder = new StringBuilder("");
            if (!isCorrectIp(paramString1)) {
                localStringBuilder.append("IP 地址");
            }
            if (!isCorrectIp(paramString2)) {
                if (!TextUtils.isEmpty(localStringBuilder)) {
                    localStringBuilder.append(",");
                }
                localStringBuilder.append("子网掩码");
            }
            if (!isCorrectIp(paramString3)) {
                if (!TextUtils.isEmpty(localStringBuilder)) {
                    localStringBuilder.append(",");
                }
                localStringBuilder.append("网关");
            }
            if (!isCorrectIp(paramString4)) {
                if (!TextUtils.isEmpty(localStringBuilder)) {
                    localStringBuilder.append(",");
                }
                localStringBuilder.append("DNS");
            }
            if (!TextUtils.isEmpty(localStringBuilder)) {
                localStringBuilder.append("输入错误，请重新输入");
                ToastTools.Instance().showToast(this.context, localStringBuilder.toString());
            }
            return false;
        }
        if (paramBoolean) {
            return setEthernetStaticIp(this.context, paramString1, paramString2, paramString3, paramString4);
        }
        return setDynamicIp(this.context);
    }
}

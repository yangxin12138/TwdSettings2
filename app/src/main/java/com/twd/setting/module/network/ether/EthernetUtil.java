package com.twd.setting.module.network.ether;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.provider.Settings.Global;

import com.twd.setting.utils.HLog;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EthernetUtil {
    private static final String LOG_TAG = "EthernetUtil";

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
        String[] strs = paramString.split("\\.");
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
        boolean bool1 = Settings.Global.putString(contentResolver, "ethernet_static_ip", paramString1);
        boolean bool2 = Settings.Global.putString(contentResolver, "ethernet_static_mask", paramString2);
        boolean bool3 = Settings.Global.putString(contentResolver, "ethernet_static_gateway", paramString3);
        boolean bool4 = Settings.Global.putString(contentResolver, "ethernet_static_dns1", paramString4);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("a = ");
        stringBuilder.append(bool1);
        stringBuilder.append(", b = ");
        stringBuilder.append(bool2);
        stringBuilder.append(", c = ");
        stringBuilder.append(bool3);
        stringBuilder.append(", d = ");
        stringBuilder.append(bool4);
        HLog.d(LOG_TAG, stringBuilder.toString());
    }

    @SuppressLint("WrongConstant")
    public static boolean setDynamicIp(Context paramContext) {
        try {
            Class class1 = Class.forName("android.net.EthernetManager");
            Object ethernet = paramContext.getSystemService("ethernet");
            Class class2 = Class.forName("android.net.IpConfiguration");
            Object localObject3 = class2.newInstance();
            Map localMap = getIpConfigurationEnum(class2);
            class2.getField("ipAssignment").set(localObject3, localMap.get("IpAssignment.DHCP"));
            class2.getField("proxySettings").set(localObject3, localMap.get("ProxySettings.NONE"));
            class1.getDeclaredMethod("setConfiguration", new Class[]{localObject3.getClass()}).invoke(ethernet, new Object[]{localObject3});
            return true;
        } catch (Exception e) {
            HLog.d(LOG_TAG, "e --> " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @SuppressLint("WrongConstant")
    public static boolean setEthernetStaticIp(Context paramContext, String paramString1, String paramString2, String paramString3, String paramString4) {
        try {
            Object localObject3 = Class.forName("android.net.EthernetManager");
            Object localObject1 = paramContext.getSystemService("ethernet");
            Object localObject2 = newIpConfiguration(newStaticIpConfiguration(paramString1, paramString3, paramString2, paramString4));
            localObject3 = ((Class) localObject3).getDeclaredMethod("setConfiguration", new Class[]{String.class, localObject2.getClass()});
            saveIpSettings(paramContext, paramString1, paramString2, paramString3, paramString4);
            ((Method) localObject3).invoke(localObject1, new Object[]{"eth0", localObject2});
            return true;
        } catch (Exception e) {
            HLog.d(LOG_TAG, "e --> " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}

package com.twd.setting.module.network.repository;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Build.VERSION;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Ipv4ConnectInfoRepository {
    private static String calcMaskByPrefixLength(int paramInt) {
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

    private String[] getDnsFromCommand() {
        LinkedList localLinkedList = new LinkedList();
        try {
            LineNumberReader localLineNumberReader = new LineNumberReader(new InputStreamReader(Runtime.getRuntime().exec("getprop").getInputStream()));
            for (; ; ) {
                String str = localLineNumberReader.readLine();
                if (str == null) {
                    break;
                }
                int i = str.indexOf("]: [");
                if (i != -1) {
                    Object localObject = str.substring(1, i);
                    str = str.substring(i + 4, str.length() - 1);
                    if ((((String) localObject).endsWith(".dns")) || (((String) localObject).endsWith(".dns1")) || (((String) localObject).endsWith(".dns2")) || (((String) localObject).endsWith(".dns3")) || (((String) localObject).endsWith(".dns4"))) {
                        localObject = InetAddress.getByName(str);
                        if (localObject != null) {
                            localObject = ((InetAddress) localObject).getHostAddress();
                            if ((localObject != null) && (((String) localObject).length() != 0)) {
                                localLinkedList.add(localObject);
                            }
                        }
                    }
                }
            }
            return (String[]) localLinkedList.toArray(new String[localLinkedList.size()]);
        } catch (Exception localException) {
            localException.printStackTrace();
            if (localLinkedList.isEmpty()) {
                return new String[0];
            }
        }
        return null;
    }

    private String[] getDnsFromConnectionManager(Context paramContext) {
        LinkedList localLinkedList = new LinkedList();
        if ((Build.VERSION.SDK_INT >= 21) && (paramContext != null)) {
            ConnectivityManager connectivityManager = (ConnectivityManager) paramContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo localNetworkInfo1 = connectivityManager.getActiveNetworkInfo();
                if (localNetworkInfo1 != null) {
                    Network[] arrayOfNetwork = connectivityManager.getAllNetworks();
                    int j = arrayOfNetwork.length;
                    int i = 0;
                    while (i < j) {
                        Object localObject = arrayOfNetwork[i];
                        NetworkInfo localNetworkInfo2 = connectivityManager.getNetworkInfo((Network) localObject);
                        if ((localNetworkInfo2 != null) && (localNetworkInfo2.getType() == localNetworkInfo1.getType())) {
                            localObject = connectivityManager.getLinkProperties((Network) localObject).getDnsServers().iterator();
                            while (((Iterator) localObject).hasNext()) {
                                localLinkedList.add(((InetAddress) ((Iterator) localObject).next()).getHostAddress());
                            }
                        }
                        i += 1;
                    }
                }
            }
        }
        if (localLinkedList.isEmpty()) {
            return new String[0];
        }
        return (String[]) localLinkedList.toArray(new String[localLinkedList.size()]);
    }

    public String getDns(Context paramContext) {
        String[] arrayOfString2 = getDnsFromCommand();
        String[] arrayOfString1;
        if (arrayOfString2 != null) {
            arrayOfString1 = arrayOfString2;
            if (arrayOfString2.length != 0) {
            }
        } else {
            arrayOfString1 = getDnsFromConnectionManager(paramContext);
        }
        StringBuffer stringBuffer = new StringBuffer();
        if (arrayOfString1 != null) {
            stringBuffer.append(arrayOfString1[0]);
        }
        return stringBuffer.toString();
    }

    public String getGateWay() {
        try {
            String str = new java.io.BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("ip route list table 0").getInputStream())).readLine().split("\\s+")[2];
            return str;
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
        return "0.0.0.0";
    }

    public String getIP() {
        try {
            InetAddress localInetAddress;
            do {
                Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
                NetworkInterface localObject2 = null;
                while (!allNetInterfaces.hasMoreElements()) {
                    do {
                        if (!allNetInterfaces.hasMoreElements()) {
                            break;
                        }
                        localObject2 = (NetworkInterface) allNetInterfaces.nextElement();
                    } while ((!localObject2.isUp()) || (!"eth0".equals(localObject2.getDisplayName())));
                    //String localObject3 = localObject2.getInetAddresses().toString();
                    return localObject2.getInetAddresses().toString();
                }
                localInetAddress = (InetAddress) allNetInterfaces.nextElement();
            } while (!(localInetAddress instanceof Inet4Address));

            return localInetAddress.getHostAddress();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return "0.0.0.0";
    }

    public String getSubnetMask() {
        try {
            InterfaceAddress localInterfaceAddress;
            do {
                Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
                NetworkInterface localObject2 = null;
                while (!((Iterator) allNetInterfaces).hasNext()) {
                    do {
                        if (!allNetInterfaces.hasMoreElements()) {
                            break;
                        }
                        localObject2 = (NetworkInterface) allNetInterfaces.nextElement();
                    } while ((!localObject2.isUp()) || (!"eth0".equals(localObject2.getDisplayName())));
                    //localObject2 = ((NetworkInterface)localObject2).getInterfaceAddresses().iterator();
                }
                localInterfaceAddress = (InterfaceAddress) ((Iterator) allNetInterfaces).next();
            } while (!(localInterfaceAddress.getAddress() instanceof Inet4Address));
            Object localObject1 = calcMaskByPrefixLength(localInterfaceAddress.getNetworkPrefixLength());
            return (String) localObject1;
        } catch (SocketException localSocketException) {
            localSocketException.printStackTrace();
        }
        return "0.0.0.0";
    }
}

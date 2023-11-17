package com.twd.setting.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Enumeration;

import java.lang.ref.WeakReference;

import android.content.Context;

import android.net.LinkProperties;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import android.net.NetworkInfo;
//import android.net.ProxyProperties;
//import android.net.ethernet.EthernetManager;
import android.net.ConnectivityManager;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;
/*	// bk add start
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.impl.client.DefaultHttpClient;
*/ // bk add end
import org.apache.http.params.CoreConnectionPNames;

import android.net.RouteInfo;

import android.util.Log;

/**
 * 常用的网络转换等工具
 */
public class NetworkUtils {

    //add by liufei for konka samba 20130509 begin
    static NetworkUtils mInstance = null;
    
    private static WeakReference<Context> mContextReference;

    /**

     */
    private NetworkUtils() {
    }

    public static NetworkUtils getInstance(Context context) {
        mContextReference = new WeakReference<Context>(context);
        if (mInstance == null) {
            mInstance = new NetworkUtils();
        }
        return mInstance;
    }
    //add by liufei for konka samba 20130509 end

    /**
     * Convert a IPv4 address from an integer to an InetAddress.
     *
     * @param hostAddress an int corresponding to the IPv4 address in network byte order
     */
    public static InetAddress intToInetAddress(int hostAddress) {
        byte[] addressBytes = {(byte) (0xff & hostAddress),
                (byte) (0xff & (hostAddress >> 8)),
                (byte) (0xff & (hostAddress >> 16)),
                (byte) (0xff & (hostAddress >> 24))};

        try {
            return InetAddress.getByAddress(addressBytes);
        } catch (UnknownHostException e) {
            throw new AssertionError();
        }
    }

    /**
     * Convert a IPv4 address from an InetAddress to an integer
     *
     * @param inetAddr is an InetAddress corresponding to the IPv4 address
     * @return the IP address as an integer in network byte order
     */
    public static int inetAddressToInt(InetAddress inetAddr) {
        //return android.net.NetworkUtils.inetAddressToInt((Inet4Address) inetAddr);
        return NetworkUtils.inetAddressToInt((Inet4Address) inetAddr);
    }

    /**
     * Convert a network prefix length to an IPv4 netmask integer
     *
     * @param prefixLength
     * @return the IPv4 netmask as an integer in network byte order
     */
    public static int prefixLengthToNetmaskInt(int prefixLength) {
        //return android.net.NetworkUtils.prefixLengthToNetmaskInt(prefixLength);
        return NetworkUtils.prefixLengthToNetmaskInt(prefixLength);
    }

    /**
     * Convert a IPv4 netmask integer to a prefix length
     *
     * @param netmask as an integer in network byte order
     * @return the network prefix length
     */
    public static int netmaskIntToPrefixLength(int netmask) {
        return Integer.bitCount(netmask);
    }

    /**
     * Create an InetAddress from a string where the string must be a standard
     * representation of a V4 or V6 address.  Avoids doing a DNS lookup on failure
     * but it will throw an IllegalArgumentException in that case.
     *
     * @param addrString
     * @return the InetAddress
     * {@hide}
     */
    public static InetAddress numericToInetAddress(String addrString)
            throws IllegalArgumentException {
        //return android.net.NetworkUtils.numericToInetAddress(addrString);
        return NetworkUtils.numericToInetAddress(addrString);
    }

    /**
     * Get InetAddress masked with prefixLength.  Will never return null.
     *
     * @param address      IP address which will be masked with specified prefixLength
     * @param prefixLength the prefixLength used to mask the IP
     */
    public static InetAddress getNetworkPart(InetAddress address, int prefixLength) {
        //return android.net.NetworkUtils.getNetworkPart(address, prefixLength);
        return NetworkUtils.getNetworkPart(address, prefixLength);
    }

    /**
     * Check if IP address type is consistent between two InetAddress.
     *
     * @return true if both are the same type.  False otherwise.
     */
    public static boolean addressTypeMatches(InetAddress left, InetAddress right) {
        //return android.net.NetworkUtils.addressTypeMatches(left, right);
        return NetworkUtils.addressTypeMatches(left, right);
    }

    /**
     * Convert a 32 char hex string into a Inet6Address.
     * throws a runtime exception if the string isn't 32 chars, isn't hex or can't be
     * made into an Inet6Address
     *
     * @param addrHexString a 32 character hex string representing an IPv6 addr
     * @return addr an InetAddress representation for the string
     */
    public static InetAddress hexToInet6Address(String addrHexString)
            throws IllegalArgumentException {
        //return android.net.NetworkUtils.hexToInet6Address(addrHexString);
        return hexToInet6Address(addrHexString);
    }

    /**
     * Create a string array of host addresses from a collection of InetAddresses
     *
     * @param addrs a Collection of InetAddresses
     * @return an array of Strings containing their host addresses
     */
    public static String[] makeStrings(Collection<InetAddress> addrs) {
        //return android.net.NetworkUtils.makeStrings(addrs);
        return NetworkUtils.makeStrings(addrs);
    }

    /**
     * Trim leading zeros from IPv4 address strings
     * Our base libraries will interpret that as octel..
     * Must leave non v4 addresses and host names alone.
     * For example, 192.168.000.010 -> 192.168.0.10
     * TODO - fix base libraries and remove this function
     *
     * @param addr a string representing an ip addr
     * @return a string propertly trimmed
     */
    public static String trimV4AddrZeros(String addr) {
        //return android.net.NetworkUtils.trimV4AddrZeros(addr);
        return NetworkUtils.trimV4AddrZeros(addr);
    }

    /*
    * This api is Deprecated,
    * Usage:
    *   NetworkUitls mNetworkUtils = NetworkUtils.getIntance(mContext);
    *   mNetowrkUtils.getLocalIP();
    */
    @Deprecated
    public static String getLocalIP() {
        if (mContextReference == null) {
            System.out.println("yuzong debug: Context Reference error");
            return "";
        } else {
            return getLocalIP(mContextReference.get());
        }
    }
    /*
    * API for geting IP addr
    * When using wifi, we should get the ip from WifiManager,
    * should not direct get ip from NetworkInterface, it may not get the correct interface
    *
    * When wifi ap&ethernet is enable, return AP ip.
    */

    /**
     * {@hide}
     */
    public static String getLocalIP(Context context) {
        System.out.println("yuzong debug getLocalIP:");
        if (context == null) {
            System.out.println("yuzong debug: Context error");
            return "";
        }
        StringBuilder sb = new StringBuilder();
        String eth0Ip = "";
        String wlan0Ip = "";
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager == null) {
            System.out.println("yuzong debug: can't get ConnectivityManager");
            return "";
        }
        NetworkInfo netInfo = connManager.getActiveNetworkInfo();
        // get network type, wifi/ethernet?
        if (netInfo != null) {
            if (!netInfo.isAvailable()) {
                sb.append("Network Unavailable===");
                return sb.toString();
            }
            String type = netInfo.getTypeName();
            System.out.println("yuzong debug:" + type);

            // is wifi, so get the ip addr from wifimanager
            if (type.equalsIgnoreCase("wifi")) {
                WifiManager wifiManager = (WifiManager) context
                        .getSystemService(Context.WIFI_SERVICE);
                if (wifiManager == null) {
                    System.out.println("yuzong debug: can't get WifiManager");
                    return "";
                }
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo != null) {
                    int num = wifiInfo.getIpAddress();
                    String ip = (num & 0xFF) + "." +
                            ((num >> 8) & 0xFF) + "." +
                            ((num >> 16) & 0xFF) + "." +
                            (num >> 24 & 0xFF);
                    sb.append(ip);
                    return sb.toString();
                } else {
                    sb.append("WIFI Network Error");
                    return sb.toString();
                }
            } else if (type.equalsIgnoreCase("ethernet")) {
                try {
                    List<NetworkInterface> interfaces = Collections
                            .list(NetworkInterface.getNetworkInterfaces());
                    for (NetworkInterface intf : interfaces) {
                        List<InterfaceAddress> addr = intf
                                .getInterfaceAddresses();
                        for (InterfaceAddress i : addr) {
                            String ip = i.getAddress().toString()
                                    .replace("/", "");

                            //boolean isIpv4 = InetAddressUtils
                            //       .isIPv4Address(ip);
                            boolean isLoopback = i.getAddress()
                                    .isLoopbackAddress();
                            if (isLoopback) {
                                // do nothing with loopback ip
                                continue;
                            }
							/*
                            if (!isIpv4) {
                                // do nothing with ipv6
                                // int delim = ip.indexOf("%");
                                // if (delim < 0){
                                // sb.append(ip);
                                // }else{
                                // sb.append(ip.substring(0, delim));
                                // }
                            } else {
                                // is ipv4
                                if (intf.getName().equals("wlan0")) {
                                    wlan0Ip = ip;
                                } else if (intf.getName().equals("eth0")) {
                                    eth0Ip = ip;
                                }
                            }
							*/
                        }
                    }
                    if (wlan0Ip != null && !wlan0Ip.isEmpty()) {
                        sb.append(wlan0Ip);
                        return sb.toString();
                    } else {
                        sb.append(eth0Ip);
                        return sb.toString();
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                    sb.append("Network Error");
                }
            }
        } else {
        /*    WifiManager wifiManager_ap = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            int wifiAPState = wifiManager_ap.getWifiApState();
            if ((wifiAPState == WifiManager.WIFI_AP_STATE_ENABLING) || (wifiAPState == WifiManager.WIFI_AP_STATE_ENABLED)) {
                try {
                    List<NetworkInterface> interfaces = Collections
                            .list(NetworkInterface.getNetworkInterfaces());
                    for (NetworkInterface intf : interfaces) {
                        List<InterfaceAddress> addr = intf
                                .getInterfaceAddresses();
                        for (InterfaceAddress i : addr) {
                            String ip = i.getAddress().toString()
                                    .replace("/", "");
                            //boolean isIpv4 = InetAddressUtils
                            //       .isIPv4Address(ip);
                            boolean isLoopback = i.getAddress()
                                    .isLoopbackAddress();
                            if (isLoopback) {
                                continue;
                            }

                            //if (!isIpv4) {
                            //} else {
                            //    if (intf.getName().equals("wlan0")) {
                            //        wlan0Ip = ip;
                            //    } else {//if(intf.getName().equals("eth0")){
                            //    }
                            //}

                        }
                    }
                    if (wlan0Ip != null && !wlan0Ip.isEmpty()) {
                        sb.append(wlan0Ip);
                        return sb.toString();
                    } else {
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                    sb.append("Network Error");
                }
            } else
                sb.append("Network Unavailable");

         */

        }
        return sb.toString();
    }

    /**
     * {@hide}
     */
    public static boolean pingHost(String host, int timeout) {
        BufferedReader in = null;
        boolean ret = false;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("ping", "-w", "" + timeout, "-c", "2", host);
            processBuilder.redirectErrorStream(true);
            Process p = processBuilder.start();
            if (p == null) {
                return false;
            }
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                line = in.readLine();
                if (line.contains("bytes from")) {
                    return true;
                }
            }
        } catch (Exception ex) {
            return false;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }


    /**
     * {@hide}
     */
    public static String getGateWay(Context ctx) {

        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

//			System.out.println("NetworkUtil-- GET ACTIVE NET TYPE = --->"+activeNetInfo.getType());


//			System.out.println("WIFI isEnable ?-----> "+wifi_service.getWifiState());
        //if(wifi_service.getWifiState()==3)  // 1.fail  3.enable
        if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            WifiManager wifi_service = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
            DhcpInfo dhcpInfo = wifi_service.getDhcpInfo();
//				System.out.println("WIFI DHCP info gateway----->"+Formatter.formatIpAddress(dhcpInfo.gateway));
//				System.out.println("WIFI DHCP info netmask----->"+Formatter.formatIpAddress(dhcpInfo.netmask));

            return Formatter.formatIpAddress(dhcpInfo.gateway);
        } else {
        /*    LinkProperties linkProp = connectivityManager.getLinkProperties(ConnectivityManager.TYPE_ETHERNET);
            if (linkProp == null) {
            } else {
                List<RouteInfo> routes = linkProp.getRoutes();
                for (RouteInfo route : routes) {
                    if (route.isDefaultRoute()) {
                        InetAddress gateway = route.getGateway();
                        //System.out.println("________ wychen debug GateWay = " + (gateway.toString().split("/")[1]));
                        return (gateway.toString().split("/")[1]);
                    }
                }
            }
            //EthernetManager eth_service = (EthernetManager)ctx.getSystemService(Context.ETHERNET_SERVICE);
            //DhcpInfo eth_dhcpInfo = eth_service.getDhcpInfo();
//				System.out.println("ETH DHCP info gateway----->"+Formatter.formatIpAddress(eth_dhcpInfo.gateway));
//				System.out.println("ETH DHCP info netmask----->"+Formatter.formatIpAddress(eth_dhcpInfo.netmask));
//				System.out.println("ETH DHCP info ipaddress----->"+Formatter.formatIpAddress(eth_dhcpInfo.ipAddress));

            System.out.println("ERROR: cannot get gateway!!!");

         */
            return null;//Formatter.formatIpAddress(eth_dhcpInfo.gateway);
        }
    }

    /***
     * added the function setMacAddr.
     */
    public native static int setMacAddr(String interfaceName, String macAddress);

    /***
     * Remove the default route for the named interface.
     */
    public native static int removeDefaultRoute(String interfaceName);

    /***
     * Remove host routes that uses the named interface.
     */
    public native static int removeHostRoutes(String interfaceName);
}

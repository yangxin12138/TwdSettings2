package com.twd.setting.module.network.model;

public class NetConfigInfo {
    private String dns1;
    private String dns2;
    private String gateway;
    private String ipAddress;
    private String ssid;
    private String subnetMask;

    public String getDns1() {
        return dns1;
    }

    public String getDns2() {
        return dns2;
    }

    public String getGateway() {
        return gateway;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getSsid() {
        return ssid;
    }

    public String getSubnetMask() {
        return subnetMask;
    }

    public void setDns1(String paramString) {
        dns1 = paramString;
    }

    public void setDns2(String paramString) {
        dns2 = paramString;
    }

    public void setGateway(String paramString) {
        gateway = paramString;
    }

    public void setIpAddress(String paramString) {
        ipAddress = paramString;
    }

    public void setSsid(String paramString) {
        ssid = paramString;
    }

    public void setSubnetMask(String paramString) {
        subnetMask = paramString;
    }
}

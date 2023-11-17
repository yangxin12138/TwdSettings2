package com.twd.setting.module.network.model;

import android.net.wifi.WifiInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.Log;

import java.util.Objects;

public class NetworkKey
        implements Parcelable {
    public static final Parcelable.Creator<NetworkKey> CREATOR = new Parcelable.Creator() {
        public NetworkKey createFromParcel(Parcel source) {
            return new NetworkKey(source);
        }

        public NetworkKey[] newArray(int size) {
            return new NetworkKey[size];
        }
    };
    private static final String TAG = "NetworkKey";
    public static final int TYPE_WIFI = 1;
    public final int type;
    public WifiKey wifiKey;

    private NetworkKey(Parcel in) {
        int i = in.readInt();
        type = i;
        if (i == 1) {
            wifiKey = ((WifiKey) WifiKey.CREATOR.createFromParcel(in));
            return;
        }
        Log.d(TAG, "Parcel has unknown type: " + i);
    }

    public NetworkKey(WifiKey value) {
        type = 1;
        wifiKey = value;
    }

    public static NetworkKey createFromWifiInfo(WifiInfo wifiInfo) {
        if (wifiInfo != null) {
            String ssid = wifiInfo.getSSID();
            String bssid = wifiInfo.getBSSID();
            if ((!TextUtils.isEmpty(ssid)) && (!ssid.equals("<unknown ssid>")) && (!TextUtils.isEmpty(bssid))) {
                try {
                    WifiKey wifiKey = new WifiKey(ssid, bssid);
                    return new NetworkKey(wifiKey);
                } catch (IllegalArgumentException paramWifiInfo) {
                    Log.e("NetworkKey", "Unable to create WifiKey.", paramWifiInfo);
                }
            }
        }
        return null;
    }

    public int describeContents() {
        return 0;
    }

    public boolean equals(Object paramObject) {
        if (this == paramObject) {
            return true;
        }
        if (paramObject != null) {
            if (getClass() != paramObject.getClass()) {
                return false;
            }
            NetworkKey networkKey = (NetworkKey) paramObject;
            return (type == networkKey.type) && (Objects.equals(wifiKey, networkKey.wifiKey));
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{Integer.valueOf(type), wifiKey});
    }

    public String toString() {
        if (type != 1) {
            return "InvalidKey";
        }
        return wifiKey.toString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        if (type == 1) {
            wifiKey.writeToParcel(dest, flags);
            return;
        }
        Log.d(TAG, "NetworkKey has unknown type " + type);
    }
}

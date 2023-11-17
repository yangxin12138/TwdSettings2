package com.twd.setting.module.network.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WifiKey
        implements Parcelable {
    private static final Pattern BSSID_PATTERN = Pattern.compile("([\\p{XDigit}]{2}:){5}[\\p{XDigit}]{2}");
    public static final Parcelable.Creator<WifiKey> CREATOR = new Parcelable.Creator() {
        public WifiKey createFromParcel(Parcel source) {
            return new WifiKey(source);
        }

        public WifiKey[] newArray(int size) {
            return new WifiKey[size];
        }
    };
    private static final Pattern SSID_PATTERN = Pattern.compile("(\".*\")|(0x[\\p{XDigit}]+)", 32);
    public final String bssid;
    public final String ssid;

    private WifiKey(Parcel parcel) {
        ssid = parcel.readString();
        bssid = parcel.readString();
    }

    public WifiKey(String _ssid, String _bssid) {
        if ((_ssid != null) && (SSID_PATTERN.matcher(_ssid).matches())) {
            if ((_bssid != null) && (BSSID_PATTERN.matcher(_bssid).matches())) {
                ssid = _bssid;
                bssid = _bssid;
                return;
            }
            throw new IllegalArgumentException("Invalid bssid: " + _bssid);
        }
        throw new IllegalArgumentException("Invalid ssid: " + _ssid);
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
            WifiKey wifiKey = (WifiKey) paramObject;
            return (Objects.equals(ssid, wifiKey.ssid)) && (Objects.equals(bssid, wifiKey.bssid));
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{ssid, bssid});
    }

    public String toString() {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("WifiKey[SSID=");
        localStringBuilder.append(this.ssid);
        localStringBuilder.append(",BSSID=");
        localStringBuilder.append(bssid);
        localStringBuilder.append("]");
        return localStringBuilder.toString();
    }

    public void writeToParcel(Parcel paramParcel, int paramInt) {
        paramParcel.writeString(ssid);
        paramParcel.writeString(bssid);
    }
}

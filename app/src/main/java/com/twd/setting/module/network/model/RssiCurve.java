package com.twd.setting.module.network.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

import java.util.Arrays;
import java.util.Objects;

public class RssiCurve
        implements Parcelable {
    public static final Parcelable.Creator<RssiCurve> CREATOR = new Parcelable.Creator() {
        public RssiCurve createFromParcel(Parcel source) {
            return new RssiCurve(source);
        }

        public RssiCurve[] newArray(int size) {
            return new RssiCurve[size];
        }
    };
    private static final int DEFAULT_ACTIVE_NETWORK_RSSI_BOOST = 25;
    public final int activeNetworkRssiBoost;
    public final int bucketWidth;
    public final byte[] rssiBuckets;
    public final int start;

    public RssiCurve(int paramInt1, int paramInt2, byte[] paramArrayOfByte) {
        this(paramInt1, paramInt2, paramArrayOfByte, 25);
    }

    public RssiCurve(int _start, int _bucketWidth, byte[] _rssiBuckets, int _activeNetworkRssiBoost) {
        start = _start;
        bucketWidth = _bucketWidth;
        if ((_rssiBuckets != null) && (_rssiBuckets.length != 0)) {
            rssiBuckets = _rssiBuckets;
            activeNetworkRssiBoost = _activeNetworkRssiBoost;
			return;
        }
        throw new IllegalArgumentException("rssiBuckets must be at least one element large.");
    }

    private RssiCurve(Parcel in) {
        start = in.readInt();
        bucketWidth = in.readInt();
        rssiBuckets = new byte[in.readInt()];
        in.readByteArray(rssiBuckets);
        activeNetworkRssiBoost = in.readInt();
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
            RssiCurve rssiCurve = (RssiCurve) paramObject;
            return (start == rssiCurve.start) && (bucketWidth == rssiCurve.bucketWidth) && (Arrays.equals(rssiBuckets, rssiCurve.rssiBuckets)) && (activeNetworkRssiBoost == rssiCurve.activeNetworkRssiBoost);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{Integer.valueOf(this.start), Integer.valueOf(this.bucketWidth), Integer.valueOf(this.activeNetworkRssiBoost)}) ^ Arrays.hashCode(this.rssiBuckets);
    }

    public byte lookupScore(int paramInt) {
        return lookupScore(paramInt, false);
    }

    public byte lookupScore(int paramInt, boolean paramBoolean) {
        int i = paramInt;
        if (paramBoolean) {
            i = paramInt + activeNetworkRssiBoost;
        }
        i = (i - start) / bucketWidth;
        if (i < 0) {
            paramInt = 0;
        } else {
            paramInt = i;
            if (i > rssiBuckets.length - 1) {
                paramInt = rssiBuckets.length - 1;
            }
        }
        return rssiBuckets[paramInt];
    }

    public String toString() {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("RssiCurve[start=");
        localStringBuilder.append(start);
        localStringBuilder.append(",bucketWidth=");
        localStringBuilder.append(bucketWidth);
        localStringBuilder.append(",activeNetworkRssiBoost=");
        localStringBuilder.append(activeNetworkRssiBoost);
        localStringBuilder.append(",buckets=");
        for (int i = 0; i < rssiBuckets.length; i++) {
            localStringBuilder.append(rssiBuckets[i]);
            if (i < rssiBuckets.length - 1) {
                localStringBuilder.append(",");
            }
        }
        localStringBuilder.append("]");
        return localStringBuilder.toString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(start);
        dest.writeInt(bucketWidth);
        dest.writeInt(rssiBuckets.length);
        dest.writeByteArray(rssiBuckets);
        dest.writeInt(activeNetworkRssiBoost);
    }
}

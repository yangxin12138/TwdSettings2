package com.twd.setting.module.network.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class ScoredNetwork
        implements Parcelable {
    private static final String TAG = "ScoredNetwork";
    public static final String ATTRIBUTES_KEY_BADGING_CURVE = "android.net.attributes.key.BADGING_CURVE";
    public static final String ATTRIBUTES_KEY_HAS_CAPTIVE_PORTAL = "android.net.attributes.key.HAS_CAPTIVE_PORTAL";
    public static final String ATTRIBUTES_KEY_RANKING_SCORE_OFFSET = "android.net.attributes.key.RANKING_SCORE_OFFSET";
    public static final Parcelable.Creator<ScoredNetwork> CREATOR = new Parcelable.Creator() {
        public ScoredNetwork createFromParcel(Parcel source) {
            return new ScoredNetwork(source);
        }

        public ScoredNetwork[] newArray(int size) {
            return new ScoredNetwork[size];
        }
    };
    public final Bundle attributes;
    public final boolean meteredHint;
    public final NetworkKey networkKey;
    public final RssiCurve rssiCurve;

    private ScoredNetwork(Parcel source) {
        networkKey = ((NetworkKey) NetworkKey.CREATOR.createFromParcel(source));
        int i = source.readByte();
        boolean bool = true;
        if (i == 1) {
            rssiCurve = ((RssiCurve) RssiCurve.CREATOR.createFromParcel(source));
        } else {
            rssiCurve = null;
        }
        if (source.readByte() != 1) {
            bool = false;
        }
        meteredHint = bool;
        attributes = source.readBundle();
    }

    public ScoredNetwork(NetworkKey paramNetworkKey, RssiCurve paramRssiCurve) {
        this(paramNetworkKey, paramRssiCurve, false);
    }

    public ScoredNetwork(NetworkKey paramNetworkKey, RssiCurve paramRssiCurve, boolean paramBoolean) {
        this(paramNetworkKey, paramRssiCurve, paramBoolean, null);
    }

    public ScoredNetwork(NetworkKey paramNetworkKey, RssiCurve paramRssiCurve, boolean paramBoolean, Bundle paramBundle) {
        networkKey = paramNetworkKey;
        rssiCurve = paramRssiCurve;
        meteredHint = paramBoolean;
        attributes = paramBundle;
    }

    private boolean bundleEquals(Bundle paramBundle1, Bundle paramBundle2) {
        if (paramBundle1 == paramBundle2) {
            return true;
        }
        if (paramBundle1 != null) {
            if (paramBundle2 == null) {
                return false;
            }
            if (paramBundle1.size() != paramBundle2.size()) {
                return false;
            }
            Iterator localIterator = paramBundle1.keySet().iterator();
            while (localIterator.hasNext()) {
                String str = (String) localIterator.next();
                if (!Objects.equals(paramBundle1.get(str), paramBundle2.get(str))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public int calculateBadge(int paramInt) {
        Bundle localBundle = this.attributes;
        if ((localBundle != null) && (localBundle.containsKey("android.net.attributes.key.BADGING_CURVE"))) {
            return ((RssiCurve) this.attributes.getParcelable("android.net.attributes.key.BADGING_CURVE")).lookupScore(paramInt);
        }
        return 0;
    }

    public int calculateRankingScore(int paramInt)
            throws UnsupportedOperationException {
        int attr_v;
        int rssi_v;
        Log.d(TAG,"===============calculateRankingScore=======================");
        if (hasRankingScore()) {
            Object localObject = this.attributes;
            int j = 0;
            if (attributes != null) {
                attr_v = attributes.getInt("android.net.attributes.key.RANKING_SCORE_OFFSET", 0) + 0;
            } else {
                attr_v = 0;
            }
            if (rssiCurve == null) {
                rssi_v = 0;
            } else {
                rssi_v = rssiCurve.lookupScore(paramInt) << 8;
            }
        }
 /*   try
    {
      return ScoredNetwork..ExternalSyntheticBackport0.m(rssi_v, attr_v);

    }
    catch (ArithmeticException localArithmeticException)
    {

    }*/
        if (paramInt < 0) {
            return Integer.MIN_VALUE;
        }
        return Integer.MAX_VALUE;
        //  throw new UnsupportedOperationException("Either rssiCurve or rankingScoreOffset is required to calculate the ranking score");
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
            paramObject = (ScoredNetwork) paramObject;
            return (Objects.equals(networkKey, ((ScoredNetwork) paramObject).networkKey)) && (Objects.equals(this.rssiCurve, ((ScoredNetwork) paramObject).rssiCurve)) && (Objects.equals(Boolean.valueOf(this.meteredHint), Boolean.valueOf(((ScoredNetwork) paramObject).meteredHint))) && (bundleEquals(this.attributes, ((ScoredNetwork) paramObject).attributes));
        }
        return false;
    }

    public boolean hasRankingScore() {
        if (rssiCurve == null) {
            if ((attributes == null) || (!attributes.containsKey("android.net.attributes.key.RANKING_SCORE_OFFSET"))) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.networkKey, this.rssiCurve, Boolean.valueOf(this.meteredHint), this.attributes});
    }

    public String toString() {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("ScoredNetwork{networkKey=");
        localStringBuilder.append(networkKey);
        localStringBuilder.append(", rssiCurve=");
        localStringBuilder.append(rssiCurve);
        localStringBuilder.append(", meteredHint=");
        localStringBuilder.append(meteredHint);
        if ((attributes != null) && (!attributes.isEmpty())) {
            StringBuilder attr = new StringBuilder();
            attr.append(", attributes=");
            attr.append(attributes);
            localStringBuilder.append(attr.toString());
        }
        localStringBuilder.append('}');
        return localStringBuilder.toString();
    }

    public void writeToParcel(Parcel dest, int flags) {
Log.d(TAG,"===============writeToParcel=======================");
        networkKey.writeToParcel(dest, flags);
        if (rssiCurve != null) {
            dest.writeByte((byte) 1);
            rssiCurve.writeToParcel(dest, flags);
        } else {
            dest.writeByte((byte) 0);
        }
        if (meteredHint) {
            dest.writeByte((byte) 1);
        } else {
            dest.writeByte((byte) 0);
        }
        dest.writeBundle(attributes);
    }

}

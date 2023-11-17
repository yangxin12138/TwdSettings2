package com.twd.setting.module.network.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

class TimestampedScoredNetwork
        implements Parcelable {

    private ScoredNetwork mScore;
    private long mUpdatedTimestampMillis;

    protected TimestampedScoredNetwork(Parcel in) {
        mScore =  in.readParcelable(ScoredNetwork.class.getClassLoader());
        mUpdatedTimestampMillis = in.readLong();
    }

    TimestampedScoredNetwork(ScoredNetwork score, long updatedTimestampMillis) {
        mScore = score;
        mUpdatedTimestampMillis = updatedTimestampMillis;
    }

    public int describeContents() {
        return 0;
    }

    public ScoredNetwork getScore() {
        return mScore;
    }

    public long getUpdatedTimestampMillis() {
        return mUpdatedTimestampMillis;
    }

    public void update(ScoredNetwork score, long updatedTimestampMillis) {
        mScore = score;
        mUpdatedTimestampMillis = updatedTimestampMillis;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mScore, flags);
        dest.writeLong(mUpdatedTimestampMillis);
    }

    public static final Parcelable.Creator<TimestampedScoredNetwork> CREATOR = new Parcelable.Creator<TimestampedScoredNetwork>() {
        public TimestampedScoredNetwork createFromParcel(Parcel in) {
            return new TimestampedScoredNetwork(in);
        }

        public TimestampedScoredNetwork[] newArray(int size) {
            return new TimestampedScoredNetwork[size];
        }
    };
}

package com.mocca_capstone.potlatch.adapters;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nektario on 10/15/2014.
 */
public class AdapterState implements Parcelable {
    public int lastAnimatedPosition;


    public AdapterState() {

    }

    public AdapterState(Parcel in) {
        lastAnimatedPosition = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(lastAnimatedPosition);
    }

    public static final Parcelable.Creator<AdapterState> CREATOR = new Parcelable.Creator<AdapterState>() {
        public AdapterState createFromParcel(Parcel in) {
            return new AdapterState(in);
        }

        public AdapterState[] newArray(int size) {
            return new AdapterState[size];
        }
    };
}

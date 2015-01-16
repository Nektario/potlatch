package com.mocca_capstone.potlatch.events;

/**
 * Created by nektario on 11/8/2014.
 */
public class EventAddGift {
    private boolean mIsSuccessStatusCode;

    public EventAddGift(boolean isSuccessStatusCode) {
        mIsSuccessStatusCode = isSuccessStatusCode;
    }

    public boolean isSuccessStatusCode() {
        return mIsSuccessStatusCode;
    }
}

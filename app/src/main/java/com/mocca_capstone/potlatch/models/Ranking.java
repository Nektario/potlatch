package com.mocca_capstone.potlatch.models;

import android.database.Cursor;

import com.mocca_capstone.potlatch.contentprovider.GiftContract;

import java.io.Serializable;

/**
 * Created by nektario on 10/25/2014.
 */
public class Ranking implements Serializable {
    private long mOwnerId;
    private String mOwnerProfileName;
    private String mOwnerProfilePhotoUrl;
    private long mTouchCount;
    private long mFlagCount;


    public Ranking(Cursor c) {
        mOwnerId = c.getLong(c.getColumnIndexOrThrow(GiftContract.Gifts._ID));
        mOwnerProfileName = c.getString(c.getColumnIndexOrThrow(GiftContract.Gifts.OWNER_PROFILE_NAME));
        mOwnerProfilePhotoUrl = c.getString(c.getColumnIndexOrThrow(GiftContract.Gifts.OWNER_PROFILE_PHOTO_URL));
        mTouchCount = c.getLong(c.getColumnIndexOrThrow(GiftContract.Gifts.TOUCH_COUNT));
        mFlagCount = c.getLong(c.getColumnIndexOrThrow(GiftContract.Gifts.FLAG_COUNT));
    }

    public long getOwnerId() {
        return mOwnerId;
    }

    public String getOwnerProfilePhotoUrl() {
        return mOwnerProfilePhotoUrl;
    }

    public String getOwnerProfileName() {
        return mOwnerProfileName;
    }

    public long getTouchCount() {
        return mTouchCount;
    }

    public long getFlagCount() {
        return mFlagCount;
    }
}

package com.mocca_capstone.potlatch.models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.gson.annotations.SerializedName;
import com.mocca_capstone.potlatch.contentprovider.GiftContract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nektario on 10/13/2014.
 */
public class Gift implements Votable, Parcelable, Serializable {
    private static final String TAG = "Gift";
    public static final long CHAIN_ROOT_PARENT_ID = -1;

    @SerializedName("id")
    private long id;
    @SerializedName("parentId")
    private long mParentId;

    @SerializedName("createdTime")
    private long mCreatedTime;
    @SerializedName("modifiedTime")
    private long mModifiedTime;
    @SerializedName("isChainRoot")
    private boolean mIsChainRoot;

    @SerializedName("title")
    private String mTitle;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("latitude")
    private double mLatitude;
    @SerializedName("longitude")
    private double mLongitude;
    @SerializedName("touchCount")
    private long mTouchCount = 0;
    @SerializedName("flagCount")
    private long mFlagCount = 0;
    @SerializedName("touchedUsers")
    private ArrayList<Long> mTouchedUsers;
    @SerializedName("flaggedInappropriateByUsers")
    private ArrayList<Long> mFlaggedByUsers;

    // Media
    @SerializedName("mediaName")
    private String mMediaName;
    @SerializedName("mediaMimeType")
    private String mMediaMimeType;
    @SerializedName("mediaUrl")
    private String mMediaUrl;

    // Owner
    @SerializedName("ownerId")
    private long mOwnerId;
    @SerializedName("ownerProfileName")
    private String mOwnerProfileName;
    @SerializedName("ownerProfilePhotoUrl")
    private String mOwnerProfilePhotoUrl;



    public Gift(long parentId, String title, String description) {
        mParentId = parentId;
        mTitle = title;
        mDescription = description;
    }

    public Gift() {
        id = -1;
    }

    public Gift(Cursor c) {
        id = c.getLong(c.getColumnIndexOrThrow(GiftContract.Gifts._ID));
        mParentId = c.getLong(c.getColumnIndexOrThrow(GiftContract.Gifts.PARENT_ID));
        mCreatedTime = c.getLong(c.getColumnIndexOrThrow(GiftContract.Gifts.CREATED_TIME));
        mModifiedTime = c.getLong(c.getColumnIndexOrThrow(GiftContract.Gifts.MODIFIED_TIME));
        mIsChainRoot = c.getInt(c.getColumnIndexOrThrow(GiftContract.Gifts.IS_CHAIN_ROOT)) == 1;
        mTitle = c.getString(c.getColumnIndexOrThrow(GiftContract.Gifts.TITLE));
        mDescription = c.getString(c.getColumnIndexOrThrow(GiftContract.Gifts.DESCRIPTION));
        mLatitude = c.getDouble(c.getColumnIndexOrThrow(GiftContract.Gifts.LATITUDE));
        mLongitude = c.getDouble(c.getColumnIndexOrThrow(GiftContract.Gifts.LONGITUDE));
        mMediaName = c.getString(c.getColumnIndexOrThrow(GiftContract.Gifts.MEDIA_NAME));
        mMediaMimeType = c.getString(c.getColumnIndexOrThrow(GiftContract.Gifts.MEDIA_MIME_TYPE));
        mMediaUrl = c.getString(c.getColumnIndexOrThrow(GiftContract.Gifts.MEDIA_URL));
        mOwnerId = c.getLong(c.getColumnIndexOrThrow(GiftContract.Gifts.OWNER_ID));
        mOwnerProfileName = c.getString(c.getColumnIndexOrThrow(GiftContract.Gifts.OWNER_PROFILE_NAME));
        mOwnerProfilePhotoUrl = c.getString(c.getColumnIndexOrThrow(GiftContract.Gifts.OWNER_PROFILE_PHOTO_URL));
        mTouchCount = c.getLong(c.getColumnIndexOrThrow(GiftContract.Gifts.TOUCH_COUNT));
        mFlagCount = c.getLong(c.getColumnIndexOrThrow(GiftContract.Gifts.FLAG_COUNT));

        String touchedUsers = c.getString(c.getColumnIndexOrThrow(GiftContract.Gifts.TOUCHED_USERS));
        String flaggedByUsers = c.getString(c.getColumnIndexOrThrow(GiftContract.Gifts.FLAGGED_BY_USERS));

        mTouchedUsers = new ArrayList<Long>();
        Iterable<String> itT = Splitter
                        .on(",")
                        .trimResults()
                        .omitEmptyStrings()
                        .split(touchedUsers);
        for (String id : itT) {
            mTouchedUsers.add(Long.valueOf(id));
        }

        mFlaggedByUsers = new ArrayList<Long>();
        Iterable<String> itF = Splitter
                        .on(",")
                        .trimResults()
                        .omitEmptyStrings()
                        .split(flaggedByUsers);
        for (String id : itF) {
            mFlaggedByUsers.add(Long.valueOf(id));
        }
    }

    public String getMediaName() {
        return mMediaName;
    }
    public String getMediaMimeType() {
        return mMediaMimeType;
    }
    public String getMediaUrl() {
        return mMediaUrl;
    }
    public String getTitle() {
        return mTitle;
    }
    public String getDescription() {
        return mDescription;
    }
    public String getOwnerProfilePhotoUrl() {
        return mOwnerProfilePhotoUrl;
    }
    public String getOwnerProfileName() {
        return mOwnerProfileName;
    }
    public long getOwnerId() {
        return mOwnerId;
    }
    public long getCreatedTime() {
        return mCreatedTime;
    }
    public long getModifiedTime() {
        return mModifiedTime;
    }
    public long getParentId() {
        return mParentId;
    }
    public long getId() {
        return id;
    }
    public boolean isChainRoot() {
        return mIsChainRoot;
    }
    public double getLatitude() {
        return mLatitude;
    }
    public double getLongitude() {
        return mLongitude;
    }


    public long getTouchCount() {
        return mTouchCount;
    }
    public long getFlagCount() {
        return mFlagCount;
    }

    public List<Long> geTouchedUsers() {
        return mTouchedUsers;
    }
    public List<Long> getFlaggedByUsers() {
        return mFlaggedByUsers;
    }

    public String getTouchedUsersAsString() {
        Joiner joiner = Joiner.on(",");
        return joiner.join(mTouchedUsers);
    }
    public String getFlaggedByUsersAsString() {
        Joiner joiner = Joiner.on(",");
        return joiner.join(mFlaggedByUsers);
    }

    public boolean isTouchedByUser(long userId) {
        return mTouchedUsers.contains(userId);
    }
    public boolean isFlaggedByUser(long userId) {
        return mFlaggedByUsers.contains(userId);
    }

    public void addTouch(long userId) {
        if (!mTouchedUsers.contains(userId)) {
            mTouchedUsers.add(userId);
            mTouchCount++;
        }
    }
    public void removeTouch(long userId) {
        if (mTouchedUsers.contains(userId)) {
            mTouchedUsers.remove(mTouchedUsers.indexOf(userId));
            mTouchCount--;
        }
    }

    @Override
    public void addFlag(long userId) {
        if (!mFlaggedByUsers.contains(userId)) {
            mFlaggedByUsers.add(userId);
            mFlagCount++;
        }
    }

    @Override
    public void removeFlag(long userId) {
        if (mFlaggedByUsers.contains(userId)) {
            mFlaggedByUsers.remove(mFlaggedByUsers.indexOf(userId));
            mFlagCount--;
        }
    }


    // Parcelable
    @SuppressWarnings("unchecked")
    public Gift(Parcel in) {
        id = in.readLong();
        mParentId = in.readLong();
        mCreatedTime = in.readLong();
        mModifiedTime = in.readLong();
        mIsChainRoot = in.readInt() == 1;

        mTitle = in.readString();
        mDescription = in.readString();
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
        mTouchCount = in.readLong();
        mFlagCount = in.readLong();
        mTouchedUsers = in.readArrayList(Long.class.getClassLoader());
        mFlaggedByUsers = in.readArrayList(Long.class.getClassLoader());

        mMediaName = in.readString();
        mMediaMimeType = in.readString();
        mMediaUrl = in.readString();

        mOwnerId = in.readLong();
        mOwnerProfileName = in.readString();
        mOwnerProfilePhotoUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeLong(mParentId);
        out.writeLong(mCreatedTime);
        out.writeLong(mModifiedTime);
        out.writeInt(mIsChainRoot ? 1 : 0);

        out.writeString(mTitle);
        out.writeString(mDescription);
        out.writeDouble(mLatitude);
        out.writeDouble(mLongitude);
        out.writeLong(mTouchCount);
        out.writeLong(mFlagCount);
        out.writeList(mTouchedUsers);
        out.writeList(mFlaggedByUsers);

        out.writeString(mMediaName);
        out.writeString(mMediaMimeType);
        out.writeString(mMediaUrl);

        out.writeLong(mOwnerId);
        out.writeString(mOwnerProfileName);
        out.writeString(mOwnerProfilePhotoUrl);
    }

    public static final Parcelable.Creator<Gift> CREATOR = new Parcelable.Creator<Gift>() {
        public Gift createFromParcel(Parcel in) {
            return new Gift(in);
        }

        public Gift[] newArray(int size) {
            return new Gift[size];
        }
    };
}

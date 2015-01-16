package com.mocca_capstone.potlatch.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by nektario on 10/28/2014.
 */
public class User implements BaseUser, Serializable, Comparable<User> {
    @SerializedName("id")
    private long mId;
    @SerializedName("profileName")
    private String mProfileName;
    @SerializedName("profilePhotoUrl")
    private String mProfilePhotoUrl;


    public User() {

    }

    public User(long id, String profileName, String profilePhotoUrl) {
        mId = id;
        mProfileName = profileName;
        mProfilePhotoUrl = profilePhotoUrl;
    }


    public long getId() {
        return mId;
    }

    public String getProfileName() {
        return mProfileName;
    }

    public String getProfilePhotoUrl() {
        return mProfilePhotoUrl;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null
                && obj instanceof User
                && ((User) obj).getId() == mId;
    }

    @Override
    public String toString() {
        return mProfileName;
    }

    @Override
    public int compareTo(User user) {
        return this.mProfileName.compareTo(user.getProfileName());
    }
}

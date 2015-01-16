package com.mocca_capstone.potlatch.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.mocca_capstone.potlatch.R;
import com.mocca_capstone.potlatch.application.PotlatchApp;

import java.io.Serializable;
import java.util.Locale;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGD;

/**
 * Created by nektario on 10/16/2014.
 */
public class UserAccount implements BaseUser, Parcelable, Serializable {
    @SerializedName("id")
    private long mId;
    @SerializedName("email")
    private String mEmail;
    @SerializedName("profileId")
    private String mProfileId;
    @SerializedName("profileName")
    private String mProfileName;
    @SerializedName("profileUrl")
    private String mProfileUrl;
    @SerializedName("profilePhotoUrl")
    private String mProfilePhotoUrl;
    @SerializedName("profileCoverPhotoUrl")
    private String mProfileCoverPhotoUrl;
    @SerializedName("token")
    private String mToken;
    private boolean mIsLoggedIn;
    private boolean mIsSpecialAddUserUser;



    public UserAccount() {

    }

    public UserAccount(String email) {
        mEmail = email;
    }

    public UserAccount(boolean shouldMakeSpecialAddUserUser) {
        mIsSpecialAddUserUser = shouldMakeSpecialAddUserUser;
        if (shouldMakeSpecialAddUserUser) {
            mEmail = PotlatchApp.getAppContext().getResources().getString(R.string.user_add_user);
        }
    }


    public long getId() {
        return mId;
    }
    public void setId(long id) {
        mId = id;
    }

    public String getEmail() {
        return mEmail;
    }
    public void setEmail(String email) {
        mEmail = email;
    }

    public String getProfileId() {
        return mProfileId;
    }
    public void setProfileId(String profileId) {
        mProfileId = profileId;
    }

    public String getProfileUrl() {
        return mProfileUrl;
    }
    public void setProfileUrl(String profileUrl) {
        mProfileUrl = profileUrl;
    }

    public String getProfileName() {
        return mProfileName;
    }
    public void setProfileName(String profileName) {
        mProfileName = profileName;
    }

    public String getProfilePhotoUrl() {
        return mProfilePhotoUrl;
    }
    public void setProfilePhotoUrl(String profilePhotoUrl) {
        mProfilePhotoUrl = profilePhotoUrl;
    }

    public String getProfileCoverPhotoUrl() {
        return mProfileCoverPhotoUrl;
    }
    public void setProfileCoverPhotoUrl(String profilePhotoUrl) {
        mProfileCoverPhotoUrl = profilePhotoUrl;
    }

    public String getToken() {
        return mToken;
    }
    public void setToken(String token) {
        mToken = token;
    }

    public boolean isLoggedIn() {
        return mIsLoggedIn;
    }
    public void setLoggedIn(boolean isLoggedIn) {
        mIsLoggedIn = isLoggedIn;
    }

    public boolean isSpecialUser() {
        return mIsSpecialAddUserUser;
    }
    public boolean hasLoggedInBefore() {
        return mToken != null;
    }


    @Override
    public boolean equals(Object obj) {
        return obj != null
                && obj instanceof UserAccount
                && ((UserAccount) obj).getEmail().toLowerCase(Locale.US).equals(mEmail);
    }

    @Override
    public String toString() {
        return mEmail+":"+mProfileName;
    }


    // Parcelable
    public UserAccount(Parcel in) {
        mId = in.readLong();
        mEmail = in.readString();
        mProfileId = in.readString();
        mProfileUrl = in.readString();
        mProfileName = in.readString();
        mProfilePhotoUrl = in.readString();
        mProfileCoverPhotoUrl = in.readString();
        mToken = in.readString();
        mIsLoggedIn = in.readInt() == 1;
        mIsSpecialAddUserUser = in.readInt() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(mId);
        out.writeString(mEmail);
        out.writeString(mProfileId);
        out.writeString(mProfileUrl);
        out.writeString(mProfileName);
        out.writeString(mProfilePhotoUrl);
        out.writeString(mProfileCoverPhotoUrl);
        out.writeString(mToken);
        out.writeInt(mIsLoggedIn ? 1 : 0);
        out.writeInt(mIsSpecialAddUserUser ? 1 : 0);
    }

    public static final Parcelable.Creator<UserAccount> CREATOR = new Parcelable.Creator<UserAccount>() {
        public UserAccount createFromParcel(Parcel in) {
            return new UserAccount(in);
        }

        public UserAccount[] newArray(int size) {
            return new UserAccount[size];
        }
    };
}

package com.mocca_capstone.potlatch.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nektario on 10/16/2014.
 */
public class RegistrationUser {
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


    public RegistrationUser() {

    }

    public RegistrationUser(UserAccount user) {
        mEmail = user.getEmail();
        mProfileId = user.getProfileId();
        mProfileName = user.getProfileName();
        mProfileUrl = user.getProfileUrl();
        mProfilePhotoUrl = user.getProfilePhotoUrl();
        mProfileCoverPhotoUrl = user.getProfileCoverPhotoUrl();
        mToken = user.getToken();
    }
}

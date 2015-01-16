package com.mocca_capstone.potlatch.models;

import java.io.Serializable;

/**
 * Created by nektario on 11/2/2014.
 */
public interface BaseUser extends Serializable {
    public long getId();
    public String getProfileName();
    public String getProfilePhotoUrl();
}

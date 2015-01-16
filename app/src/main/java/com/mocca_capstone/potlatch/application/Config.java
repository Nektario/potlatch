package com.mocca_capstone.potlatch.application;

import com.mocca_capstone.potlatch.BuildConfig;

import retrofit.RestAdapter;

/**
 * Created by nektario on 10/13/2014.
 */
public interface Config {
    public static final String SERVER_CLIENT_ID =
                                    "744180979605-0bcteiakvrpl03d9acbbch1iesvsi0pt.apps.googleusercontent.com";
    public static final String API_ENDPOINT = "https://192.168.1.115:8443";
    public static final String API_KEYSTORE_PASSWORD = "changeit";
    public static final String USER_AGENT = "Potlatch/1.0";

    public static final float BITMAP_MEM_CACHE_PERCENT = 0.20f; // 20%
    public static final long BITMAP_DISK_CACHE_BYTES_MAX = 75 * 1024 * 1024; // 75MB

    public static final RestAdapter.LogLevel RETROFIT_LOG_LEVEL = BuildConfig.SHOULD_LOG ?
                                                                  RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE;
    public static final boolean PICASSO_LOG = false;
    public static final boolean PICASSO_INDICATORS = false;

    // sync adapter
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "com.mocca_capstone.potlatch.sync";
    // The account name
    public static final String ACCOUNT_NAME = "dummyaccount";
}

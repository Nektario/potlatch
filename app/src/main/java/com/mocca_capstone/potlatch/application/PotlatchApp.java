package com.mocca_capstone.potlatch.application;

import android.accounts.Account;
import android.app.Application;
import android.content.Context;

/**
 * Created by nektario on 10/13/2014.
 */
public class PotlatchApp extends Application {
    private static PotlatchApp mApp;
    private Account mSyncAccount;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        Injector.getInstance().initialize(this);
    }

    public static Context getAppContext() {
        return mApp.getApplicationContext();
    }

    public static void setSyncAccount(Account account) {
        mApp.mSyncAccount = account;
    }

    public static Account getSyncAccount() {
        return mApp.mSyncAccount;
    }
}

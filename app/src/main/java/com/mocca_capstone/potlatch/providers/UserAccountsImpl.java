package com.mocca_capstone.potlatch.providers;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.gson.reflect.TypeToken;
import com.mocca_capstone.potlatch.application.ForApplication;
import com.mocca_capstone.potlatch.application.Injector;
import com.mocca_capstone.potlatch.events.EventUserAccountDeleted;
import com.mocca_capstone.potlatch.listeners.OnUserDeletedListener;
import com.mocca_capstone.potlatch.models.UserAccount;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGD;

/**
 * Created by nektario on 10/16/2014.
 */
public class UserAccountsImpl implements UserAccounts {
    private StorageProvider<UserAccount> mStorageProvider;
    private List<UserAccount> mUserAccounts;
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;


    @Inject
    public UserAccountsImpl(@ForApplication Context appContext) {
        Injector.getInstance().inject(this);
        mContext = appContext;
        mStorageProvider = new FileProvider<UserAccount>(appContext,
                                                         "useraccounts",
                                                         new TypeToken<List<UserAccount>>() {}.getType());
        loadUsers();
    }

    @Override
    public UserAccount getActiveUser() {
        for (UserAccount user : mUserAccounts) {
            if (user.isLoggedIn()) {
                return user;
            }
        }
        return null;
    }

    @Override
    public boolean isLoggedIn() {
        return getActiveUser() != null;
    }

    @Override
    public boolean isUser(UserAccount user) {
        return mUserAccounts.contains(user);
    }

    @Override
    public void loginAs(UserAccount user) {
        if (!mUserAccounts.contains(user)) {
            mUserAccounts.add(user);
        }

        for (UserAccount userAccount : mUserAccounts) {
            if (userAccount.equals(user)) {
                userAccount.setLoggedIn(true);
            } else {
                // log all other users out
                userAccount.setLoggedIn(false);
            }
        }

        saveUsers();
    }

    @Override
    public List<UserAccount> getUsers() {
        return new ArrayList<UserAccount>(mUserAccounts);
    }

    @Override
    public void addUser(UserAccount user) {
        if (!mUserAccounts.contains(user)) {
            mUserAccounts.add(user);
            saveUsers();
        }
    }

    @Override
    public void addUsers(List<UserAccount> users) {
        boolean added = false;
        for (UserAccount user : users) {
            if (!mUserAccounts.contains(user)) {
                mUserAccounts.add(user);
                added = true;
            }
        }

        if (added) {
            saveUsers();
        }
    }

    @Override
    public void deleteUser(UserAccount user, final OnUserDeletedListener listener) {
        if (mUserAccounts.contains(user)) {
            mUserAccounts.remove(user);

            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        LOGD("UserAccountsImpl", "onConnected()");
                        if (mGoogleApiClient.isConnected()) {
                            LOGD("UserAccountsImpl", "isConnected Going to delete");
                            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
                            mGoogleApiClient.disconnect();
                            listener.onUserDeleted();
                        }
                    }
                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .setAccountName(user.getEmail())
                .build();
            mGoogleApiClient.connect();
        }
    }

    public void saveUsers() {
        mStorageProvider.save(mUserAccounts);
    }

    private void loadUsers() {
        long start = SystemClock.elapsedRealtime();
        mUserAccounts = mStorageProvider.load();
        long end = SystemClock.elapsedRealtime();
        long diff = end - start;
        LOGD("UserProvider", "Time to load users: " + diff + "ms");
    }
}

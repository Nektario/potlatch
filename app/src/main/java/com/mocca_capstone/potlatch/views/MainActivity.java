package com.mocca_capstone.potlatch.views;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

import com.mocca_capstone.potlatch.R;
import com.mocca_capstone.potlatch.application.Config;
import com.mocca_capstone.potlatch.application.Injector;
import com.mocca_capstone.potlatch.application.PotlatchApp;
import com.mocca_capstone.potlatch.contentprovider.PotlatchProviderHelper;
import com.mocca_capstone.potlatch.widgets.EndlessCollectionView;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGD;

/**
 * Created by nektario on 10/13/2014.
 */
public class MainActivity extends NavDrawerActivity {
    private static final String TAG = "MainActivity";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift_chain_list);
        Injector.getInstance().inject(this);

        initializeActionBarToolbar(false, true);
        if (savedInstanceState == null) {
            launchGiftChains();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        LOGD(TAG, "onPostCreate");
        enableActionBarAutoHide((EndlessCollectionView) findViewById(R.id.list), false);
        doFirstActivityToLaunchTasks();
        doFirstTimeRunningTasks();
    }

    private void doFirstActivityToLaunchTasks() {
        if (isTaskRoot()) {
            PotlatchApp.setSyncAccount(createSyncAccount(this));
        }
    }

    private void doFirstTimeRunningTasks() {
        if (mAppPrefs.isFirstTimeRunning()) {
            mAppPrefs.initializeDefaultValues();
            openNavDrawer();    // show navdrawer on first app launch so that it's discoverable by the user
            bootstrapInitialGiftData();
            mAppPrefs.setFirstTimeRunComplete();
        }
    }

    public void bootstrapInitialGiftData() {
        PotlatchProviderHelper.syncNow();
    }

    protected NavItem getSelfNavDrawerItem() {
        return NavItem.HOME;
    }

    public Account createSyncAccount(Context context) {
        Account newAccount = new Account(Config.ACCOUNT_NAME, Config.ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            if (mAppPrefs.getSyncInterval() > 0) {
                PotlatchProviderHelper.enablePeriodicSync(newAccount, mAppPrefs.getSyncInterval());
            }
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }

        return newAccount;
    }
}

package com.mocca_capstone.potlatch.views;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.mocca_capstone.potlatch.R;
import com.mocca_capstone.potlatch.application.Injector;
import com.mocca_capstone.potlatch.application.PotlatchApp;
import com.mocca_capstone.potlatch.contentprovider.PotlatchProviderHelper;
import com.mocca_capstone.potlatch.events.EventHideFlaggedGiftsPrefChanged;
import com.mocca_capstone.potlatch.providers.AppPrefs;
import com.mocca_capstone.potlatch.providers.EventBus;

import javax.inject.Inject;

/**
 * Created by nektario on 10/22/2014.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Inject AppPrefs mAppPrefs;
    @Inject EventBus mEventBus;


    public SettingsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.getInstance().inject(this);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_key_user_sync_frequency))) {
            int frequency = mAppPrefs.getSyncInterval();
            if (frequency > 0) {
                PotlatchProviderHelper.enablePeriodicSync(PotlatchApp.getSyncAccount(), frequency);
            } else {
                PotlatchProviderHelper.disablePeriodicSync(PotlatchApp.getSyncAccount());
            }
        } else if (key.equals(getString(R.string.pref_key_user_hide_flagged_gifts))) {
            mEventBus.post(new EventHideFlaggedGiftsPrefChanged());
        }
    }
}

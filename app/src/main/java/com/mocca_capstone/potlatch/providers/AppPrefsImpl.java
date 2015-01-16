package com.mocca_capstone.potlatch.providers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.mocca_capstone.potlatch.R;

/**
 * Created by nektario on 6/1/2014.
 */
public class AppPrefsImpl implements AppPrefs {
    private static final String TAG = "AppPrefsImpl";
    private SharedPreferences mSharedPrefs;
    private SharedPreferences.Editor mEditor;
    private Context mContext;


    public AppPrefsImpl(Context ctx) {
        mContext = ctx;
        mSharedPrefs = ctx.getSharedPreferences(ctx.getString(R.string.pref_app_prefs_file_name), Context.MODE_PRIVATE);
    }

    @Override
    public void initializeDefaultValues() {
        PreferenceManager.setDefaultValues(mContext, mContext.getString(R.string.pref_app_prefs_file_name),
                                           Context.MODE_PRIVATE, R.xml.preferences, false);
    }


    // User prefs
    @Override
    public boolean hideFlaggedGifts() {
        return mSharedPrefs.getBoolean(mContext.getString(R.string.pref_key_user_hide_flagged_gifts), true);
    }

    @Override
    public int getSyncInterval() {
        return Integer.valueOf(mSharedPrefs.getString(mContext.getString(R.string.pref_key_user_sync_frequency), "60"));
    }


    // Internal App items
    @Override
    public boolean isFirstTimeRunning() {
        return mSharedPrefs.getBoolean(mContext.getString(R.string.pref_key_app_is_first_time_running), true);
    }
    @Override
    public void setFirstTimeRunComplete() {
        prepareEditor();
        mEditor.putBoolean(mContext.getString(R.string.pref_key_app_is_first_time_running), false);
        save();
    }

    private void prepareEditor() {
        mEditor = mSharedPrefs.edit();
    }

    private void save() {
        mEditor.apply();
    }
}

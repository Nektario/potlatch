package com.mocca_capstone.potlatch.widgets;

import android.content.Context;
import android.preference.ListPreference;
import android.preference.Preference;
import android.util.AttributeSet;

/**
 * Created by nektario on 10/23/2014.
 */
public class ListPreferenceWithSummary extends ListPreference {
    private final static String TAG = "ListPreferenceWithSummary";
    private OnPreferenceChangeListener mListener;

    public ListPreferenceWithSummary(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ListPreferenceWithSummary(Context context) {
        super(context);
        init();
    }

    private void init() {
        mListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary(getEntry());
                return true;
            }
        };
        setOnPreferenceChangeListener(mListener);
    }

    @Override
    public CharSequence getSummary() {
        return super.getEntry();
    }
}

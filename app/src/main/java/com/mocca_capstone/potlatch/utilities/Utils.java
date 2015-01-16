package com.mocca_capstone.potlatch.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.mocca_capstone.potlatch.application.PotlatchApp;

/**
 * Created by nektario on 10/13/2014.
 */
public class Utils {

    public static int calculateMemCacheSize(float percent) {
        if (percent < 0.05f || percent > 0.8f) {
            throw new IllegalArgumentException("setMemCacheSizePercent - percent must be "
                    + "between 0.05 and 0.8 (inclusive)");
        }
        return Math.round(percent * Runtime.getRuntime().maxMemory());
    }

    public static void setAccessibilityIgnore(View view) {
        view.setClickable(false);
        view.setFocusable(false);
        view.setContentDescription("");
        if (DeviceUtils.hasJellyBean()) {
            view.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        }
    }

    public static boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) PotlatchApp.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}

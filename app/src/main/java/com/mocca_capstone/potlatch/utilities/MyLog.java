package com.mocca_capstone.potlatch.utilities;

import android.util.Log;

/**
 * Created by nektario on 10/11/2014.
 */
public class MyLog {

    public static void LOGD(String tag, String text) {
        Log.d(tag, text);
    }
    public static void LOGWTF(String tag, String text) {
        Log.wtf(tag, text);
    }
}

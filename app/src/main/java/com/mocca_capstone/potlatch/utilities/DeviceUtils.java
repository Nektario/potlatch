package com.mocca_capstone.potlatch.utilities;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.mocca_capstone.potlatch.R;
import com.mocca_capstone.potlatch.application.PotlatchApp;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGD;

public class DeviceUtils {
    private static final String TAG = "DeviceUtils";
    private static final int[] RES_IDS_ACTION_BAR_SIZE = { R.attr.toolbarActionbarSize };

    public static int getScreenWidthDp() {
        //MyLog.d(TAG, "Current screen width dp: " + PotlatchApp.getAppContext().getResources().getConfiguration().screenWidthDp);
        return PotlatchApp.getAppContext().getResources().getConfiguration().screenWidthDp;
    }
    public static int getScreenHeightDp() {
        //MyLog.d(TAG, "Current screen width dp: " + PotlatchApp.getAppContext().getResources().getConfiguration().screenWidthDp);
        return PotlatchApp.getAppContext().getResources().getConfiguration().screenHeightDp;
    }
    public static int getScreenWidthPx() {
        return PotlatchApp.getAppContext().getResources().getDisplayMetrics().widthPixels;
    }
    public static int getScreenHeightPx() {
        return PotlatchApp.getAppContext().getResources().getDisplayMetrics().heightPixels;
    }

    public static boolean isLandscape() {
        return getCurrentOrientation() == Configuration.ORIENTATION_LANDSCAPE;
    }
    private static int getCurrentOrientation() {
        //MyLog.d(TAG, "Current screen orientation: " + PotlatchApp.getAppContext().getResources().getConfiguration().orientation);
        return PotlatchApp.getAppContext().getResources().getConfiguration().orientation;
    }

    public static int calculateActionBarSizeInPixels(Context context) {
        if (context == null) {
            return 0;
        }

        Resources.Theme curTheme = context.getTheme();
        if (curTheme == null) {
            return 0;
        }

        TypedArray att = curTheme.obtainStyledAttributes(RES_IDS_ACTION_BAR_SIZE);
        if (att == null) {
            return 0;
        }
        float size = att.getDimension(0, 0);
        att.recycle();
        return (int) size;
    }

    public static int getBiggestScreenDimensionPx() {
        WindowManager wm = (WindowManager) PotlatchApp.getAppContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size.x >= size.y ? size.x : size.y;
    }

    public static float convertPixelsToDp(float px){
        Resources resources = PotlatchApp.getAppContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / (metrics.densityDpi / 160f);
    }

    public static int convertDpToPixel(int dp){
        Resources resources = PotlatchApp.getAppContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (int) (dp * (metrics.densityDpi / 160f));
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasJellyBeanMr1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }
}

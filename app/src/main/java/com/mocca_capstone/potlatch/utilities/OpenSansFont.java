package com.mocca_capstone.potlatch.utilities;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nektario on 3/29/14.
 */
public class OpenSansFont {
    public static enum OpenSansFontType { LIGHT, BOLD, REGULAR, THIN, MEDIUM, BLACK, CONDENSED_BOLD, CONDENSED_LIGHT }
    private static Map<OpenSansFontType, Typeface> typefaceCache = new HashMap<OpenSansFontType, Typeface>();

    public static Map<OpenSansFontType, String> fontMap = new HashMap<OpenSansFontType, String>();
    static {
        fontMap.put(OpenSansFontType.LIGHT, "fonts/OpenSans-Light.ttf");
        fontMap.put(OpenSansFontType.BOLD, "fonts/OpenSans-Bold.ttf");
        fontMap.put(OpenSansFontType.REGULAR, "fonts/OpenSans-Regular.ttf");
        fontMap.put(OpenSansFontType.MEDIUM, "fonts/OpenSans-Medium.ttf");
        fontMap.put(OpenSansFontType.CONDENSED_BOLD, "fonts/OpenSansCondensed-Bold.ttf");
        fontMap.put(OpenSansFontType.CONDENSED_LIGHT, "fonts/OpenSansCondensed-Light.ttf");
    }


    public static Typeface getOpenSansTypeface(Context context, OpenSansFontType fontType) {

        if (!typefaceCache.containsKey(fontType)) {
            typefaceCache.put(fontType, Typeface.createFromAsset(context.getAssets(), fontMap.get(fontType)));
        }

        return typefaceCache.get(fontType);
    }

    public static void setLight(Context context, View view) {
        if (view instanceof TextView) {
            ((TextView) view).setTypeface(getOpenSansTypeface(context, OpenSansFontType.LIGHT));
        }
    }
    public static void setRegular(Context context, View view) {
        if (view instanceof TextView) {
            ((TextView) view).setTypeface(getOpenSansTypeface(context, OpenSansFontType.REGULAR));
        }
    }
    public static void setMedium(Context context, View view) {
        if (view instanceof TextView) {
            ((TextView) view).setTypeface(getOpenSansTypeface(context, OpenSansFontType.MEDIUM));
        }
    }
    public static void setBold(Context context, View view) {
        if (view instanceof TextView) {
            ((TextView) view).setTypeface(getOpenSansTypeface(context, OpenSansFontType.BOLD));
        }
    }
    public static void setCondensedBold(Context context, View view) {
        if (view instanceof TextView) {
            ((TextView) view).setTypeface(getOpenSansTypeface(context, OpenSansFontType.CONDENSED_BOLD));
        }
    }
    public static void setCondensedLight(Context context, View view) {
        if (view instanceof TextView) {
            ((TextView) view).setTypeface(getOpenSansTypeface(context, OpenSansFontType.CONDENSED_LIGHT));
        }
    }
}

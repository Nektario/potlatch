package com.mocca_capstone.potlatch.utilities;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nektario on 10/8/2014.
 */
public class RobotoSlabFont {
    public static enum FontType { REGULAR, LIGHT }
    private static Map<FontType, Typeface> typefaceCache = new HashMap<FontType, Typeface>();

    public static Map<FontType, String> fontMap = new HashMap<FontType, String>();
    static {
        fontMap.put(FontType.REGULAR, "fonts/RobotoSlab-Regular.ttf");
        fontMap.put(FontType.LIGHT, "fonts/RobotoSlab-Light.ttf");
    }


    public static Typeface getTypeFace(Context context, FontType fontType) {

        if (!typefaceCache.containsKey(fontType)) {
            typefaceCache.put(fontType, Typeface.createFromAsset(context.getAssets(), fontMap.get(fontType)));
        }

        return typefaceCache.get(fontType);
    }

    public static void set(Context context, View view, FontType fontType) {
        if (view instanceof TextView) {
            ((TextView) view).setTypeface(getTypeFace(context, fontType));
        }
    }
    public static void setLight(Context context, View view) {
        if (view instanceof TextView) {
            ((TextView) view).setTypeface(getTypeFace(context, FontType.LIGHT));
        }
    }
    public static void setRegular(Context context, View view) {
        if (view instanceof TextView) {
            ((TextView) view).setTypeface(getTypeFace(context, FontType.REGULAR));
        }
    }
}

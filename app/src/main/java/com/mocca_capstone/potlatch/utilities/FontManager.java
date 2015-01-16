package com.mocca_capstone.potlatch.utilities;

import android.content.Context;
import android.widget.TextView;

/**
 * Created by nektario on 5/17/2014.
 */
public class FontManager {

    public static void setLight(Context context, TextView view) {
        setLight(context, view, 0);
    }
    public static void setLight(Context context, TextView view, float scaleX) {
        OpenSansFont.setLight(context, view);
        if (scaleX != 0) {
            view.setTextScaleX(scaleX);
        }
    }

    public static void setRegular(Context context, TextView view) {
        setRegular(context, view, 0);
    }
    public static void setRegular(Context context, TextView view, float scaleX) {
        OpenSansFont.setRegular(context, view);
        if (scaleX != 0) {
            view.setTextScaleX(scaleX);
        }
    }

    public static void setMedium(Context context, TextView view) {
        setMedium(context, view, 0);
    }
    public static void setMedium(Context context, TextView view, float scaleX) {
        OpenSansFont.setMedium(context, view);
        if (scaleX != 0) {
            view.setTextScaleX(scaleX);
        }
    }

    public static void setBold(Context context, TextView view) {
        setBold(context, view, 0);
    }
    public static void setBold(Context context, TextView view, float scaleX) {
        OpenSansFont.setBold(context, view);
        if (scaleX != 0) {
            view.setTextScaleX(scaleX);
        }
    }

    public static void setCondensedLight(Context context, TextView view) {
        setCondensedLight(context, view, 0);
    }
    public static void setCondensedLight(Context context, TextView view, float scaleX) {
        OpenSansFont.setCondensedLight(context, view);
        if (scaleX != 0) {
            view.setTextScaleX(scaleX);
        }
    }

    public static void setCondensedBold(Context context, TextView view) {
        setCondensedBold(context, view, 0);
    }
    public static void setCondensedBold(Context context, TextView view, float scaleX) {
        OpenSansFont.setCondensedBold(context, view);
        if (scaleX != 0) {
            view.setTextScaleX(scaleX);
        }
    }

    public static void setSerifRegular(Context context, TextView view) {
        setSerifRegular(context, view, 0);
    }
    public static void setSerifRegular(Context context, TextView view, float scaleX) {
        RobotoSlabFont.setRegular(context, view);
        if (scaleX != 0) {
            view.setTextScaleX(scaleX);
        }
    }

    public static void setSerifLight(Context context, TextView view) {
        setSerifLight(context, view, 0);
    }
    public static void setSerifLight(Context context, TextView view, float scaleX) {
        RobotoSlabFont.setLight(context, view);
        if (scaleX != 0) {
            view.setTextScaleX(scaleX);
        }
    }
}

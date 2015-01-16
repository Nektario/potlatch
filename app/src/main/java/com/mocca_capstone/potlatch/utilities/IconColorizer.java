package com.mocca_capstone.potlatch.utilities;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;

import com.mocca_capstone.potlatch.R;

/**
 * Created by nektario on 10/19/2014.
 */
public class IconColorizer {
    private final int mTouchedColor;
    private final int mFlaggedColor;
    private final int mFollowedUserColor;
    private final int mNotActiveColor;
    private final int mWhite;

    public IconColorizer(Context context) {
        int touchedColorResource = context.getResources().getColor(R.color.touched_icon_selected_tint);
        mTouchedColor = Color.parseColor("#" + Integer.toHexString(touchedColorResource));

        int flaggedColorResource = context.getResources().getColor(R.color.flagged_icon_selected_tint);
        mFlaggedColor = Color.parseColor("#" + Integer.toHexString(flaggedColorResource));

        int followedUserColorResource = context.getResources().getColor(R.color.followed_user_icon_selected_tint);
        mFollowedUserColor = Color.parseColor("#" + Integer.toHexString(followedUserColorResource));

        int notTouchedColorResource = context.getResources().getColor(R.color.icon_unselected_tint);
        mNotActiveColor = Color.parseColor("#"+Integer.toHexString(notTouchedColorResource));

        int whiteColorResource = context.getResources().getColor(R.color.color__white);
        mWhite = Color.parseColor("#"+Integer.toHexString(whiteColorResource));
    }

    public void setWhite(ImageView icon) {
        icon.setColorFilter(mWhite);
    }

    public void setTouchedColor(ImageView icon) {
        icon.setColorFilter(mTouchedColor);
    }

    public void setFlaggedColor(ImageView icon) {
        icon.setColorFilter(mFlaggedColor);
    }

    public void setFollowedColor(ImageView icon) {
        icon.setColorFilter(mFollowedUserColor);
    }

    public void setNoColor(ImageView icon) {
        icon.setColorFilter(mNotActiveColor);
    }
}

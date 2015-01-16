package com.mocca_capstone.potlatch.utilities;

import android.app.Activity;
import android.view.View;

/**
 * Created by nektario on 11/8/2014.
 */
public class SystemUiHider {
    public enum UiMode { LEAN_BACK, IMMERSIVE, IMMERSIVE_STICKY }

    private Activity mActivity;
    private View mAnchorView;
    private boolean mIsVisible = true;
    private boolean mHideOnlyInLandscape;
    private int mHideFlags;
    private static int mTestFlags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    private static int mShowFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
    private OnVisibilityChangeListener mOnVisibilityChangeListener = sDummyListener;


    public SystemUiHider(Activity activity, View anchorView, UiMode mode, boolean hideOnlyInLandscape) {
        mActivity = activity;
        mAnchorView = anchorView;
        mHideOnlyInLandscape = hideOnlyInLandscape;
        mActivity.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int isVisible) {
                if ((isVisible & mTestFlags) != 0) {
                    mOnVisibilityChangeListener.onVisibilityChange(false);
                    mIsVisible = false;
                } else {
                    mOnVisibilityChangeListener.onVisibilityChange(true);
                    mIsVisible = true;
                }
            }
        });

        switch (mode) {
            case LEAN_BACK :
                setLeanBackFlags();
                break;
            case IMMERSIVE :
                setImmersiveFlags();
                break;
            case IMMERSIVE_STICKY :
                setImmersiveStickyFlags();
                break;
        }
    }

    public void setDontHideNavigation() {
        mHideFlags =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE // api 16
                        | View.SYSTEM_UI_FLAG_FULLSCREEN    // api 16
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN; // api 16
        mAnchorView.setSystemUiVisibility(mHideFlags);
    }

    private void setLeanBackFlags() {
        mHideFlags =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE // api 16
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // api 14
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // api 16
                        | View.SYSTEM_UI_FLAG_FULLSCREEN    // api 16
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN; // api 16
    }
    private void setImmersiveFlags() {
        setLeanBackFlags();
        mHideFlags ^= View.SYSTEM_UI_FLAG_IMMERSIVE; // api 19
    }
    private void setImmersiveStickyFlags() {
        setLeanBackFlags();
        mHideFlags ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY; // api 19
    }


    /**
     * Returns whether or not the system UI is visible.
     */
    public boolean isVisible() {
        return mIsVisible;
    }

    public void hide() {
        if (mHideOnlyInLandscape) {
            if (DeviceUtils.isLandscape()) {
                mAnchorView.setSystemUiVisibility(mHideFlags);
            }
        } else {
            mAnchorView.setSystemUiVisibility(mHideFlags);
        }
    }

    public void show() {
        mAnchorView.setSystemUiVisibility(mShowFlags);
    }

    /**
     * Toggle the visibility of the system UI.
     */
    public void toggle() {
        if (isVisible()) {
            hide();
        } else {
            show();
        }
    }

    /**
     * Registers a callback, to be triggered when the system UI visibility
     * changes.
     */
    public void setOnVisibilityChangeListener(OnVisibilityChangeListener listener) {
        if (listener == null) {
            listener = sDummyListener;
        }

        mOnVisibilityChangeListener = listener;
    }

    /**
     * A dummy no-op callback for use when there is no other listener set.
     */
    private static OnVisibilityChangeListener sDummyListener = new OnVisibilityChangeListener() {
        @Override
        public void onVisibilityChange(boolean visible) {
        }
    };

    /**
     * A callback interface used to listen for system UI visibility changes.
     */
    public interface OnVisibilityChangeListener {
        /**
         * Called when the system UI visibility has changed.
         *
         * @param visible True if the system UI is visible.
         */
        public void onVisibilityChange(boolean visible);
    }
}


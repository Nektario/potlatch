package com.mocca_capstone.potlatch.widgets;

import android.animation.Animator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.melnykov.fab.FloatingActionButton;
import com.mocca_capstone.potlatch.R;
import com.mocca_capstone.potlatch.utilities.DeviceUtils;

/**
 * Created by nektario on 11/3/2014.
 */
public class AddGiftFab extends FloatingActionButton {
    private static final float ROTATION_DEGREES = 180f;
    private static final int BUTTON_ANIMATION_DURATION = 250;
    private static final int GALLERY_BUTTON_TRANSLATION_X = 80;
    private FloatingActionButton mGalleryButton;
    private Handler mHandler = new Handler();


    public AddGiftFab(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public AddGiftFab(Context context) {
        super(context);
        init();
    }
    public AddGiftFab(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    private void init() {

    }

    public void setGalleryButton(FloatingActionButton galleryButton, OnClickListener listener) {
        mGalleryButton = galleryButton;
        mGalleryButton.setOnClickListener(listener);
    }


    public void expand() {
        animateGalleryButtonIn();
        morphIntoCameraButton();
    }
    private void animateGalleryButtonIn() {
        mGalleryButton.setVisibility(View.VISIBLE);
        mGalleryButton.setAlpha(0f);
        mGalleryButton.animate()
                .alpha(1)
                .translationX(-DeviceUtils.convertDpToPixel(GALLERY_BUTTON_TRANSLATION_X))
                .setDuration(BUTTON_ANIMATION_DURATION)
                .setInterpolator(new DecelerateInterpolator());
    }
    private void morphIntoCameraButton() {
        animate().rotationBy(-ROTATION_DEGREES);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setImageResource(R.drawable.ic_action_camera);
            }
        }, BUTTON_ANIMATION_DURATION / 2);
    }



    public void collapse() {
        animateGalleryButtonOut();
        morphIntoToAddGiftButton();
    }
    private void animateGalleryButtonOut() {
        if (mGalleryButton.getVisibility() == View.VISIBLE) {
            mGalleryButton.setAlpha(1f);
            mGalleryButton.animate()
                    .alpha(0)
                    .translationX(0)
                    .setDuration(BUTTON_ANIMATION_DURATION)
                    .setInterpolator(new DecelerateInterpolator());
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mGalleryButton.setVisibility(View.GONE);
                }
            }, BUTTON_ANIMATION_DURATION);
        }
    }
    private void morphIntoToAddGiftButton() {
        animate().rotationBy(ROTATION_DEGREES);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setImageResource(R.drawable.ic_action_new_gift);
            }
        }, BUTTON_ANIMATION_DURATION / 2);
    }


    public void collapseInstantly() {
        hideGalleryButton();
        switchToAddGiftButton();
    }
    private void hideGalleryButton() {
        if (mGalleryButton.getVisibility() == View.VISIBLE) {
            mGalleryButton.setVisibility(View.GONE);
            mGalleryButton.setTranslationX(0);
        }
    }
    private void switchToAddGiftButton() {
        setImageResource(R.drawable.ic_action_new_gift);
    }



    public boolean isExpanded() {
        return hasExpandableButtons() && mGalleryButton.getVisibility() == View.VISIBLE;
    }

    private boolean hasExpandableButtons() {
        return mGalleryButton != null;
    }
}

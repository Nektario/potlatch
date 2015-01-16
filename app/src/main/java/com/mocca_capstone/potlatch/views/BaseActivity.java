package com.mocca_capstone.potlatch.views;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;

import com.mocca_capstone.potlatch.R;
import com.mocca_capstone.potlatch.application.Injector;
import com.mocca_capstone.potlatch.providers.AppPrefs;
import com.mocca_capstone.potlatch.providers.UserAccounts;
import com.mocca_capstone.potlatch.widgets.EndlessCollectionView;

import java.util.ArrayList;

import javax.inject.Inject;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGD;

/**
 * Created by nektario on 10/13/2014.
 */
public abstract class BaseActivity extends ActionBarActivity {
    private static final String TAG = "BaseActivity";
    protected static final int MAIN_CONTENT_FADEIN_DURATION = 500;
    private static final int HIDEABLE_HEADER_VIEW_ANIM_DURATION = 300;

    // variables that control the Action Bar auto hide behavior (aka "quick recall")
    private ArrayList<View> mHideableHeaderViews = new ArrayList<View>();
    private int mActionBarAutoHideSensivity = 0;
    private int mActionBarAutoHideMinY = 0;
    private int mActionBarAutoHideSignal = 0;
    private boolean mActionBarAutoHideAtTop = false;
    protected boolean mActionBarShown = true;

    @Inject AppPrefs mAppPrefs;
    @Inject UserAccounts mUserAccounts;

    // Primary toolbar and drawer toggle
    protected Toolbar mActionBarToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.getInstance().inject(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }*/

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        fadeMainContent();
    }

    protected void fadeMainContent() {
        View mainContent = findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.setAlpha(0);
            mainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
        } else {
            LOGD(TAG, "No view with ID main_content to fade in.");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LOGD(TAG, "ONOPTIONSITEMSSELECT");
        switch(item.getItemId()) {
            case R.id.action_settings:
                startActivity(SettingsActivity.newInstance(this));
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void initializeActionBarToolbar(boolean shouldStartHidden, boolean setAsActionBar) {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            if (mActionBarToolbar != null) {
                if (setAsActionBar) {
                    setSupportActionBar(mActionBarToolbar);
                }

                if (shouldStartHidden) {
                    animateHeaderView(mActionBarToolbar, -mActionBarToolbar.getBottom(), 0, 0);
                    mActionBarShown = false;
                }
            }

        }
    }

    protected Toolbar getActionBarToolbar() {
        return mActionBarToolbar;
    }

    public void cancelPendingActivityAnimations() {
        overridePendingTransition(0, 0);
    }


    /*
     *
     * Actionbar Toolbar auto show/hide
     *
     */
    protected boolean isActionBarVisible() {
        return mActionBarShown;
    }

    public void hideActionBar() {
        if (isActionBarVisible()) {
            autoShowOrHideActionBar(false);
            mActionBarAutoHideSignal = 0;
        }
    }

    public void showActionBar() {
        if (!isActionBarVisible()) {
            autoShowOrHideActionBar(true);
        }
    }

    private boolean shouldHideAtTopOfList() {
        return mActionBarAutoHideAtTop;
    }

    private boolean isListAtTop(AbsListView view) {
        return view.getChildAt(0).getTop() == 0;
    }

    protected void enableActionBarAutoHide(final EndlessCollectionView endlessCollectionView, boolean hideAtTop) {
        initActionBarAutoHide(hideAtTop);
        registerHideableHeaderView(mActionBarToolbar);
        endlessCollectionView.addOnScrollListener(new AbsListView.OnScrollListener() {
            final static int ITEMS_THRESHOLD = 1;
            int lastFvi = 0;
            boolean isScrollStateIdle = true;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                isScrollStateIdle = scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (isScrollStateIdle) {
                    return;
                }

                if (shouldHideAtTopOfList() && isListAtTop(view)) {
                    autoShowOrHideActionBar(true);
                }

                onMainContentScrolled(firstVisibleItem <= ITEMS_THRESHOLD ? 0 : Integer.MAX_VALUE,
                        lastFvi - firstVisibleItem > 0 ? Integer.MIN_VALUE :
                                lastFvi == firstVisibleItem ? 0 : Integer.MAX_VALUE
                );
                lastFvi = firstVisibleItem;
            }
        });
    }

    private void initActionBarAutoHide(boolean hideAtTop) {
        mActionBarAutoHideAtTop = hideAtTop;
        mActionBarAutoHideMinY = getResources().getDimensionPixelSize(R.dimen.action_bar_auto_hide_min_y);
        mActionBarAutoHideSensivity = getResources().getDimensionPixelSize(R.dimen.action_bar_auto_hide_sensivity);
    }

    protected void registerHideableHeaderView(View hideableHeaderView) {
        if (!mHideableHeaderViews.contains(hideableHeaderView) && hideableHeaderView != null) {
            mHideableHeaderViews.add(hideableHeaderView);
        }
    }


    private void onMainContentScrolled(int currentY, int deltaY) {
        if (deltaY > mActionBarAutoHideSensivity) {
            deltaY = mActionBarAutoHideSensivity;
        } else if (deltaY < -mActionBarAutoHideSensivity) {
            deltaY = -mActionBarAutoHideSensivity;
        }

        if (Math.signum(deltaY) * Math.signum(mActionBarAutoHideSignal) < 0) {
            // deltaY is a motion opposite to the accumulated signal, so reset signal
            mActionBarAutoHideSignal = deltaY;
        } else {
            // add to accumulated signal
            mActionBarAutoHideSignal += deltaY;
        }

        boolean shouldShow =
                    currentY < mActionBarAutoHideMinY || (mActionBarAutoHideSignal <= -mActionBarAutoHideSensivity);
        autoShowOrHideActionBar(shouldShow);
    }

    protected void autoShowOrHideActionBar(boolean shouldShow) {
        if (shouldShow == mActionBarShown) {
            return;
        }

        mActionBarShown = shouldShow;
        for (View view : mHideableHeaderViews) {
            animateHideableHeaderView(view, shouldShow);
        }
    }

    private void animateHideableHeaderView(View view, boolean shouldShow) {
        if (shouldShow) {
            animateHeaderView(view, 0, 1, HIDEABLE_HEADER_VIEW_ANIM_DURATION);
        } else {
            animateHeaderView(view, -view.getBottom(), 0, HIDEABLE_HEADER_VIEW_ANIM_DURATION);
        }
    }

    private void animateHeaderView(View view, int distanceY, int alpha, int duration) {
        view.animate()
                .translationY(distanceY)
                .alpha(alpha)
                .setDuration(duration)
                .setInterpolator(new DecelerateInterpolator());
    }

    @Override
    public void onBackPressed() {
        final Fragment fragment = getFragmentManager().findFragmentById(R.id.main_content);
        if (fragment != null && fragment instanceof BaseGiftFragment) {
            boolean handled = ((BaseGiftFragment) fragment).onBackPressed();
            if (!handled) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }
}

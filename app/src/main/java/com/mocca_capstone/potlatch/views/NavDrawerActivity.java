package com.mocca_capstone.potlatch.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mocca_capstone.potlatch.R;
import com.mocca_capstone.potlatch.application.Injector;
import com.mocca_capstone.potlatch.events.EventUserAccountAdded;
import com.mocca_capstone.potlatch.events.EventUserAccountLoggedOut;
import com.mocca_capstone.potlatch.imageloader.ImageLoader;
import com.mocca_capstone.potlatch.models.UserAccount;
import com.mocca_capstone.potlatch.providers.EventBus;
import com.mocca_capstone.potlatch.utilities.Utils;
import com.mocca_capstone.potlatch.widgets.BezelImageView;
import com.mocca_capstone.potlatch.widgets.EndlessCollectionView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGD;

/**
 * Created by nektario on 10/16/2014.
 */
public class NavDrawerActivity extends BaseActivity {
    private static final String TAG = "NavDrawerActivity";
    @Inject EventBus mEventBus;
    @Inject ImageLoader mImageLoader;

    // User Accounts
    private static final int ACCOUNT_BOX_EXPAND_ANIM_DURATION = 150;
    private LinearLayout mAccountListContainer;
    private ImageView mExpandAccountBoxIndicator;
    private LinearLayout.LayoutParams mLoggedOutLayoutParams;
    private LinearLayout.LayoutParams mLoggedInLayoutParams;
    private Object onUserAccountsChangedListener = new Object() {
        @Subscribe
        public void onUserAccountAdded(final EventUserAccountAdded event) {
            LOGD(TAG, "New Account: Updating accounts list");
            createNavDrawerItems();
            setupUserAccountsBox();
        }

        @Subscribe
        public void onUserAccountLoggedOut(final EventUserAccountLoggedOut event) {
            LOGD(TAG, "Logout: Updating accounts list");
            createNavDrawerItems();
            setupUserAccountsBox();
        }
    };

    // Navigation drawer
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean mAccountBoxExpanded = false;
    private ViewGroup mDrawerItemsListContainer;
    private static final int NAVDRAWER_LAUNCH_DELAY = 350;
    private ArrayList<NavItem> mNavDrawerItems = new ArrayList<NavItem>();
    private View[] mNavDrawerItemViews = null;
    private Handler mHandler;
    private NavItem mCurrentNavItem;

    // list of all possible items that can be added to the Navigation Drawer
    protected enum NavItem { HOME, PROFILE, RANKINGS, FAVOURITES, FOLLOWING, SETTINGS, SEPARATOR, INVALID };
    private static final HashMap<NavItem, Integer> NAVDRAWER_TITLE_RES_ID = new HashMap<NavItem, Integer>();
    static {
        NAVDRAWER_TITLE_RES_ID.put(NavItem.HOME, R.string.navdrawer_item_home);
        NAVDRAWER_TITLE_RES_ID.put(NavItem.RANKINGS, R.string.navdrawer_item_rankings);
        NAVDRAWER_TITLE_RES_ID.put(NavItem.FAVOURITES, R.string.navdrawer_item_favourites);
        NAVDRAWER_TITLE_RES_ID.put(NavItem.FOLLOWING, R.string.navdrawer_item_following);
        NAVDRAWER_TITLE_RES_ID.put(NavItem.PROFILE, R.string.navdrawer_item_profile);
        NAVDRAWER_TITLE_RES_ID.put(NavItem.SETTINGS, R.string.navdrawer_item_settings);
    }
    // icons for navdrawer items (indices must correspond to above)
    private static final HashMap<NavItem, Integer> NAVDRAWER_ICON_RES_ID = new HashMap<NavItem, Integer>();
    static {
        NAVDRAWER_ICON_RES_ID.put(NavItem.HOME, R.drawable.ic_drawer_home);
        NAVDRAWER_ICON_RES_ID.put(NavItem.RANKINGS, R.drawable.ic_drawer_rankings);
        NAVDRAWER_ICON_RES_ID.put(NavItem.FAVOURITES, R.drawable.ic_drawer_favourites);
        NAVDRAWER_ICON_RES_ID.put(NavItem.FOLLOWING, R.drawable.ic_drawer_following);
        NAVDRAWER_ICON_RES_ID.put(NavItem.PROFILE, R.drawable.ic_drawer_profile);
        NAVDRAWER_ICON_RES_ID.put(NavItem.SETTINGS, R.drawable.ic_drawer_settings);
    }


    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        LOGD(TAG, "Saving state: " + mCurrentNavItem.toString());
        out.putString("current-nav-item", mCurrentNavItem.toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.getInstance().inject(this);
        mHandler = new Handler();
        mEventBus.register(onUserAccountsChangedListener);

        if (savedInstanceState == null) {
            mCurrentNavItem = NavItem.HOME;
        } else {
            mCurrentNavItem = NavItem.valueOf(savedInstanceState.getString("current-nav-item"));
            LOGD(TAG, "Restoring state: " + mCurrentNavItem.toString());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mEventBus.unRegister(onUserAccountsChangedListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupNavDrawer();
        setupUserAccountsBox();
    }


    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else {
            super.onBackPressed();
        }
    }


    /**
     * Returns the navigation drawer item that corresponds to this Activity. Subclasses
     * of BaseActivity override this to indicate what nav drawer item corresponds to them
     * Return NAVDRAWER_ITEM_INVALID to mean that this Activity should not have a Nav Drawer.
     */
    protected NavItem getSelfNavDrawerItem() {
        return NavItem.INVALID;
    }

    private void setupNavDrawer() {
        // What nav drawer item should be selected?
        NavItem selfItem = getSelfNavDrawerItem();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout == null) {
            return;
        }

        if (selfItem == NavItem.INVALID) {
            // do not show a nav drawer
            View navDrawer = mDrawerLayout.findViewById(R.id.navdrawer);
            if (navDrawer != null) {
                ((ViewGroup) navDrawer.getParent()).removeView(navDrawer);
            }
            mDrawerLayout = null;
            return;
        }

        mLoggedInLayoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        (int) getResources().getDimension(R.dimen.navdrawer_account_box_logged_in_height));

        mLoggedOutLayoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        (int) getResources().getDimension(R.dimen.navdrawer_account_box_logged_out_height));

        if (mActionBarToolbar != null) {
            mActionBarToolbar.setNavigationIcon(R.drawable.ic_drawer_white);
            mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerLayout.openDrawer(Gravity.START);
                }
            });

            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mActionBarToolbar,
                    R.string.drawer_open, R.string.drawer_close);
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();
        }
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);
        createNavDrawerItems();
    }

    private void createNavDrawerItems() {
        mNavDrawerItems.clear();
        mNavDrawerItems.add(NavItem.HOME);
        mNavDrawerItems.add(NavItem.RANKINGS);
        mNavDrawerItems.add(NavItem.FOLLOWING);
        if (mUserAccounts.isLoggedIn()) {
            mNavDrawerItems.add(NavItem.PROFILE);
        }
        mNavDrawerItems.add(NavItem.SEPARATOR);
        mNavDrawerItems.add(NavItem.SETTINGS);

        mDrawerItemsListContainer = (ViewGroup) findViewById(R.id.navdrawer_items_list);
        if (mDrawerItemsListContainer == null) {
            return;
        }

        mNavDrawerItemViews = new View[mNavDrawerItems.size()];
        mDrawerItemsListContainer.removeAllViews();
        int i = 0;
        for (NavItem itemId : mNavDrawerItems) {
            mNavDrawerItemViews[i] = makeNavDrawerItem(itemId, mDrawerItemsListContainer);
            mDrawerItemsListContainer.addView(mNavDrawerItemViews[i]);
            ++i;
        }
    }

    private View makeNavDrawerItem(final NavItem item, ViewGroup container) {
        boolean selected = mCurrentNavItem == item;
        int layoutToInflate;
        if (item == NavItem.SEPARATOR) {
            layoutToInflate = R.layout.navdrawer_separator;
        } else {
            layoutToInflate = R.layout.navdrawer_item;
        }
        View view = getLayoutInflater().inflate(layoutToInflate, container, false);

        if (isSeparator(item)) {
            // we are done
            Utils.setAccessibilityIgnore(view);
            return view;
        }

        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        TextView titleView = (TextView) view.findViewById(R.id.title);
        int iconId = NAVDRAWER_ICON_RES_ID.get(item);
        int titleId = NAVDRAWER_TITLE_RES_ID.get(item);

        // set icon and text
        iconView.setVisibility(iconId > 0 ? View.VISIBLE : View.GONE);
        if (iconId > 0) {
            iconView.setImageResource(iconId);
        }
        titleView.setText(getString(titleId));
        formatNavDrawerItem(view, item, selected);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavDrawerItemClicked(item);
            }
        });

        return view;
    }

    private void onNavDrawerItemClicked(final NavItem itemId) {
        if (itemId == mCurrentNavItem) {
            closeNavDrawer();
            return;
        }

        // launch the target item after a short delay, to allow the close animation to play
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goToNavDrawerItem(itemId);
            }
        }, NAVDRAWER_LAUNCH_DELAY);

        if (!doesNavItemLaunchNewActivity(itemId)) {
            setSelectedNavDrawerItem(itemId);
            mCurrentNavItem = itemId;
        }

        closeNavDrawer();
    }

    private void goToNavDrawerItem(NavItem item) {
        switch (item) {
            case HOME :
                fadeMainContent();
                launchGiftChains();
                break;
            case RANKINGS :
                fadeMainContent();
                showActionBar();
                launchRankings();
                break;
            case FOLLOWING :
                fadeMainContent();
                showActionBar();
                launchFollowedUsers();
                break;
            case PROFILE :
                fadeMainContent();
                launchAccountProfile();
                break;
            case SETTINGS :
                startActivity(SettingsActivity.newInstance(this));
                break;
        }
    }

    protected void launchGiftChains() {
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setSubtitle("");
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_content, GiftListFragment.newInstance());
        ft.commit();
    }

    protected void launchRankings() {
        getSupportActionBar().setTitle(R.string.navdrawer_item_rankings);
        getSupportActionBar().setSubtitle("");
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_content, RankingsFragment.newInstance());
        ft.commit();
    }

    protected void launchFollowedUsers() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_content, FollowedUsersFragment.newInstance());
        ft.commit();
    }

    protected void launchAccountProfile() {
        startActivity(ProfileActivity.newInstance(this.getApplicationContext(), mUserAccounts.getActiveUser()));
    }

    private void setSelectedNavDrawerItem(NavItem itemId) {
        if (mNavDrawerItemViews != null) {
            for (int i = 0; i < mNavDrawerItemViews.length; i++) {
                if (i < mNavDrawerItems.size()) {
                    NavItem thisItemId = mNavDrawerItems.get(i);
                    formatNavDrawerItem(mNavDrawerItemViews[i], thisItemId, itemId == thisItemId);
                }
            }
        }
    }

    private void formatNavDrawerItem(View view, NavItem itemId, boolean selected) {
        if (isSeparator(itemId)) {
            // not applicable
            return;
        }

        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        TextView titleView = (TextView) view.findViewById(R.id.title);

        // configure its appearance according to whether or not it's selected
        titleView.setTextColor(selected ?
                getResources().getColor(R.color.navdrawer_text_color_selected) :
                getResources().getColor(R.color.navdrawer_text_color));
        iconView.setColorFilter(selected ?
                getResources().getColor(R.color.navdrawer_icon_tint_selected) :
                getResources().getColor(R.color.navdrawer_icon_tint));
    }

    protected boolean hasNavDrawer() {
        return mDrawerLayout != null && getSelfNavDrawerItem() != NavItem.INVALID;
    }
    protected void openNavDrawer() {
        if (hasNavDrawer()) {
            mDrawerLayout.openDrawer(Gravity.START);
        }
    }
    protected void closeNavDrawer() {
        if (hasNavDrawer()) {
            mDrawerLayout.closeDrawer(Gravity.START);
        }
    }
    protected boolean isNavDrawerOpen() {
        return hasNavDrawer() && mDrawerLayout.isDrawerOpen(Gravity.START);
    }

    private boolean doesNavItemLaunchNewActivity(NavItem item) {
        return item == NavItem.SETTINGS || item == NavItem.PROFILE;
    }

    private boolean isSeparator(NavItem item) {
        return item == NavItem.SEPARATOR;
    }





    /*
     *
     *  Accounts
     *
     */
    @Subscribe
    public void onAccountAdded(EventUserAccountAdded event) {
        LOGD(TAG, "New account added!!! setup the account box");
        setupUserAccountsBox();
    }

    private void setupUserAccountsBox() {
        LOGD(TAG, "setupUserAccountsBox()");
        mAccountListContainer = (LinearLayout) findViewById(R.id.account_list);
        if (mAccountListContainer == null) {
            return;
        }

        formatAccountBoxBasedOnLoginStatus();
    }

    private void formatAccountBoxBasedOnLoginStatus() {
        LOGD(TAG, "formatAccountBoxBasedOnLoginStatus()");
        if (mUserAccounts.isLoggedIn()) {
            formatAccountBoxForLoggedIn();
        } else {
            formatAccountBoxForLoggedOut();
        }
    }

    private void formatAccountBoxForLoggedOut() {
        LOGD(TAG, "formatAccountBoxForLoggedOut()");
        final View accountBox = findViewById(R.id.account_box);
        accountBox.setEnabled(true);
        accountBox.setLayoutParams(mLoggedOutLayoutParams);

        TextView nameText = (TextView) accountBox.findViewById(R.id.profile_name_text);
        TextView emailText = (TextView) accountBox.findViewById(R.id.profile_email_text);
        BezelImageView profilePic = (BezelImageView) accountBox.findViewById(R.id.profile_image);
        mExpandAccountBoxIndicator = (ImageView) accountBox.findViewById(R.id.expand_account_box_indicator);

        nameText.setText(R.string.user_logged_out);
        emailText.setText(R.string.user_logged_out_subtext);
        profilePic.setVisibility(View.GONE);
        mExpandAccountBoxIndicator.setVisibility(View.GONE);

        accountBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchLoginScreen();
            }
        });
    }

    private void formatAccountBoxForLoggedIn() {
        LOGD(TAG, "formatAccountBoxForLoggedIn()");
        final View accountBox = findViewById(R.id.account_box);
        accountBox.setEnabled(true);
        accountBox.setClickable(true);
        accountBox.setLayoutParams(mLoggedInLayoutParams);

        TextView nameTextView = (TextView) accountBox.findViewById(R.id.profile_name_text);
        TextView emailTextView = (TextView) accountBox.findViewById(R.id.profile_email_text);
        ImageView profilePic = (ImageView) accountBox.findViewById(R.id.profile_image);
        mExpandAccountBoxIndicator = (ImageView) findViewById(R.id.expand_account_box_indicator);

        List<UserAccount> users = mUserAccounts.getUsers();
        UserAccount loggedInUser = mUserAccounts.getActiveUser();
        users.remove(loggedInUser);
        users.add(new UserAccount(true));

        profilePic.setVisibility(View.VISIBLE);
        mExpandAccountBoxIndicator.setVisibility(View.VISIBLE);
        mImageLoader.loadWithoutDecoration(loggedInUser.getProfilePhotoUrl(), profilePic);
        nameTextView.setText(loggedInUser.getProfileName());
        emailTextView.setText(loggedInUser.getEmail());
        accountBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeAccountBoxExpandedStatus();
                setupAndRunAccountBoxToggle();
            }
        });

        setupAndRunAccountBoxToggle();
        populateAccountList(users);
    }

    private void changeAccountBoxExpandedStatus() {
        mAccountBoxExpanded = !mAccountBoxExpanded;
    }

    private void populateAccountList(List<UserAccount> users) {
        mAccountListContainer.removeAllViews();
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        for (final UserAccount user : users) {
            View itemView = layoutInflater.inflate(R.layout.navdrawer_account_list_item, mAccountListContainer, false);
            TextView accountName = (TextView) itemView.findViewById(R.id.account_name);
            accountName.setText(user.getEmail());

            formatAccountItem(itemView, user.isLoggedIn());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LOGD(TAG, "User clicked account: " + user.getEmail());
                    if (user.isSpecialUser()) {
                        changeAccountBoxExpandedStatus();
                        setupAndRunAccountBoxToggle();
                    }
                    loginAsUser(user);
                }
            });
            mAccountListContainer.addView(itemView);
        }
    }

    private void loginAsUser(UserAccount user) {
        if (user.hasLoggedInBefore()) {
            mUserAccounts.loginAs(user);
            setupUserAccountsBox();
        } else {
            launchLoginScreen();
        }
    }

    private void launchLoginScreen() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(LoginActivity.newInstance(NavDrawerActivity.this));
            }
        }, NAVDRAWER_LAUNCH_DELAY);

        closeNavDrawer();
    }

    private void formatAccountItem(View view, boolean selected) {
        TextView titleView = (TextView) view.findViewById(R.id.account_name);
        ImageView iconView = (ImageView) view.findViewById(R.id.account_image);

        // configure its appearance according to whether or not it's selected
        titleView.setTextColor(selected ?
                getResources().getColor(R.color.navdrawer_text_color_selected) :
                getResources().getColor(R.color.navdrawer_text_color));
        iconView.setColorFilter(selected ?
                getResources().getColor(R.color.navdrawer_icon_tint_selected) :
                getResources().getColor(R.color.navdrawer_icon_tint));
    }

    private void setupAndRunAccountBoxToggle() {
        if (!hasNavDrawer()) {
            return;
        }

        mExpandAccountBoxIndicator.setImageResource(mAccountBoxExpanded
                ? R.drawable.ic_drawer_accounts_collapse
                : R.drawable.ic_drawer_accounts_expand);
        int hideTranslateY = -mAccountListContainer.getHeight() / 4; // last 25% of animation
        if (mAccountBoxExpanded && mAccountListContainer.getTranslationY() == 0) {
            // initial setup
            mAccountListContainer.setAlpha(0);
            mAccountListContainer.setTranslationY(hideTranslateY);
        }

        AnimatorSet set = new AnimatorSet();
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mDrawerItemsListContainer.setVisibility(mAccountBoxExpanded ? View.INVISIBLE : View.VISIBLE);
                mAccountListContainer.setVisibility(mAccountBoxExpanded ? View.VISIBLE : View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                onAnimationEnd(animation);
            }
        });

        if (mAccountBoxExpanded) {
            mAccountListContainer.setVisibility(View.VISIBLE);
            AnimatorSet subSet = new AnimatorSet();
            subSet.playTogether(
                    ObjectAnimator.ofFloat(mAccountListContainer, View.ALPHA, 1)
                            .setDuration(ACCOUNT_BOX_EXPAND_ANIM_DURATION),
                    ObjectAnimator.ofFloat(mAccountListContainer, View.TRANSLATION_Y, 0)
                            .setDuration(ACCOUNT_BOX_EXPAND_ANIM_DURATION));
            set.playSequentially(
                    ObjectAnimator.ofFloat(mDrawerItemsListContainer, View.ALPHA, 0)
                            .setDuration(ACCOUNT_BOX_EXPAND_ANIM_DURATION),
                    subSet);
            set.start();
        } else {
            mDrawerItemsListContainer.setVisibility(View.VISIBLE);
            AnimatorSet subSet = new AnimatorSet();
            subSet.playTogether(
                    ObjectAnimator.ofFloat(mAccountListContainer, View.ALPHA, 0)
                            .setDuration(ACCOUNT_BOX_EXPAND_ANIM_DURATION),
                    ObjectAnimator.ofFloat(mAccountListContainer, View.TRANSLATION_Y,
                            hideTranslateY)
                            .setDuration(ACCOUNT_BOX_EXPAND_ANIM_DURATION));
            set.playSequentially(
                    subSet,
                    ObjectAnimator.ofFloat(mDrawerItemsListContainer, View.ALPHA, 1)
                            .setDuration(ACCOUNT_BOX_EXPAND_ANIM_DURATION));
            set.start();
        }

        set.start();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LOGD(TAG, "onOptionsItemSelected");
        if (mDrawerLayout != null && item.getItemId() == android.R.id.home) {
            if (isNavDrawerOpen()) {
                closeNavDrawer();
            } else {
                openNavDrawer();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

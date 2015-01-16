package com.mocca_capstone.potlatch.views;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mocca_capstone.potlatch.R;
import com.mocca_capstone.potlatch.application.Injector;
import com.mocca_capstone.potlatch.contentprovider.GiftContract;
import com.mocca_capstone.potlatch.contentprovider.PotlatchProviderHelper;
import com.mocca_capstone.potlatch.events.EventUserAccountDeleted;
import com.mocca_capstone.potlatch.events.EventUserAccountLoggedOut;
import com.mocca_capstone.potlatch.imageloader.ImageLoader;
import com.mocca_capstone.potlatch.listeners.OnUserDeletedListener;
import com.mocca_capstone.potlatch.models.BaseUser;
import com.mocca_capstone.potlatch.models.User;
import com.mocca_capstone.potlatch.models.UserAccount;
import com.mocca_capstone.potlatch.providers.EventBus;
import com.mocca_capstone.potlatch.providers.FollowedUsers;
import com.mocca_capstone.potlatch.utilities.FontManager;
import com.mocca_capstone.potlatch.utilities.IconColorizer;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGD;

/**
 * Created by nektario on 11/1/2014.
 */
public class ProfileFragment extends GiftListFragment {
    private static final String TAG = "ProfileFragment";
    @Inject FollowedUsers mFollowedUsers;
    private BaseUser mUser;
    private IconColorizer mIconColorizer;


    public static ProfileFragment newInstance(BaseUser user) {
        Bundle args = new Bundle();
        args.putSerializable("extra-user", user);
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.getInstance().inject(this);
        mUser = (BaseUser) getArguments().getSerializable("extra-user");
        mIconColorizer = new IconColorizer(getActivity().getApplicationContext());
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        int actionBarSize = (int) getResources().getDimension(R.dimen.action_bar_height_x3);
        int additionalPadding = getResources().getDimensionPixelSize(R.dimen.base_padding_level);
        setContentTopPadding(actionBarSize + additionalPadding);
        ((BaseActivity) getActivity()).getActionBarToolbar().setTitle(mUser.getProfileName());
        hideAddGiftFab();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sort, menu);
        if (mUser instanceof UserAccount) {
            inflater.inflate(R.menu.menu_logout, menu);
        } else {
            if (mFollowedUsers.isFollowedUser((User) mUser)) {
                inflater.inflate(R.menu.menu_unstar, menu);
            } else {
                inflater.inflate(R.menu.menu_star, menu);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LOGD(TAG, "onOptionsItemSelected");
        switch(item.getItemId()) {
            case R.id.action_star :
                mFollowedUsers.followUser((User) mUser);
                getActivity().invalidateOptionsMenu();
                return true;
            case R.id.action_unstar :
                mFollowedUsers.unfollowUser((User) mUser);
                getActivity().invalidateOptionsMenu();
                return true;
            case R.id.action_logout :
                mUserAccounts.deleteUser(mUserAccounts.getActiveUser(), new OnUserDeletedListener() {
                    @Override
                    public void onUserDeleted() {
                        LOGD(TAG, "onUserDeleted");
                        mEventBus.postOnUiThread(new EventUserAccountLoggedOut());
                        getActivity().finish();
                    }
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        LOGD(TAG, "onCreateLoader, id=" + id + ", bundle=" + bundle);
        return new CursorLoader(getActivity(), GiftContract.Gifts.CONTENT_URI, null,
                    makeQuerySelection(), makeQuerySelectionArgs(), makeQuerySortOrder());
    }

    private String makeQuerySelection() {
        String selection;
        selection = GiftContract.Gifts.OWNER_ID + "=?";

        if (mAppPrefs.hideFlaggedGifts()) {
            selection += " and " +  GiftContract.Gifts.FLAG_COUNT + " =?";
        }

        return selection;
    }

    private String[] makeQuerySelectionArgs() {
        List<String> projection = new ArrayList<String>();
        projection.add(String.valueOf(mUser.getId()));

        if (mAppPrefs.hideFlaggedGifts()) {
            projection.add(String.valueOf(0));
        }
        return projection.toArray(new String[projection.size()]);
    }

    private void colorizeTouchIcons(ImageView icon) {
        mIconColorizer.setTouchedColor(icon);
    }

    private void colorizeFlagIcons(ImageView icon) {
        mIconColorizer.setFlaggedColor(icon);
    }

    private void colorizeFollowIcon(ImageView icon, boolean isFollowedUser) {
        if (isFollowedUser) {
            mIconColorizer.setFollowedColor(icon);
        } else {
            mIconColorizer.setWhite(icon);
        }
    }
}

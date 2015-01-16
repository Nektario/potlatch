package com.mocca_capstone.potlatch.views;

import android.app.LoaderManager;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.mocca_capstone.potlatch.R;
import com.mocca_capstone.potlatch.application.Injector;
import com.mocca_capstone.potlatch.contentprovider.GiftContract;
import com.mocca_capstone.potlatch.contentprovider.PotlatchProviderHelper;
import com.mocca_capstone.potlatch.events.EventHideFlaggedGiftsPrefChanged;
import com.mocca_capstone.potlatch.events.EventUserAccountLoggedOut;
import com.mocca_capstone.potlatch.events.EventUserAccountAdded;
import com.mocca_capstone.potlatch.events.EventVoteComplete;
import com.mocca_capstone.potlatch.listeners.AddGiftFabOnScrollListener;
import com.mocca_capstone.potlatch.listeners.GiftCallbacks;
import com.mocca_capstone.potlatch.models.Gift;
import com.mocca_capstone.potlatch.models.Votable;
import com.mocca_capstone.potlatch.models.Vote;
import com.mocca_capstone.potlatch.network.ApiTaskService;
import com.mocca_capstone.potlatch.network.VoteCommand;
import com.mocca_capstone.potlatch.providers.EventBus;
import com.mocca_capstone.potlatch.providers.UserAccounts;
import com.mocca_capstone.potlatch.utilities.Utils;
import com.mocca_capstone.potlatch.widgets.AddGiftFab;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGD;

/**
 * Created by nektario on 10/20/2014.
 */
public abstract class BaseGiftFragment extends BaseFragment implements GiftCallbacks,
                                                                       LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "BaseGiftFragment";
    private static final int LOADER_NUM = 1;
    @Inject UserAccounts mUserAccounts;
    @Inject EventBus mEventBus;
    protected GiftContract.Gifts.Sort mSortOrder;
    protected AddGiftFab mAddGiftFab;

    protected Object onVoteCompletedListener = new Object() {
        @Subscribe
        public void onVoteComplete(final EventVoteComplete event) {
            LOGD(TAG, "onVoteComplete()");
            if (event.isSuccessStatusCode()) {
                if (event.getVoteType() == Votable.Type.TOUCH) {
                    switch (event.getVoteState()) {
                        case ON :
                            event.getVotable().addTouch(mUserAccounts.getActiveUser().getId());
                            break;
                        case OFF :
                            event.getVotable().removeTouch(mUserAccounts.getActiveUser().getId());
                            break;
                    }
                    PotlatchProviderHelper.setTouch(getActivity().getContentResolver(), event.getVotable());
                } else {
                    switch (event.getVoteState()) {
                        case ON:
                            event.getVotable().addFlag(mUserAccounts.getActiveUser().getId());
                            break;
                        case OFF:
                            event.getVotable().removeFlag(mUserAccounts.getActiveUser().getId());
                            break;
                    }
                    PotlatchProviderHelper.setFlag(getActivity().getContentResolver(), event.getVotable());
                }
            } else {
                Toast.makeText(getActivity(), event.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    protected Object onHideFlaggedGiftsPrefChangedListener = new Object() {
        @Subscribe
        public void onHideFlaggedGiftsPrefChanged(final EventHideFlaggedGiftsPrefChanged event) {
            LOGD(TAG, "onHideFlaggedGiftsPrefChanged()");
            restartLoader(null);
        }
    };

    private Object onUserAccountsChangedListener = new Object() {
        @Subscribe
        public void onUserAccountAdded(final EventUserAccountAdded event) {
            showOrHideAddGiftFabBasedOnLoginStatus();
        }

        @Subscribe
        public void onUserAccountLoggedOut(final EventUserAccountLoggedOut event) {
            showOrHideAddGiftFabBasedOnLoginStatus();
        }
    };


    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putString("current-sort", mSortOrder.toString());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LOGD(TAG, "onCreate()");
        Injector.getInstance().inject(this);
        if (savedInstanceState != null && savedInstanceState.containsKey("current-sort")) {
            mSortOrder = GiftContract.Gifts.Sort.valueOf(savedInstanceState.getString("current-sort"));
        }
        mEventBus.register(onVoteCompletedListener);
        mEventBus.register(onHideFlaggedGiftsPrefChangedListener);
        mEventBus.register(onUserAccountsChangedListener);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        showOrHideAddGiftFabBasedOnLoginStatus();
    }

    private void showOrHideAddGiftFabBasedOnLoginStatus() {
        if (mUserAccounts.isLoggedIn()) {
            showAddGiftFab();
        } else {
            hideAddGiftFab();
        }
    }

    protected void showAddGiftFab() {
        mAddGiftFab.setVisibility(View.VISIBLE);
    }
    protected void hideAddGiftFab() {
        mAddGiftFab.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LOGD(TAG, "onDestroy");
        mEventBus.unRegister(onVoteCompletedListener);
        mEventBus.unRegister(onHideFlaggedGiftsPrefChangedListener);
        mEventBus.unRegister(onUserAccountsChangedListener);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sort, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    protected void setUpAddGiftFab(View view, AbsListView listView, final long giftParentId) {
        mAddGiftFab = (AddGiftFab) view.findViewById(R.id.add_gift_fab);
        mAddGiftFab.attachToListView(listView, new AddGiftFabOnScrollListener(mAddGiftFab));
        mAddGiftFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAddGiftFab.isExpanded()) {
                    mAddGiftFab.collapse();
                    startActivity(AddGiftActivity.newInstance(
                                        getActivity().getApplicationContext(),
                                        AddGiftActivity.USE_CAMERA,
                                        giftParentId));
                } else {
                    mAddGiftFab.expand();
                }
            }
        });
        mAddGiftFab.setGalleryButton((FloatingActionButton) view.findViewById(R.id.add_gift_from_gallery_fab),
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LOGD(TAG, "Add From Gallery");
                    mAddGiftFab.collapse();
                    startActivity(AddGiftActivity.newInstance(
                                        getActivity().getApplicationContext(),
                                        AddGiftActivity.USE_GALLERY,
                                        giftParentId));
                }
            });
    }


    /*
     * Sorting
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LOGD(TAG, "onOptionsItemSelected");
        switch(item.getItemId()) {
            case R.id.action_sort :
                return true;
            case R.id.action_sort_new :
                handleSortChange(GiftContract.Gifts.Sort.NEW);
                return true;
            case R.id.action_sort_popularity :
                handleSortChange(GiftContract.Gifts.Sort.POPULAR);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleSortChange(GiftContract.Gifts.Sort sort) {
        if (sort != mSortOrder) {
            mSortOrder = sort;
            ((BaseActivity) getActivity()).fadeMainContent();
            restartLoader(null);
        }
    }

    protected String makeQuerySortOrder() {
        StringBuilder sb = new StringBuilder();
        if (mSortOrder == null) {
            mSortOrder = GiftContract.Gifts.Sort.NEW;
        }
        sb.append(mSortOrder.getValue());
        sb.append(" DESC");
        ((BaseActivity) getActivity()).getActionBarToolbar().setSubtitle(mSortOrder.toString());
        return sb.toString();
    }


    /*
     * Touch / flag icons
     */
    @Override
    public void onTouchedIconClicked(Gift gift) {
        if (!validateLoginStatusAndNetwork()) {
            return;
        }

        VoteCommand command;

        if (gift.isTouchedByUser(mUserAccounts.getActiveUser().getId())) {
            command = new VoteCommand(gift, new Vote(gift.getId(), Votable.State.OFF, Votable.Type.TOUCH));
        } else {
            command = new VoteCommand(gift, new Vote(gift.getId(), Votable.State.ON, Votable.Type.TOUCH));
        }

        getActivity().startService(ApiTaskService.newInstance(getActivity(), command));
    }

    @Override
    public void onFlagIconClicked(Gift gift) {
        if (!validateLoginStatusAndNetwork()) {
            return;
        }

        VoteCommand command;

        if (gift.isFlaggedByUser(mUserAccounts.getActiveUser().getId())) {
            command = new VoteCommand(gift, new Vote(gift.getId(), Votable.State.OFF, Votable.Type.FLAG));
        } else {
            command = new VoteCommand(gift, new Vote(gift.getId(), Votable.State.ON, Votable.Type.FLAG));
        }

        getActivity().startService(ApiTaskService.newInstance(getActivity(), command));
    }


    /*
     * General
     */
    private boolean validateLoginStatusAndNetwork() {
        if (!mUserAccounts.isLoggedIn()) {
            Toast.makeText(getActivity(), R.string.login_required, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Utils.isNetworkConnected()) {
            Toast.makeText(getActivity(), R.string.internet_required, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    protected void startLoader(Bundle bundle) {
        getLoaderManager().initLoader(LOADER_NUM, bundle, this);
    }

    protected void restartLoader(Bundle bundle) {
        getLoaderManager().restartLoader(LOADER_NUM, bundle, this);
    }

    public abstract boolean onBackPressed();
}

package com.mocca_capstone.potlatch.views;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.mocca_capstone.potlatch.R;
import com.mocca_capstone.potlatch.adapters.AdapterState;
import com.mocca_capstone.potlatch.adapters.GiftAdapter;
import com.mocca_capstone.potlatch.application.Injector;
import com.mocca_capstone.potlatch.contentprovider.GiftContract;
import com.mocca_capstone.potlatch.contentprovider.PotlatchProviderHelper;
import com.mocca_capstone.potlatch.listeners.AddGiftFabOnScrollListener;
import com.mocca_capstone.potlatch.models.Gift;
import com.mocca_capstone.potlatch.models.GiftChainListResponse;
import com.mocca_capstone.potlatch.models.SearchItem;
import com.mocca_capstone.potlatch.network.GiftApiClient;
import com.mocca_capstone.potlatch.providers.AppPrefs;
import com.mocca_capstone.potlatch.widgets.AddGiftFab;
import com.mocca_capstone.potlatch.widgets.EndlessCollectionView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGD;

/**
 * Created by nektario on 10/13/2014.
 */
public class GiftListFragment extends BaseGiftFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "GiftListFragment";
    @Inject GiftApiClient mApiClient;
    @Inject AppPrefs mAppPrefs;
    private GiftAdapter mAdapter;
    private EndlessCollectionView mListView;
    private TextView mNewGiftsAvailablePopup;
    private boolean mIsInSearchMode;
    private SearchItem mSearch;
    private TextView mSearchReturnedNoResultsView;
    private View mHeaderView;


    public static GiftListFragment newInstance() {
        return new GiftListFragment();
    }

    public static GiftListFragment newInstance(SearchItem search) {
        Bundle args = new Bundle();
        args.putSerializable("extra-search", search);
        GiftListFragment fragment = new GiftListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public GiftListFragment() {

    }


    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putParcelable("adapter-state", mAdapter.getState());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.getInstance().inject(this);
        setHasOptionsMenu(true);
        getSearchTermsIfPresent();
    }

    private void getSearchTermsIfPresent() {
        if (getArguments() != null && getArguments().containsKey("extra-search")) {
            mIsInSearchMode = true;
            mSearch = (SearchItem) getArguments().getSerializable("extra-search");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        super.onCreateView(inflater, parent, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_gift_chain_list, parent, false);
        AdapterState state = null;
        if (savedInstanceState != null && savedInstanceState.containsKey("adapter-state")) {
            state = savedInstanceState.getParcelable("adapter-state");
        }

        mListView = (EndlessCollectionView) view.findViewById(R.id.list);
        mAdapter = new GiftAdapter(getActivity(), null, true, state, this);

        if (mHeaderView != null) {
            mListView.addHeaderView(mHeaderView);
        }
        mListView.setAdapter(mAdapter);

        mSearchReturnedNoResultsView = (TextView) view.findViewById(R.id.no_search_results);
        mNewGiftsAvailablePopup = (TextView) view.findViewById(R.id.new_gifts_available_popup);
        setUpAddGiftFab(view, (AbsListView) mListView, Gift.CHAIN_ROOT_PARENT_ID);
        ((BaseActivity) getActivity()).enableActionBarAutoHide(
                                            (EndlessCollectionView)view.findViewById(R.id.list),
                                            false);

        if (!mAppPrefs.isFirstTimeRunning()) {
            checkForNewGifts();
        }
        return view;
    }



    @Override
    public boolean onBackPressed() {
        if (mAddGiftFab.isExpanded()) {
            mAddGiftFab.collapse();
            return true;
        }
        return false;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        startLoader(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (isInSearchMode()) {
            inflater.inflate(R.menu.menu_settings, menu);
        } else {
            inflater.inflate(R.menu.menu_search, menu);
            MenuItem item = menu.findItem(R.id.action_search);

            SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setSubmitButtonEnabled(true);
            searchView.setQueryRefinementEnabled(true);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    private boolean isInSearchMode() {
        return mIsInSearchMode;
    }

    @Override
    public void setContentTopPadding(int topPadding) {
        mListView.setContentTopClearance(topPadding);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isInSearchMode()) {
            ((BaseActivity) getActivity()).getActionBarToolbar().setTitle(mSearch.description);
        }
        ((BaseActivity) getActivity()).getActionBarToolbar().setSubtitle(mSortOrder.toString());
    }


    /*
     * Loader
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        LOGD(TAG, "onCreateLoader, id=" + id + ", bundle=" + bundle);
        mAdapter.resetState();
        if (isInSearchMode()) {
            return new CursorLoader(getActivity(), GiftContract.Gifts.CONTENT_URI, null,
                    makeQuerySelection(), makeQuerySelectionArgs(), makeQuerySortOrder());
        } else {
            return new CursorLoader(getActivity(), GiftContract.Gifts.CONTENT_URI, null,
                    makeQuerySelection(), makeQuerySelectionArgs(), makeQuerySortOrder());
        }
    }

    private String makeQuerySelection() {
        String selection;
        if (isInSearchMode()) {
            selection = mSearch.selection + " LIKE ?";
        } else {
            selection = GiftContract.Gifts.IS_CHAIN_ROOT + "=?";
        }

        if (mAppPrefs.hideFlaggedGifts()) {
            selection += " and " +  GiftContract.Gifts.FLAG_COUNT + " =?";
        }

        return selection;
    }

    private String[] makeQuerySelectionArgs() {
        List<String> projection = new ArrayList<String>();
        if (isInSearchMode()) {
            projection.add("%"+mSearch.selectionArgs+"%");
        } else {
            projection.add(String.valueOf(1));
        }

        if (mAppPrefs.hideFlaggedGifts()) {
            projection.add(String.valueOf(0));
        }
        return projection.toArray(new String[projection.size()]);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.swapCursor(cursor);

        if (isInSearchMode()) {
            ((BaseActivity) getActivity()).getSupportActionBar().setTitle(mSearch.description);

            if (cursor.getCount() == 0) {
                showSearchReturnedNoResultsView();
            } else {
                hideSearchReturnedNoResultsView();
            }
        }
    }

    private void showSearchReturnedNoResultsView() {
        mSearchReturnedNoResultsView.setVisibility(View.VISIBLE);
    }

    private void hideSearchReturnedNoResultsView() {
        mSearchReturnedNoResultsView.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }

    protected void setHeader(View view) {
        mHeaderView = view;
    }


    private void checkForNewGifts() {
        final Gift newestGiftInContentProvider = PotlatchProviderHelper.getNewestGift(getActivity().getContentResolver());

        mApiClient.getNewestGift(new Callback<Gift>() {
            @Override
            public void success(Gift gift, Response response) {
                LOGD(TAG, "checkForNewGifts:success");
                LOGD(TAG, "Newest Gift id in our content provider is: " + newestGiftInContentProvider.getId());
                if (gift.getId() == newestGiftInContentProvider.getId()) {
                    LOGD(TAG, "We are up to date");
                } else {
                    LOGD(TAG, "We have new gifts! - show a popup");
                    showNewGiftChainsPopup();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                LOGD(TAG, "checkForNewGiftChains():failure: " + error.toString());
            }
        });
    }

    private void showNewGiftChainsPopup() {
        mNewGiftsAvailablePopup.setVisibility(View.VISIBLE);
        mNewGiftsAvailablePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideNewGiftChainsPopup();
                loadNewGiftChains();
            }
        });
    }

    private void hideNewGiftChainsPopup() {
        mNewGiftsAvailablePopup.setVisibility(View.GONE);
    }

    private void loadNewGiftChains() {
        PotlatchProviderHelper.syncNow();
    }
}

package com.mocca_capstone.potlatch.views;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.mocca_capstone.potlatch.R;
import com.mocca_capstone.potlatch.adapters.RankingsAdapter;
import com.mocca_capstone.potlatch.application.Injector;
import com.mocca_capstone.potlatch.contentprovider.GiftContract;
import com.mocca_capstone.potlatch.models.Ranking;
import com.mocca_capstone.potlatch.models.User;
import com.mocca_capstone.potlatch.providers.AppPrefs;
import com.mocca_capstone.potlatch.widgets.EndlessCollectionView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGD;

/**
 * Created by nektario on 10/25/2014.
 */
public class RankingsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "RankingsFragment";
    @Inject AppPrefs mAppPrefs;
    private RankingsAdapter mAdapter;
    private EndlessCollectionView mListView;


    public static RankingsFragment newInstance() {
        return new RankingsFragment();
    }


    public RankingsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.getInstance().inject(this);
        getLoaderManager().initLoader(1, null, this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        super.onCreateView(inflater, parent, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_rankings, parent, false);
        mAdapter = new RankingsAdapter(getActivity(), null, true);
        mListView = (EndlessCollectionView) view.findViewById(R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor c = mAdapter.getCursor();
                c.moveToPosition(position);
                final Ranking ranking = new Ranking(c);
                final User user = new User(ranking.getOwnerId(),
                                               ranking.getOwnerProfileName(),
                                               ranking.getOwnerProfilePhotoUrl());
                startActivity(ProfileActivity.newInstance(getActivity(), user));
            }
        });
        return view;
    }

    @Override
    public void setContentTopPadding(int topPadding) {
        mListView.setContentTopClearance(topPadding);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).getSupportActionBar().setTitle(R.string.navdrawer_item_rankings);
        ((BaseActivity) getActivity()).getSupportActionBar().setSubtitle("");
    }


    /*
     * Loader
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        LOGD(TAG, "onCreateLoader, id=" + id + ", bundle=" + bundle);
        return new CursorLoader(getActivity(), Uri.parse(GiftContract.Gifts.CONTENT_URI + "/Rankings"),
                makeQueryProjection(), null, null, null);
    }

    private String[] makeQueryProjection() {
        List<String> projection = new ArrayList<String>();
        projection.add(GiftContract.Gifts.OWNER_ID + " as " + GiftContract.Gifts._ID);
        projection.add(GiftContract.Gifts.OWNER_PROFILE_NAME);
        projection.add(GiftContract.Gifts.OWNER_PROFILE_PHOTO_URL);
        projection.add("sum("+GiftContract.Gifts.TOUCH_COUNT+") as " + GiftContract.Gifts.TOUCH_COUNT);
        projection.add("sum("+GiftContract.Gifts.FLAG_COUNT+") as " + GiftContract.Gifts.FLAG_COUNT);
        return projection.toArray(new String[projection.size()]);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }
}

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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.mocca_capstone.potlatch.R;
import com.mocca_capstone.potlatch.adapters.AdapterState;
import com.mocca_capstone.potlatch.adapters.GiftAdapter;
import com.mocca_capstone.potlatch.application.Injector;
import com.mocca_capstone.potlatch.contentprovider.GiftContract;
import com.mocca_capstone.potlatch.imageloader.ImageLoader;
import com.mocca_capstone.potlatch.listeners.AddGiftFabOnScrollListener;
import com.mocca_capstone.potlatch.models.Gift;
import com.mocca_capstone.potlatch.models.User;
import com.mocca_capstone.potlatch.providers.UserAccounts;
import com.mocca_capstone.potlatch.utilities.FontManager;
import com.mocca_capstone.potlatch.utilities.Formatters;
import com.mocca_capstone.potlatch.utilities.IconColorizer;
import com.mocca_capstone.potlatch.widgets.AddGiftFab;
import com.mocca_capstone.potlatch.widgets.EndlessCollectionView;

import javax.inject.Inject;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGD;

/**
 * Created by nektario on 10/16/2014.
 */
public class GiftDetailsFragment extends BaseGiftFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "GiftDetailsFragment";
    @Inject ImageLoader mImageLoader;
    @Inject UserAccounts mUserAccounts;
    private GiftAdapter mAdapter;
    private Gift mGift;
    private EndlessCollectionView mListView;
    private IconColorizer mIconColorizer;
    private ImageView mTouchIcon;
    private ImageView mFlagIcon;
    private LayoutInflater mInflater;
    private View mChainRootGiftView;


    public static GiftDetailsFragment newInstance(Gift gift) {
        GiftDetailsFragment fragment = new GiftDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("extra-gift", gift);
        fragment.setArguments(bundle);
        return fragment;
    }

    public GiftDetailsFragment() {

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
        mGift = getArguments().getParcelable("extra-gift");
        mIconColorizer = new IconColorizer(getActivity().getApplicationContext());
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
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        super.onCreateView(inflater, parent, savedInstanceState);
        mInflater = inflater;
        View view = inflater.inflate(R.layout.fragment_gift_details, parent, false);
        AdapterState state = null;
        if (savedInstanceState != null && savedInstanceState.containsKey("adapter-state")) {
            state = savedInstanceState.getParcelable("adapter-state");
        }
        mAdapter = new GiftAdapter(getActivity(), null, true, state, this);
        mListView = (EndlessCollectionView) view.findViewById(R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (((BaseActivity) getActivity()).isActionBarVisible()) {
                    ((BaseActivity) getActivity()).hideActionBar();
                } else {
                    ((BaseActivity) getActivity()).showActionBar();
                }
            }
        });
        createChainRootGiftView();
        mListView.addHeaderView(mChainRootGiftView);
        setUpAddGiftFab(view, (AbsListView) mListView, mGift.getId());

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        startLoader(null);
        getLoaderManager().initLoader(2, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        LOGD(TAG, "onResume()");
        ((BaseActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name);
        ((BaseActivity) getActivity()).getSupportActionBar().setSubtitle(mSortOrder.toString());
    }

    private void createChainRootGiftView() {
        if (mChainRootGiftView == null) {
            mChainRootGiftView = mInflater.inflate(R.layout.gift_details, null, false);
        }

        TextView title = (TextView) mChainRootGiftView.findViewById(R.id.gift_title);
        TextView description = (TextView) mChainRootGiftView.findViewById(R.id.gift_description);
        TextView ownerDetails = (TextView) mChainRootGiftView.findViewById(R.id.gift_details_details);
        TextView touchCount = (TextView) mChainRootGiftView.findViewById(R.id.gift_touch_count);
        TextView flagCount = (TextView) mChainRootGiftView.findViewById(R.id.gift_flag_count);
        mTouchIcon = (ImageView) mChainRootGiftView.findViewById(R.id.gift_touch_icon);
        mFlagIcon = (ImageView) mChainRootGiftView.findViewById(R.id.gift_flag_icon);
        LinearLayout touchGroup = (LinearLayout) mChainRootGiftView.findViewById(R.id.gift_touch_group);
        LinearLayout flagGroup = (LinearLayout) mChainRootGiftView.findViewById(R.id.gift_flag_group);
        ImageView ownerProfilePic = (ImageView) mChainRootGiftView.findViewById(R.id.gift_details_owner_profile_pic);
        ImageView giftPicture = (ImageView) mChainRootGiftView.findViewById(R.id.gift_details_picture);
        mImageLoader.load(mGift.getOwnerProfilePhotoUrl(), ownerProfilePic);
        mImageLoader.load(mGift.getMediaUrl(), giftPicture);

        ownerProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final User u = new User(mGift.getOwnerId(),
                                            mGift.getOwnerProfileName(),
                                            mGift.getOwnerProfilePhotoUrl());
                getActivity().startActivity(ProfileActivity.newInstance(getActivity().getApplicationContext(), u));
            }
        });

        touchGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LOGD(TAG, "Touched");
                onTouchedIconClicked(mGift);
            }
        });

        flagGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LOGD(TAG, "Flagged");
                onFlagIconClicked(mGift);
            }
        });

        giftPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(
                        DisplayPictureActivity.newInstance(getActivity().getApplicationContext(), mGift.getMediaUrl()));
            }
        });

        title.setText(mGift.getTitle());
        description.setText(mGift.getDescription());
        ownerDetails.setText(mGift.getOwnerProfileName());
        ownerDetails.append("\n");
        ownerDetails.append(Formatters.formatDateTimeForLocaleIndependentDisplay(mGift.getCreatedTime()));
        touchCount.setText(String.valueOf(mGift.getTouchCount()));
        flagCount.setText(String.valueOf(mGift.getFlagCount()));

        FontManager.setSerifLight(getActivity(), title);
        FontManager.setSerifLight(getActivity(), description);
        FontManager.setLight(getActivity(), ownerDetails);
        FontManager.setLight(getActivity(), touchCount);
        FontManager.setLight(getActivity(), flagCount);

        colorizeIcons();
    }

    private void colorizeIcons() {
        if (giftTouchedUser()) {
            mIconColorizer.setTouchedColor(mTouchIcon);
        } else {
            mIconColorizer.setNoColor(mTouchIcon);
        }

        if (giftWasFlaggedByUser()) {
            mIconColorizer.setFlaggedColor(mFlagIcon);
        } else {
            mIconColorizer.setNoColor(mFlagIcon);
        }
    }

    private boolean giftTouchedUser() {
        return mUserAccounts.isLoggedIn() && mGift.isTouchedByUser(mUserAccounts.getActiveUser().getId());
    }

    private boolean giftWasFlaggedByUser() {
        return mUserAccounts.isLoggedIn() && mGift.isFlaggedByUser(mUserAccounts.getActiveUser().getId());
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        LOGD(TAG, "onCreateLoader, id=" + id + ", bundle=" + bundle);
        if (id == 1) {
            return new CursorLoader(getActivity(), GiftContract.Gifts.CONTENT_URI, null, GiftContract.Gifts.PARENT_ID+"=?",
                    new String[]{String.valueOf(mGift.getId())}, makeQuerySortOrder());
        } else {
            return new CursorLoader(getActivity(), Uri.parse(GiftContract.Gifts.CONTENT_URI + "/" + mGift.getId()),
                    null, null, null, null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursorLoader.getId() == 1) {
            mAdapter.swapCursor(cursor);
        } else {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                mGift = new Gift(cursor);
                createChainRootGiftView();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }
}

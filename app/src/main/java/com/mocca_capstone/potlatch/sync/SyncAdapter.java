package com.mocca_capstone.potlatch.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.mocca_capstone.potlatch.application.Injector;
import com.mocca_capstone.potlatch.contentprovider.PotlatchProviderHelper;
import com.mocca_capstone.potlatch.models.Gift;
import com.mocca_capstone.potlatch.models.GiftChainListResponse;
import com.mocca_capstone.potlatch.network.GiftApiClient;

import javax.inject.Inject;

import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGD;

/**
 * Created by nektario on 10/14/2014.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter implements retrofit.Callback<GiftChainListResponse> {
    private static final String TAG = "SyncAdapter";
    private static final int DEFAULT_SYNC_RESULTS_LIMIT = 10;
    private static final int INITIAL_PAGE_TOKEN = 0;
    private ContentResolver mContentResolver;
    @Inject GiftApiClient mApiClient;
    private Gift mNewestGift;
    private int mNextPageToken;


    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        init(context);
    }

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        init(context);
    }

    private void init(Context context) {
        Injector.getInstance().inject(this);
        mContentResolver = context.getContentResolver();
    }


    @Override
    public void onPerformSync(Account account, Bundle bundle, String authority,
                                            ContentProviderClient contentProviderClient, SyncResult syncResult) {
        final boolean uploadOnly = bundle.getBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, false);
        final boolean manualSync = bundle.getBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, false);
        final boolean initialize = bundle.getBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, false);
        final boolean expedited = bundle.getBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, false);

        LOGD(TAG, "Beginning sync for account " + account.name + "," +
                " uploadOnly=" + uploadOnly +
                " manualSync=" + manualSync +
                " initialize=" + initialize +
                " expedited=" + expedited);

        mNewestGift = PotlatchProviderHelper.getNewestGift(mContentResolver);
        mNextPageToken = INITIAL_PAGE_TOKEN;
        doSync();
    }

    private void doSync() {
        LOGD(TAG, mNextPageToken +":"+DEFAULT_SYNC_RESULTS_LIMIT+":"+"new"+":"+mNewestGift.getId());
        mApiClient.getGiftsNewerThanGiftId(
                mNextPageToken,
                DEFAULT_SYNC_RESULTS_LIMIT,
                "new",
                mNewestGift.getId(),
                this);
    }

    @Override
    public void success(GiftChainListResponse giftChainListResponse, Response response) {
        LOGD(TAG, "onPerformSync:success");
        PotlatchProviderHelper.insert(mContentResolver, giftChainListResponse.getGifts());
        if (giftChainListResponse.hasMoreResults()) {
            mNextPageToken = giftChainListResponse.getNextPageToken();
            doSync();
        }
    }

    @Override
    public void failure(RetrofitError error) {
        LOGD(TAG, "onPerformSync:failure: " + error.toString());
    }
}

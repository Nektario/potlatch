package com.mocca_capstone.potlatch.network;

import android.content.Context;

import com.mocca_capstone.potlatch.R;
import com.mocca_capstone.potlatch.application.Config;
import com.mocca_capstone.potlatch.application.ForApplication;
import com.mocca_capstone.potlatch.application.Injector;
import com.mocca_capstone.potlatch.events.EventVoteComplete;
import com.mocca_capstone.potlatch.models.Gift;
import com.mocca_capstone.potlatch.models.GiftChainListResponse;
import com.mocca_capstone.potlatch.models.RegistrationUser;
import com.mocca_capstone.potlatch.models.UserAccount;
import com.mocca_capstone.potlatch.models.Votable;
import com.mocca_capstone.potlatch.models.Vote;
import com.mocca_capstone.potlatch.providers.EventBus;
import com.mocca_capstone.potlatch.providers.UserAccounts;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGD;

/**
 * Created by nektario on 10/13/2014.
 */
public class GiftApiClient {
    private static final String TAG = "GiftApiClient";
    private GiftApiService mService;
    @Inject UserAccounts mAccounts;
    @Inject EventBus mEventBus;
    @Inject @ForApplication Context mContext;


    @Inject
    public GiftApiClient() {
        Injector.getInstance().inject(this);
        UnsafeSsl ssl = new UnsafeSsl(mContext);
        OkHttpClient client = new OkHttpClient();
        client.setSslSocketFactory(ssl.getSslContext().getSocketFactory());
        client.setHostnameVerifier(new HardwiredHostnameVerifier());
        mService = new RestAdapter.Builder()
                .setEndpoint(Config.API_ENDPOINT)
                .setLogLevel(Config.RETROFIT_LOG_LEVEL)
                .setRequestInterceptor(new GiftApiServiceInterceptor())
                .setClient(new OkClient(client))
                .build()
                .create(GiftApiService.class);
    }

    public void getNewestGift(Callback<Gift> callback) {
        mService.getNewestGift(callback);
    }

    public void getGiftsNewerThanGiftId(int nextPageToken, int limit, String sort, long giftId,
                                                Callback<GiftChainListResponse> callback) {
        mService.getGiftsNewerThanGiftId(nextPageToken, limit, sort, giftId, callback);
    }

    public void registerUser(RegistrationUser user, Callback<UserAccount> callback) {
        mService.registerUser(user, callback);
    }

    // login required
    public void vote(final Votable votable, final Vote vote) {
        if (mAccounts.isLoggedIn()) {
            try {
                Response response = null;
                if (vote.getVoteType() == Votable.Type.TOUCH) {
                    response = mService.touch(vote.getid(), vote.getVoteState().getValue());
                } else {
                    response = mService.flag(vote.getid(), vote.getVoteState().getValue());
                }

                if (response.getStatus() == 200) {
                    mEventBus.post(new EventVoteComplete(votable, vote, true, "success"));
                } else {
                    mEventBus.post(new EventVoteComplete(votable, vote, false, "failed"));
                }
            }
            catch (RetrofitError error) {
                LOGD(TAG, error.toString());
                mEventBus.post(new EventVoteComplete(votable, vote, false, "failed"));
            }
        } else {
            LOGD(TAG, "Need to be logged in to do that");
            mEventBus.post(new EventVoteComplete(votable, vote, false, mContext.getResources().getString(R.string.login_required)));
        }
    }

    // login required
    public Gift uploadNewGift(final Gift gift, final TypedFile file) throws RetrofitError {
        if (mAccounts.isLoggedIn()) {
            return mService.uploadNewGift(gift, file);
        } else {
            LOGD(TAG, "Need to be logged in to do that");
            return null;
        }
    }
}

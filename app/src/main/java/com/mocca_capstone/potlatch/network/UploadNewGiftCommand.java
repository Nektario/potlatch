package com.mocca_capstone.potlatch.network;

import android.content.ContentResolver;
import android.content.Context;

import com.mocca_capstone.potlatch.application.ForApplication;
import com.mocca_capstone.potlatch.application.Injector;
import com.mocca_capstone.potlatch.application.PotlatchApp;
import com.mocca_capstone.potlatch.contentprovider.PotlatchProviderHelper;
import com.mocca_capstone.potlatch.events.EventAddGift;
import com.mocca_capstone.potlatch.models.Gift;
import com.mocca_capstone.potlatch.providers.EventBus;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit.RetrofitError;
import retrofit.mime.TypedFile;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGD;

/**
 * Created by nektario on 11/5/2014.
 */
public class UploadNewGiftCommand implements ApiCommand, Serializable {
    private static final String TAG = "UploadNewGiftCommand";
    private Gift mGift;
    private String mGiftPhotoPath;
    @Inject EventBus mBus;


    public UploadNewGiftCommand(Gift gift, String giftPhotoPath) {
        Injector.getInstance().inject(this);
        mGift = gift;
        mGiftPhotoPath = giftPhotoPath;
    }

    @Override
    public void execute(GiftApiClient apiClient) {
        try {
            Gift giftResponse = apiClient.uploadNewGift(mGift, new TypedFile("image/jpg", new File(mGiftPhotoPath)));
            if (giftResponse != null) {
                handleSuccess(giftResponse);
            } else {
                handleError();
            }
        } catch (RetrofitError re) {
            re.printStackTrace();
            handleError();
        }
    }

    private void handleSuccess(Gift gift) {
        List<Gift> giftInsert = new ArrayList<Gift>();
        giftInsert.add(gift);
        PotlatchProviderHelper.insert(PotlatchApp.getAppContext().getContentResolver(), giftInsert);
        mBus.postOnUiThread(new EventAddGift(true));
    }

    private void handleError() {
        mBus.postOnUiThread(new EventAddGift(false));
    }
}

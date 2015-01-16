package com.mocca_capstone.potlatch.network;

import com.mocca_capstone.potlatch.application.Config;
import com.mocca_capstone.potlatch.application.Injector;
import com.mocca_capstone.potlatch.providers.UserAccounts;

import javax.inject.Inject;

import retrofit.RequestInterceptor;

/**
 * Created by nektario on 10/20/2014.
 */
public class GiftApiServiceInterceptor implements RequestInterceptor {
    @Inject
    UserAccounts mUserAccounts;

    public GiftApiServiceInterceptor() {
        super();
        Injector.getInstance().inject(this);
    }


    @Override
    public void intercept(RequestFacade request) {
        request.addHeader("User-Agent", Config.USER_AGENT);
        if (mUserAccounts.isLoggedIn()) {
            request.addHeader("Authorization", mUserAccounts.getActiveUser().getToken());
        }
    }
}

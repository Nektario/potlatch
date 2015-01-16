package com.mocca_capstone.potlatch.network;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.mocca_capstone.potlatch.application.Config;
import com.mocca_capstone.potlatch.application.Injector;
import com.mocca_capstone.potlatch.providers.UserAccounts;

import java.io.IOException;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by nektario on 10/20/2014.
 */
public class ApiTaskService extends IntentService {
    private static final String TAG = "ApiTaskService";
    @Inject
    UserAccounts mUserAccounts;
    @Inject GiftApiClient mApiClient;


    public static Intent newInstance(Context context, ApiCommand command) {
        Intent intent = new Intent(context, ApiTaskService.class);
        intent.putExtra("extra-command", command);
        return intent;
    }


    public ApiTaskService() {
        super(TAG);
        Injector.getInstance().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ApiCommand command = (ApiCommand) intent.getSerializableExtra("extra-command");
        try {
            String token = GoogleAuthUtil.getToken(
                    getApplicationContext(),
                    mUserAccounts.getActiveUser().getEmail(),
                    "audience:server:client_id:" + Config.SERVER_CLIENT_ID);
            mUserAccounts.getActiveUser().setToken(token);
            mUserAccounts.saveUsers();
            command.execute(mApiClient);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GoogleAuthException e) {
            e.printStackTrace();
        }
    }
}

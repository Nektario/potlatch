package com.mocca_capstone.potlatch.views;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.mocca_capstone.potlatch.R;
import com.mocca_capstone.potlatch.application.Config;
import com.mocca_capstone.potlatch.application.Injector;
import com.mocca_capstone.potlatch.events.EventUserAccountAdded;
import com.mocca_capstone.potlatch.models.RegistrationUser;
import com.mocca_capstone.potlatch.models.UserAccount;
import com.mocca_capstone.potlatch.providers.EventBus;
import com.mocca_capstone.potlatch.network.GiftApiClient;
import com.mocca_capstone.potlatch.providers.UserAccounts;
import com.mocca_capstone.potlatch.utilities.FontManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGD;


/**
 * Created by nektario on 10/11/2014.
 */
public class LoginFragment extends BaseFragment implements View.OnClickListener,
                                GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "LoginFragment";
    private static final String SAVED_PROGRESS = "sign-in-progress";
    private static final int SIGN_IN_PRIMARY = 10;
    private static final int SIGN_IN_CHOOSE_ACCOUNT = 11;
    private static final int STATE_DEFAULT = 0;
    private static final int STATE_SIGN_IN = 1;
    private static final int STATE_IN_PROGRESS = 2;
    private GoogleApiClient mGoogleApiClient;
    private int mSignInProgress;
    private PendingIntent mSignInIntent;
    private UserAccount mUserToLoginAs;
    @Inject GiftApiClient mApiClient;
    @Inject UserAccounts mUserAccounts;
    @Inject EventBus mEventBus;
    private SignInButton mSignInButton;


    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    public LoginFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.getInstance().inject(this);

        if (savedInstanceState != null) {
            mSignInProgress = savedInstanceState.getInt(SAVED_PROGRESS, STATE_DEFAULT);
        }
        makeGoogleApiClient();
    }

    private void makeGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    private void makeGoogleApiClient(String accountName) {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .setAccountName(accountName)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        TextView title = (TextView) rootView.findViewById(R.id.login_screen_title);
        mSignInButton = (SignInButton) rootView.findViewById(R.id.login_screen_sign_in_button);
        mSignInButton.setSize(SignInButton.SIZE_WIDE);
        mSignInButton.setStyle(SignInButton.SIZE_WIDE, SignInButton.COLOR_LIGHT);
        mSignInButton.setOnClickListener(this);

        FontManager.setBold(getActivity().getApplicationContext(), title);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_PROGRESS, mSignInProgress);
    }

    @Override
    public void onClick(View view) {
        if (mGoogleApiClient.isConnecting()) {
            return;
        }

        switch (view.getId()) {
            case R.id.login_screen_sign_in_button :
                resolveSignInError();
                break;
        }
    }

    private void resolveSignInError() {
        if (mSignInIntent != null) {
            // We have an intent which will allow our user to sign in or
            // resolve an error.  For example if the user needs to
            // select an account to sign in with, or if they need to consent
            // to the permissions your app is requesting.

            try {
                // Send the pending intent that we stored on the most recent
                // OnConnectionFailed callback.  This will allow the user to
                // resolve the error currently preventing our connection to
                // Google Play services.
                mSignInProgress = STATE_IN_PROGRESS;
                LOGD(TAG, "Start Intent Sender");
                getActivity().startIntentSenderForResult(mSignInIntent.getIntentSender(), SIGN_IN_PRIMARY, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                LOGD(TAG, "Sign in intent could not be sent: " + e.getLocalizedMessage());
                // The intent was canceled before it was sent.  Attempt to connect to
                // get an updated ConnectionResult.
                mSignInProgress = STATE_SIGN_IN;
                mGoogleApiClient.connect();
            }
        } else {
            AccountManager am = AccountManager.get(getActivity());
            ArrayList<Account> accountPickerUsers = new ArrayList<Account>();

            for (Account account : Arrays.asList(am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE))) {
                UserAccount user = new UserAccount(account.name);
                if (!mUserAccounts.isUser(user)) {
                    accountPickerUsers.add(account);
                }
            }

            Intent googleAccountPicker = AccountPicker.newChooseAccountIntent(null, accountPickerUsers,
                            new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, true, null, null, null, null) ;
            startActivityForResult(googleAccountPicker, SIGN_IN_CHOOSE_ACCOUNT);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        LOGD(TAG, "onConnected()");
        mSignInProgress = STATE_DEFAULT;

        if (!isConnectedAccountACurrentAppUser()) {
            LOGD(TAG, "User is not an app user yet - continue");
            getProfileInformation();
            getAuthToken();
        }
    }

    private boolean isConnectedAccountACurrentAppUser() {
        String email = getAccountNameFromGoogleApiClient();
        return mUserAccounts.isUser(new UserAccount(email));
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        LOGD(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = " + result.getErrorCode()+":"+result.toString());

        if (result.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
            LOGD(TAG, "Api Unavailable");
        } else if (mSignInProgress != STATE_IN_PROGRESS) {
            LOGD(TAG, "mSignInProgress != STATE_IN_PROGRESS");
            mSignInIntent = result.getResolution();

            if (mSignInProgress == STATE_SIGN_IN) {
                LOGD(TAG, "mSignInProgress == STATE_SIGN_IN");
                resolveSignInError();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        LOGD(TAG, "onActivityResult: " + requestCode+":"+resultCode);
        if (resultCode == Activity.RESULT_CANCELED) {
            // user cancelled authorization box
            LOGD(TAG, "erase intent");
            mSignInIntent = null;
            return;
        }

        switch (requestCode) {
            case SIGN_IN_PRIMARY:
                if (resultCode == Activity.RESULT_OK) {
                    // If the error resolution was successful we should continue processing errors.
                    mSignInProgress = STATE_SIGN_IN;
                } else {
                    // If the error resolution was not successful or the user canceled,
                    // we should stop processing errors.
                    mSignInProgress = STATE_DEFAULT;
                }

                if (!mGoogleApiClient.isConnecting()) {
                    // If Google Play services resolved the issue with a dialog then
                    // onStart is not called so we need to re-attempt connection here.
                    LOGD(TAG, "Reconnecting");
                    mGoogleApiClient.connect();
                }
                break;
            case SIGN_IN_CHOOSE_ACCOUNT :
                mSignInProgress = STATE_SIGN_IN;
                mGoogleApiClient.disconnect();
                makeGoogleApiClient(intent.getStringExtra("authAccount"));
                mGoogleApiClient.connect();
                break;
        }
    }

    private String getAccountNameFromGoogleApiClient() {
        return Plus.AccountApi.getAccountName(mGoogleApiClient);
    }

    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person plusProfile = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String plusProfileName = plusProfile.getDisplayName();
                String plusProfileId = plusProfile.getId();
                String plusProfileUrl = plusProfile.getUrl();
                String plusProfilePhotoUrl = plusProfile.getImage().getUrl()
                                                .substring(0, plusProfile.getImage().getUrl().length() - 6);
                String plusProfileCoverPhotoUrl = plusProfile.getCover().getCoverPhoto().getUrl();
                String email = getAccountNameFromGoogleApiClient();

                LOGD(TAG, "Name: " + plusProfileName
                        + ", email: " + email
                        + ", plusProfileUrl: " + plusProfileUrl
                        + ", plusProfilePhotoUrl: " + plusProfilePhotoUrl
                        + ", plusProfileCoverPhotoUrl: " + plusProfileCoverPhotoUrl
                        + ", plusProfileId: " + plusProfileId);

                mUserToLoginAs = new UserAccount(email);
                mUserToLoginAs.setProfileId(plusProfileId);
                mUserToLoginAs.setProfileName(plusProfileName);
                mUserToLoginAs.setProfileUrl(plusProfileUrl);
                mUserToLoginAs.setProfilePhotoUrl(plusProfilePhotoUrl);
                mUserToLoginAs.setProfileCoverPhotoUrl(plusProfileCoverPhotoUrl);
            } else {
                LOGD(TAG, "Plus information was null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAuthToken() {
        new GetAuthTokenTask().execute();
    }

    private class GetAuthTokenTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            ((LoginActivity) getActivity()).showProgressBar();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String token = GoogleAuthUtil.getToken(getActivity().getApplicationContext(),
                                                       mUserToLoginAs.getEmail(),
                                                       "audience:server:client_id:" + Config.SERVER_CLIENT_ID);
                LOGD(TAG, "Got Token: " + token);
                mUserToLoginAs.setToken(token);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GoogleAuthException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            registerUser();
        }
    }

    private void registerUser() {
        if (mUserToLoginAs.getToken() != null) {
            mApiClient.registerUser(new RegistrationUser(mUserToLoginAs), new Callback<UserAccount>() {
                @Override
                public void success(UserAccount user, Response response) {
                    LOGD(TAG, "registerUser:success: " + response.getStatus() + ":" + response.getReason());
                    LOGD(TAG, "User: " + user.toString());
                    mUserToLoginAs.setId(user.getId());
                    mUserToLoginAs.setLoggedIn(true);
                    mUserAccounts.loginAs(mUserToLoginAs);
                    mEventBus.post(new EventUserAccountAdded());
                    Toast.makeText(getActivity(), "Succesfully Logged In", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                    hideProgressBar();
                }

                @Override
                public void failure(RetrofitError error) {
                    LOGD(TAG, "error:registerUser: " + error.toString());
                    Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
                    hideProgressBar();
                }
            });
        } else {
            LOGD(TAG, "Token was null");
            hideProgressBar();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        LOGD(TAG, "onConnectionSuspended()");
        mGoogleApiClient.connect();
    }

    private void hideProgressBar() {
        ((LoginActivity) getActivity()).hideProgressBar();
    }
}
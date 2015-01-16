package com.mocca_capstone.potlatch.views;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;

import com.mocca_capstone.potlatch.R;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGD;


public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    private ProgressBar mProgressBar;

    public static Intent newInstance(Context ctx) {
        return new Intent(ctx, LoginActivity.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeActionBarToolbar(false, true);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_holder, LoginFragment.newInstance())
                    .commit();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActionBarToolbar().setBackgroundColor(getResources().getColor(R.color.theme_primary_dark));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mProgressBar = (ProgressBar) getLayoutInflater().inflate(R.layout.indeterminate_progress, null).findViewById(R.id.progress);
        mProgressBar.setVisibility(View.GONE);
        getActionBarToolbar().addView(mProgressBar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LOGD(TAG, "onActivityResult");

        Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_holder);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }
}

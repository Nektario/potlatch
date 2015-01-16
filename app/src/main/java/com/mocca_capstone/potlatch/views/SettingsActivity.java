package com.mocca_capstone.potlatch.views;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.mocca_capstone.potlatch.R;
import com.mocca_capstone.potlatch.application.Injector;

public class SettingsActivity extends BaseActivity {


    public static Intent newInstance(Context ctx) {
        return new Intent(ctx, SettingsActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.getInstance().inject(this);
        setContentView(R.layout.activity_settings);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.fragment_holder, new SettingsFragment());
            ft.commit();
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}

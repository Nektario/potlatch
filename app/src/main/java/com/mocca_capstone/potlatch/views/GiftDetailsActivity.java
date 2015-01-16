package com.mocca_capstone.potlatch.views;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import com.mocca_capstone.potlatch.R;
import com.mocca_capstone.potlatch.models.Gift;
import com.mocca_capstone.potlatch.widgets.EndlessCollectionView;

import java.io.Serializable;

/**
 * Created by nektario on 10/16/2014.
 */
public class GiftDetailsActivity extends BaseActivity {
    private static final String TAG = "GiftDetailsActivity";


    public static Intent newInstance(Context ctx, Gift gift) {
        Intent intent = new Intent(ctx, GiftDetailsActivity.class);
        intent.putExtra("extra-gift", (Serializable) gift);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift_details);


        initializeActionBarToolbar(true, true);
        Gift gift = getIntent().getParcelableExtra("extra-gift");
        if (savedInstanceState == null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.main_content, GiftDetailsFragment.newInstance(gift));
            ft.commit();
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        enableActionBarAutoHide((EndlessCollectionView) findViewById(R.id.list), false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }
}

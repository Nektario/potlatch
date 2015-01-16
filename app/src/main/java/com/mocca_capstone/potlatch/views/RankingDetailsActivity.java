package com.mocca_capstone.potlatch.views;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.mocca_capstone.potlatch.R;
import com.mocca_capstone.potlatch.contentprovider.GiftContract;
import com.mocca_capstone.potlatch.models.SearchItem;
import com.mocca_capstone.potlatch.models.User;
import com.mocca_capstone.potlatch.widgets.EndlessCollectionView;

/**
 * Created by nektario on 11/1/2014.
 */
public class RankingDetailsActivity extends BaseActivity {
    private static final String TAG = "RankingsDetailsActivity";

    public static Intent newInstance(Context ctx, User user) {
        Intent intent = new Intent(ctx, RankingDetailsActivity.class);
        intent.putExtra("extra-user", user);
        return intent;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift_details);

        initializeActionBarToolbar(false, true);
        if (savedInstanceState == null) {
            final User user = (User) getIntent().getSerializableExtra("extra-user");
            final SearchItem search = new SearchItem(GiftContract.Gifts.OWNER_ID,
                                                     String.valueOf(user.getId()),
                                                     user.getProfileName());
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.main_content, GiftListFragment.newInstance(search));
            ft.commit();
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        enableActionBarAutoHide((EndlessCollectionView) findViewById(R.id.list), false);
    }
}

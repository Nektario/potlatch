package com.mocca_capstone.potlatch.views;

import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import com.mocca_capstone.potlatch.R;
import com.mocca_capstone.potlatch.application.Injector;
import com.mocca_capstone.potlatch.contentprovider.GiftContract;
import com.mocca_capstone.potlatch.models.SearchItem;
import com.mocca_capstone.potlatch.widgets.EndlessCollectionView;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGD;

/**
 * Created by nektario on 10/22/2014.
 */
public class SearchActivity extends BaseActivity {
    private static final String TAG = "SearchActivity";
    private SearchItem mSearch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift_chain_list);
        Injector.getInstance().inject(this);
        initializeActionBarToolbar(false, true);
        getSearchTerms();

        if (savedInstanceState == null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.main_content, GiftListFragment.newInstance(mSearch));
            ft.commit();
        }
    }

    private void getSearchTerms() {
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            String searchTerms = intent.getStringExtra(SearchManager.QUERY);
            mSearch = new SearchItem(GiftContract.Gifts.TITLE, searchTerms, searchTerms);
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        LOGD(TAG, "onPostCreate");
        enableActionBarAutoHide((EndlessCollectionView) findViewById(R.id.list), false);
    }
}


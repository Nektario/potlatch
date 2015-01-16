package com.mocca_capstone.potlatch.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.mocca_capstone.potlatch.R;
import com.mocca_capstone.potlatch.application.Injector;
import com.mocca_capstone.potlatch.imageloader.ImageLoader;
import com.mocca_capstone.potlatch.utilities.SystemUiHider;

import javax.inject.Inject;

/**
 * Created by nektario on 11/8/2014.
 */
public class DisplayPictureActivity extends BaseActivity {
    private ImageView mPicture;
    private String mPictureUrl;
    private SystemUiHider mSysUiHider;
    @Inject ImageLoader mImgageLoader;


    public static Intent newInstance(Context context, String url) {
        Intent intent = new Intent(context, DisplayPictureActivity.class);
        intent.putExtra("extra-picture-url", url);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_picture);
        Injector.getInstance().inject(this);

        mPicture = (ImageView) findViewById(R.id.picture);
        mPictureUrl = getIntent().getStringExtra("extra-picture-url");
        mImgageLoader.loadWithoutDecoration(mPictureUrl, mPicture);
        mSysUiHider = new SystemUiHider(this, mPicture, SystemUiHider.UiMode.IMMERSIVE_STICKY, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSysUiHider.hide();
    }
}

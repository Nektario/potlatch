package com.mocca_capstone.potlatch.views;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;

import com.mocca_capstone.potlatch.R;
import com.mocca_capstone.potlatch.application.Injector;
import com.mocca_capstone.potlatch.imageloader.ImageLoader;
import com.mocca_capstone.potlatch.models.BaseUser;
import com.mocca_capstone.potlatch.models.UserAccount;
import com.mocca_capstone.potlatch.utilities.DeviceUtils;
import com.mocca_capstone.potlatch.widgets.EndlessCollectionView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import javax.inject.Inject;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGD;

/**
 * Created by nektario on 11/1/2014.
 */
public class ProfileActivity extends BaseActivity {
    private static final String TAG = "ProfileActivity";
    private BaseUser mUser;
    @Inject ImageLoader mImageLoader;

    public static Intent newInstance(Context activity, BaseUser user) {
        Intent intent = new Intent(activity, ProfileActivity.class);
        intent.putExtra("extra-user", user);
        return intent;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Injector.getInstance().inject(this);
        initializeActionBarToolbar(false, true);

        mUser = (BaseUser) getIntent().getSerializableExtra("extra-user");
        if (savedInstanceState == null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.main_content, ProfileFragment.newInstance(mUser));
            ft.commit();
        }
    }

    public Bitmap addGradient(Bitmap src) {
        int overlayHeight = DeviceUtils.convertDpToPixel(32);
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap overlay = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);

        canvas.drawBitmap(src, 0, 0, null);

        LinearGradient shader = new LinearGradient(0, 0, 0, overlayHeight, 0x99000000, Color.TRANSPARENT, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(shader);
        canvas.drawRect(0, 0, w, overlayHeight, paint);

        return overlay;
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        String coverPhoto;
        if (mUser instanceof UserAccount) {
            coverPhoto = ((UserAccount) mUser).getProfileCoverPhotoUrl();
        } else {
            coverPhoto = mUser.getProfilePhotoUrl()+"-cover.png";
        }

        LOGD(TAG, "Going to load: " + coverPhoto);
        mImageLoader.loadIntoTarget(coverPhoto, new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                bitmap = addGradient(bitmap);
                getActionBarToolbar().setBackground(new BitmapDrawable(getResources(), bitmap));
            }
            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        enableActionBarAutoHide((EndlessCollectionView) findViewById(R.id.list), false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }
}

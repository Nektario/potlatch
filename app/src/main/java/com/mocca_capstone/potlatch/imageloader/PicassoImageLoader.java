package com.mocca_capstone.potlatch.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.mocca_capstone.potlatch.application.Config;
import com.mocca_capstone.potlatch.application.ForApplication;
import com.mocca_capstone.potlatch.application.Injector;
import com.mocca_capstone.potlatch.utilities.Utils;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import javax.inject.Inject;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGD;
import static com.mocca_capstone.potlatch.utilities.MyLog.LOGWTF;

/**
 * Created by nektario on 7/29/2014.
 */
public class PicassoImageLoader implements ImageLoader {
    private static final String TAG = "PicassoImageLoader";
    private final Picasso mPicasso;
    private final LruCache mMemoryCache;
    private final Downloader mDownloader;
    private static final Bitmap.Config mBitmapConfig = Bitmap.Config.RGB_565;
    private static final int MAX_WIDTH = 1280;
    private static final int MAX_HEIGHT = 1280;
    private static final PicassoMaxSizeTransform mTransform = new PicassoMaxSizeTransform(MAX_WIDTH, MAX_HEIGHT);


    @Inject
    public PicassoImageLoader(@ForApplication Context appContext) {
        Injector.getInstance().inject(this);
        mMemoryCache = new LruCache(Utils.calculateMemCacheSize(Config.BITMAP_MEM_CACHE_PERCENT));
        mDownloader = new OkHttpDownloader(appContext, Config.BITMAP_DISK_CACHE_BYTES_MAX);
        mPicasso = new Picasso.Builder(appContext)
                .indicatorsEnabled(Config.PICASSO_INDICATORS)
                .loggingEnabled(Config.PICASSO_LOG)
                .memoryCache(mMemoryCache)
                .downloader(mDownloader)
                .build();
        LOGD(TAG, "Memory cache created (size = " + mMemoryCache.maxSize() / 1024 / 1024 + "MB)");
    }

    @Override
    public void load(String url, ImageView target) {
        if (url != null && target != null) {
            mPicasso.load(url).config(mBitmapConfig).transform(mTransform).into(target);
        } else {
            LOGWTF(TAG, "Url and/or target is empty!!");
        }
    }

    @Override
    public void loadWithoutDecoration(String url, ImageView target) {
        if (url != null && target != null) {
            mPicasso.load(url).config(mBitmapConfig).transform(mTransform).noFade().into(target);
        } else {
            LOGWTF(TAG, "Url and/or target is empty!!");
        }
    }

    public void loadIntoTarget(String url, Target target) {
        if (url != null && target != null) {
            mPicasso.load(url).into(target);
        } else {
            LOGWTF(TAG, "Url and/or target is empty!!");
        }
    }

    @Override
    public void prefetch(String url) {
        if (url != null) {
            mPicasso.load(url).config(mBitmapConfig).transform(mTransform).fetch();
        } else {
            LOGWTF(TAG, "Url is empty!!");
        }
    }
}

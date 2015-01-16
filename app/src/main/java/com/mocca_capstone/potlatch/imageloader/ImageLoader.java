package com.mocca_capstone.potlatch.imageloader;

import android.widget.ImageView;

import com.squareup.picasso.Target;

/**
 * Created by nektario on 7/29/2014.
 */
public interface ImageLoader {
    public void load(String url, ImageView imageView);
    public void loadWithoutDecoration(String url, ImageView imageView);
    public void loadIntoTarget(String url, Target target);
    public void prefetch(String url);
}

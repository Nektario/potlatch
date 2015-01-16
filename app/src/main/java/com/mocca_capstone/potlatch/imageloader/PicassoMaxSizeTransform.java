package com.mocca_capstone.potlatch.imageloader;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

/**
 * Created by nektario on 9/12/2014.
 */
public class PicassoMaxSizeTransform implements Transformation {
    int maxWidth;
    int maxHeight;

    public PicassoMaxSizeTransform(int maxWidth, int maxHeight) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        if (source.getWidth() > maxWidth || source.getHeight() > maxHeight) {
            int targetWidth;
            int targetHeight;
            double aspectRatio;

            if (source.getWidth() > source.getHeight()) {
                targetWidth = maxWidth;
                aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                targetHeight = (int) (targetWidth * aspectRatio);
            } else {
                targetHeight = maxHeight;
                aspectRatio = (double) source.getWidth() / (double) source.getHeight();
                targetWidth = (int) (targetHeight * aspectRatio);
            }

            Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
            if (result != source) {
                source.recycle();
            }
            return result;
        }

        return source;
    }

    @Override
    public String key() {
        return maxWidth + "x" + maxHeight;
    }
}

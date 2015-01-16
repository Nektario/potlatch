package com.mocca_capstone.potlatch.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.mocca_capstone.potlatch.R;

/**
 * Created by nektario on 11/16/2014.
 */
public class StartMiddleSeekBar extends TallSeekBar {
    private int mMiddleValue;

    public StartMiddleSeekBar(Context context) {
        super(context);
        init();
    }

    public StartMiddleSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StartMiddleSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mRect = new Rect();
        mPaint = new Paint();
        mMiddleValue = getProgress();
    }


    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.getProgress() > mMiddleValue) {
            mRect.set(getWidth() / 2,
                    (getHeight() / 2) - (SEEKBAR_HEIGHT / 2),
                    getWidth() / 2 + (getWidth() / getMax()) * (getProgress() - mMiddleValue),
                    getHeight() / 2 + (SEEKBAR_HEIGHT / 2));

            mPaint.setColor(getResources().getColor(R.color.theme_accent_1));
            canvas.drawRect(mRect, mPaint);

        }

        if (this.getProgress() < mMiddleValue) {
            mRect.set(getWidth() / 2 - ((getWidth() / getMax()) * (mMiddleValue - getProgress())),
                    (getHeight() / 2) - (SEEKBAR_HEIGHT / 2),
                    getWidth() / 2,
                    getHeight() / 2 + (SEEKBAR_HEIGHT / 2));

            mPaint.setColor(getResources().getColor(R.color.theme_accent_1));
            canvas.drawRect(mRect, mPaint);
        }
    }
}

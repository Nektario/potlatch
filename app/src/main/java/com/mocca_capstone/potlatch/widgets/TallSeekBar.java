package com.mocca_capstone.potlatch.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.mocca_capstone.potlatch.R;

/**
 * Created by nektario on 11/16/2014.
 */
public class TallSeekBar extends SeekBar {
    protected static final int SEEKBAR_HEIGHT = 6;
    protected Rect mRect;
    protected Paint mPaint;

    public TallSeekBar(Context context) {
        super(context);

    }

    public TallSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TallSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        mRect.set(getThumbOffset(),
                (getHeight() / 2) - (SEEKBAR_HEIGHT / 2),
                getWidth() - getThumbOffset(),
                (getHeight() / 2) + (SEEKBAR_HEIGHT / 2));

        mPaint.setColor(getResources().getColor(R.color.seekbar_track));
        canvas.drawRect(mRect, mPaint);
        super.onDraw(canvas);
    }
}

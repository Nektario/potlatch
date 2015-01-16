package com.mocca_capstone.potlatch.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGWTF;

/**
 * Created by nektario on 10/15/2014.
 */
public abstract class BaseGridView extends HeaderGridView implements AbsListView.OnScrollListener {
    private static final String TAG = "BaseGridView";
    private List<OnScrollListener> mScrollListeners = new ArrayList<OnScrollListener>();
    private int mContentTopClearance = 0;


    public BaseGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setOnScrollListener(this);
    }

    public void addOnScrollListener(OnScrollListener listener) {
        if (!mScrollListeners.contains(listener)) {
            mScrollListeners.add(listener);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView listView, int scrollState) {
        for (OnScrollListener listener : mScrollListeners) {
            listener.onScrollStateChanged(listView, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        for (OnScrollListener listener : mScrollListeners) {
            listener.onScroll(absListView, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        LOGWTF(TAG, "This does nothing!  Use addOnScrollListener() instead");
        // do nothing. addOnScrollListener must be used
    }

    public void setContentTopClearance(int clearance) {
        if (mContentTopClearance != clearance) {
            mContentTopClearance = clearance;
            setPadding(getPaddingLeft(), mContentTopClearance, getPaddingRight(), getPaddingBottom());
        }
    }
}

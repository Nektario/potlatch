package com.mocca_capstone.potlatch.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGWTF;

/**
 * Created by nektario on 9/21/2014.
 */
public abstract class BaseListView extends ListView implements AbsListView.OnScrollListener {
    private static final String TAG = "BaseListView";
    private List<OnScrollListener> mScrollListeners = new ArrayList<OnScrollListener>();
    private int mContentTopClearance = 0;


    public BaseListView(Context context, AttributeSet attrs) {
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
        addOnScrollListener(l);
    }

    public void setContentTopClearance(int clearance) {
        if (mContentTopClearance != clearance) {
            mContentTopClearance = clearance;
            setPadding(getPaddingLeft(), mContentTopClearance, getPaddingRight(), getPaddingBottom());
        }
    }
}

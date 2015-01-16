package com.mocca_capstone.potlatch.widgets;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by nektario on 10/13/2014.
 */
public class EndlessListView extends BaseListView implements EndlessCollectionView {

    public EndlessListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void setSelection(int position, int top) {
        setSelectionFromTop(position, top);
    }

    @Override
    public void showStatusView() {

    }

    @Override
    public void hideStatusView() {

    }

    @Override
    public void resetStatusView() {

    }

    @Override
    public void setErrorText(int stringResourceId) {

    }

    @Override
    public void setStatusViewOnClickListener(OnClickListener listener) {

    }
}

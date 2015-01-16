package com.mocca_capstone.potlatch.widgets;

import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import java.util.List;

/**
 * Created by nektario on 10/13/2014.
 */
public interface EndlessCollectionView {
    public void setContentTopClearance(int clearance);
    public void addHeaderView(View view);
    public void setSelection(int position, int top);
    public View getChildAt(int position);
    public int getFirstVisiblePosition();
    public int getLastVisiblePosition();
    public void showStatusView();
    public void hideStatusView();
    public void resetStatusView();
    public void setErrorText(int stringResourceId);
    public void setDividerHeight(int height);
    public void addOnScrollListener(AbsListView.OnScrollListener listener);
    public void setStatusViewOnClickListener(View.OnClickListener listener);
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener);
    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener);
    public void setAdapter(ListAdapter adapter);
    public Object getItemAtPosition(int position);
}

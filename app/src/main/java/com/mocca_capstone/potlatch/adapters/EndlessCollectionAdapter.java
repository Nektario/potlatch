package com.mocca_capstone.potlatch.adapters;

import android.widget.ListAdapter;

import java.util.List;

/**
 * Created by nektario on 10/13/2014.
 */
public interface EndlessCollectionAdapter<T> extends ListAdapter {
    public T getItem(int position);
    public void addItem(T obj);
    public void addList(List<T> list);
    public void clear();
    public void refresh();
}

package com.mocca_capstone.potlatch.providers;

import java.util.List;

/**
 * Created by nektario on 10/16/2014.
 */
public interface StorageProvider<T> {
    public List<T> load();
    public void save(List<T> items);
}

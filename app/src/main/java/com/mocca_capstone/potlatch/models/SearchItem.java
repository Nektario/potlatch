package com.mocca_capstone.potlatch.models;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by nektario on 11/1/2014.
 */
public class SearchItem implements Serializable {
    public String selection;
    public String selectionArgs;
    public String description;

    public SearchItem(String selection, String selectionArgs, String description) {
        this.selection = selection;
        this.selectionArgs = selectionArgs;
        this.description = description;
    }
}

package com.mocca_capstone.potlatch.listeners;

import com.mocca_capstone.potlatch.models.Gift;

/**
 * Created by nektario on 10/20/2014.
 */
public interface GiftCallbacks {
    public void onTouchedIconClicked(Gift gift);
    public void onFlagIconClicked(Gift gift);
}

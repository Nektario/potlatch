package com.mocca_capstone.potlatch.providers;

/**
 * Created by nektario on 9/9/2014.
 */
public interface AppPrefs {
    public void initializeDefaultValues();
    public boolean hideFlaggedGifts();
    public int getSyncInterval();
    public boolean isFirstTimeRunning();
    public void setFirstTimeRunComplete();
}

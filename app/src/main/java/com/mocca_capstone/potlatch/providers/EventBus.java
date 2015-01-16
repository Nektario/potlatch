package com.mocca_capstone.potlatch.providers;

/**
 * Created by nektario on 6/15/2014.
 */
public interface EventBus {
    public void register(Object object);
    public void unRegister(Object object);
    public void post(Object event);
    public void postOnUiThread(final Object event);
}

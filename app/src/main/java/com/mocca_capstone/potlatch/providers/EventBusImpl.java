package com.mocca_capstone.potlatch.providers;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

//private static final Handler mUiThread = new Handler(Looper.getMainLooper());

/**
 * Created by nektario on 4/29/2014.
 */
public enum EventBusImpl implements EventBus {
    INSTANCE;
    private final Handler mUiThread;

    // Singleton
    private static final Bus mBus = new Bus(ThreadEnforcer.ANY);
    private EventBusImpl() {
        mUiThread = new Handler(Looper.getMainLooper());
    }

    public static EventBusImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public void register(Object object) {
        mBus.register(object);
    }

    @Override
    public void unRegister(Object object) {
        mBus.unregister(object);
    }

    @Override
    public void post(Object event) {
        mBus.post(event);
    }

    @Override
    public void postOnUiThread(final Object event) {
        mUiThread.post(new Runnable() {
            @Override
            public void run() {
                mBus.post(event);
            }
        });
    }
}

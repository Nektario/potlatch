package com.mocca_capstone.potlatch.application;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGD;

/**
 * Created by nektario on 8/25/2014.
 */
public class Injector {
    private static Injector mInjector;
    private ObjectGraph mObjectGraph;
    private PotlatchApp mApp;

    private Injector() {

    }

    public static Injector getInstance() {
        if (mInjector == null) {
            mInjector = new Injector();
        }
        return mInjector;
    }

    public void initialize(PotlatchApp app) {
        mApp = app;
        mObjectGraph = ObjectGraph.create(getModules().toArray());
        mObjectGraph.injectStatics();
        LOGD("Injector", String.format("init object graph = %s", mObjectGraph.toString()));
    }

    public void inject(Object object) {
        mObjectGraph.inject(object);
    }

    private List<Object> getModules() {
        return Arrays.<Object>asList(
                new MainModule(),
                new AndroidModule(mApp)
        );
    }
}
package com.mocca_capstone.potlatch.application;

import android.content.ContentResolver;
import android.content.Context;
import android.view.LayoutInflater;

import com.mocca_capstone.potlatch.utilities.DeviceUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by nektario on 7/30/2014.
 */
@Module(library = true)
public class AndroidModule {
    private final PotlatchApp mApplication;

    public AndroidModule(PotlatchApp application) {
        this.mApplication = application;
    }

    /**
     * Allow the application context to be injected but require that it be annotated with
     * {@link ForApplication @Annotation} to explicitly differentiate it from an activity context.
     */
    @Provides @Singleton @ForApplication Context provideApplicationContext() {
        return mApplication.getApplicationContext();
    }

    @Provides LayoutInflater provideLayoutInflater() {
        return (LayoutInflater) mApplication.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Provides ContentResolver provideContentResolver() {
        return mApplication.getApplicationContext().getContentResolver();
    }
}

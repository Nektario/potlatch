package com.mocca_capstone.potlatch.application;

import android.content.Context;

import com.mocca_capstone.potlatch.adapters.FollowedUsersAdapter;
import com.mocca_capstone.potlatch.adapters.GiftAdapter;
import com.mocca_capstone.potlatch.adapters.EndlessListAdapter;
import com.mocca_capstone.potlatch.adapters.RankingsAdapter;
import com.mocca_capstone.potlatch.imageloader.ImageLoader;
import com.mocca_capstone.potlatch.imageloader.PicassoImageLoader;
import com.mocca_capstone.potlatch.network.ApiTaskService;
import com.mocca_capstone.potlatch.network.GiftApiServiceInterceptor;
import com.mocca_capstone.potlatch.network.UploadNewGiftCommand;
import com.mocca_capstone.potlatch.providers.AppPrefs;
import com.mocca_capstone.potlatch.providers.AppPrefsImpl;
import com.mocca_capstone.potlatch.providers.EventBus;
import com.mocca_capstone.potlatch.providers.EventBusImpl;
import com.mocca_capstone.potlatch.network.GiftApiClient;
import com.mocca_capstone.potlatch.providers.FollowedUsers;
import com.mocca_capstone.potlatch.providers.FollowedUsersImpl;
import com.mocca_capstone.potlatch.providers.UserAccounts;
import com.mocca_capstone.potlatch.providers.UserAccountsImpl;
import com.mocca_capstone.potlatch.sync.SyncAdapter;
import com.mocca_capstone.potlatch.views.AddGiftActivity;
import com.mocca_capstone.potlatch.views.DisplayPictureActivity;
import com.mocca_capstone.potlatch.views.FollowedUsersFragment;
import com.mocca_capstone.potlatch.views.GiftDetailsActivity;
import com.mocca_capstone.potlatch.views.GiftDetailsFragment;
import com.mocca_capstone.potlatch.views.GiftListFragment;
import com.mocca_capstone.potlatch.views.MainActivity;
import com.mocca_capstone.potlatch.views.BaseGiftFragment;
import com.mocca_capstone.potlatch.views.LoginActivity;
import com.mocca_capstone.potlatch.views.LoginFragment;
import com.mocca_capstone.potlatch.views.ProfileActivity;
import com.mocca_capstone.potlatch.views.ProfileFragment;
import com.mocca_capstone.potlatch.views.RankingDetailsActivity;
import com.mocca_capstone.potlatch.views.RankingsFragment;
import com.mocca_capstone.potlatch.views.SearchActivity;
import com.mocca_capstone.potlatch.views.SettingsActivity;
import com.mocca_capstone.potlatch.views.SettingsFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by nektario on 7/29/2014.
 */
@Module(
        complete = true,
        library = true,
        includes = {
                AndroidModule.class
        },
        injects = {
                AddGiftActivity.class,
                ApiTaskService.class,
                BaseGiftFragment.class,
                DisplayPictureActivity.class,
                EndlessListAdapter.class,
                FollowedUsersImpl.class,
                FollowedUsersFragment.class,
                FollowedUsersAdapter.class,
                GiftAdapter.class,
                GiftApiClient.class,
                GiftApiServiceInterceptor.class,
                GiftListFragment.class,
                GiftDetailsActivity.class,
                GiftDetailsFragment.class,
                LoginActivity.class,
                LoginFragment.class,
                MainActivity.class,
                PicassoImageLoader.class,
                ProfileActivity.class,
                ProfileFragment.class,
                RankingDetailsActivity.class,
                RankingsAdapter.class,
                RankingsFragment.class,
                SearchActivity.class,
                SettingsActivity.class,
                SettingsFragment.class,
                SyncAdapter.class,
                UploadNewGiftCommand.class,
                UserAccountsImpl.class
        }
)
public class MainModule {

    @Provides @Singleton
    public ImageLoader provideImageLoader(@ForApplication Context context) {
        return new PicassoImageLoader(context);
    }

    @Provides @Singleton
    public EventBus provideEventBus() {
        return EventBusImpl.getInstance();
    }

    @Provides
    public GiftApiClient provideGiftApiClient() {
        return new GiftApiClient();
    }

    @Provides @Singleton
    public UserAccounts provideUsers(@ForApplication Context context) {
        return new UserAccountsImpl(context);
    }

    @Provides @Singleton
    public FollowedUsers provideFollwedUsers(@ForApplication Context context) {
        return new FollowedUsersImpl(context);
    }

    @Provides @Singleton
    public AppPrefs provideAppPrefs(@ForApplication Context context) {
        return new AppPrefsImpl(context);
    }
}

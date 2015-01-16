package com.mocca_capstone.potlatch.providers;

import android.content.Context;
import android.os.SystemClock;

import com.google.gson.reflect.TypeToken;
import com.mocca_capstone.potlatch.application.ForApplication;
import com.mocca_capstone.potlatch.application.Injector;
import com.mocca_capstone.potlatch.models.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mocca_capstone.potlatch.utilities.MyLog.LOGD;

/**
 * Created by nektario on 10/28/2014.
 */
public class FollowedUsersImpl implements FollowedUsers {
    private static final String TAG = "FollowedUsersImpl";
    private StorageProvider<User> mStorageProvider;
    private List<User> mFollowedUsers;


    public FollowedUsersImpl(@ForApplication Context appContext) {
        Injector.getInstance().inject(this);
        mStorageProvider = new FileProvider<User>(appContext, "followedusers", new TypeToken<List<User>>() {}.getType());
        loadFollowedUsers();
    }

    @Override
    public void followUser(User userToFollow) {
        if (!mFollowedUsers.contains(userToFollow)) {
            LOGD(TAG, "+ Followed user: " + userToFollow.getProfileName());
            mFollowedUsers.add(userToFollow);
        }
        saveFollowedUsers();
    }

    @Override
    public void unfollowUser(User userToUnfollow) {
        if (mFollowedUsers.contains(userToUnfollow)) {
            LOGD(TAG, "- Followed user: " + userToUnfollow.getProfileName());
            mFollowedUsers.remove(userToUnfollow);
        }
        saveFollowedUsers();
    }

    @Override
    public boolean isFollowedUser(User user) {
        return mFollowedUsers.contains(user);
    }

    @Override
    public List<User> getFollowedUsers() {
        return new ArrayList<User>(mFollowedUsers);
    }


    public void saveFollowedUsers() {
        mStorageProvider.save(mFollowedUsers);
    }

    private void loadFollowedUsers() {
        long start = SystemClock.elapsedRealtime();
        mFollowedUsers = mStorageProvider.load();
        Collections.sort(mFollowedUsers);
        long end = SystemClock.elapsedRealtime();
        long diff = end - start;
        LOGD("UserProvider", "Time to load followed users: " + diff + "ms");
    }
}

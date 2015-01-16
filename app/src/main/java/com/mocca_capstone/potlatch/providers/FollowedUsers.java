package com.mocca_capstone.potlatch.providers;

import com.mocca_capstone.potlatch.models.User;

import java.util.List;

/**
 * Created by nektario on 10/28/2014.
 */
public interface FollowedUsers {
    public void followUser(User userToFollow);
    public void unfollowUser(User userToUnfollow);
    public boolean isFollowedUser(User user);
    public List<User> getFollowedUsers();
}

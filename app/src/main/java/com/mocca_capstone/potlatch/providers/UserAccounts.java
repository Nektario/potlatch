package com.mocca_capstone.potlatch.providers;

import com.mocca_capstone.potlatch.listeners.OnUserDeletedListener;
import com.mocca_capstone.potlatch.models.UserAccount;

import java.util.List;

/**
 * Created by nektario on 10/16/2014.
 */
public interface UserAccounts {
    public UserAccount getActiveUser();
    public List<UserAccount> getUsers();
    public boolean isLoggedIn();
    public boolean isUser(UserAccount user);
    public void loginAs(UserAccount user);
    public void addUser(UserAccount user);
    public void addUsers(List<UserAccount> users);
    public void deleteUser(UserAccount user, OnUserDeletedListener listener);
    public void saveUsers();
}

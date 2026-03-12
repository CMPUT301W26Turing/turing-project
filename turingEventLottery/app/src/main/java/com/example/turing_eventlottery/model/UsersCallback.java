package com.example.turing_eventlottery.model;

import java.util.List;

/**
 * Callback interface used to handle asynchronous retrieval of a list of users.
 */
public interface UsersCallback {
    /**
     * Called when the list of users is successfully retrieved.
     *
     * @param users the list of retrieved User objects
     */
    void onSuccess(List<User> users);
}

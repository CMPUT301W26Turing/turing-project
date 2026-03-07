package com.example.turing_eventlottery.model;

/**
 * Callback interface used to handle asynchronous user retrieval from
 * the Firebase Firestone database.
 * Implementations define behavior for successful user retrieval or failure
 * when accessing user data.
 *
 * @author Matthew Adams
 * @version 1.0
 * @since 1.0
 * @see User
 */
public interface UserCallback {

    /**
     * Called when the user data is successfully retrieved.
     *
     * @param user the retrieved User object
     */
    void onSuccess(User user);

    /**
     * Called when an error occurs while retrieving the user data.
     *
     * @param e the exception describing the failure
     */
    void onFailure(Exception e);
}

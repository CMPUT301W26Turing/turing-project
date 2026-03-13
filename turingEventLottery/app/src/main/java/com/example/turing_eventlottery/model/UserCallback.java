package com.example.turing_eventlottery.model;

/**
 * Callback interface used to handle asynchronous user retrieval from
 * the Firebase Firestone database.
 * Implementations define behavior for successful user retrieval or failure
 * when accessing user data.
 *
 * @author Matthew Adams
 * @version 1.0
 * @since 03-07-2026
 * @see User
 */

/*  This interface was created with the help of Developer Android API Reference and from Stack Overflow
    API Reference: https://developer.android.com/games/services/cpp/api/group/callbacks

    Author: Eduardo Figueiredo https://stackoverflow.com/questions/65492137/why-to-use-callback-functions-when-i-can-simply-call-them
    Title: Why to use callback functions when I can simply call them?
    Answer: T.J. Crowder https://stackoverflow.com/revisions/c65727c4-bcd3-4360-b4b6-ccfc81904608/view-source
    Date: 2020-12-29
    License:    CC-BY-SA    4.0     (International)
 */
public interface UserCallback {

    /**
     * Called when the user data is successfully retrieved.
     *
     * @param user the retrieved User object
     */
    void onSuccess(User user);
}
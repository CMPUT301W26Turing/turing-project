package com.example.turing_eventlottery.model;

/**
 * Callback interface used to handle asynchronous event and event related detail retrieval from
 * the Firebase Firestone database.
 * Implementations define behavior for successful event and event related detail retrieval or failure
 * when accessing event data. This interface uses a type T in the callback to handle different scenarios
 * such as accessing certain elements of an event's details.
 *
 * @author Matthew Adams
 * @version 1.1
 * @since 03-07-2026
 * @see User
 */
/*  This interface uses ideas for creating a generic callback that can pass multiple different types of callbacks from stack overflow.
    Author: PNS https://stackoverflow.com/questions/15260596/generic-callback-in-java
    Title: Generic callback in Java
    Answer: user949300 https://stackoverflow.com/revisions/504efa0f-9516-4ea7-bd68-8c11517bdb3c/view-source
    Date: 2013-03-07
    License:    CC-BY-SA    4.0     (International)
 */
public interface ModelCallback<T> {
    void onCallback(T result);
}

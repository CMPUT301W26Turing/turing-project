package com.example.turing_eventlottery;

import android.content.Context;
import android.provider.ContactsContract;
import android.provider.Settings;

public abstract class User {
    protected final String userId;
    protected String contactInfo;


    /**
     * Constructs a new User with the specified user ID and contact information
     * @param userId
     *      the unique device ID for this user
     * @param contactInfo
     *      the contact information for the user
     */
    public User(String userId, String contactInfo) {
        this.userId = userId;
        this.contactInfo = contactInfo;
    }

    /**
     * Getter for user ID
     * @return
     *      returns the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Getter for user contact information
     * @return
     *      returns the user contact information
     */
    public String getContactInfo() {
        return contactInfo;
    }

    /**
     * Method to update the users contact information with the new information
     * @param contactInfo
     *      the new contact information to set
     */
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}

package com.example.turing_eventlottery.model;

import com.example.turing_eventlottery.viewmodel.UserViewModel;

/**
 * Represents a user in the system.
 * A user can be an admin, organizer, entrant, or guest.
 * Users may also be marked as banned
 * <p>
 * This class stores the user's ID, contact information,
 * and role/status flags are used by the application.
 * </p>
 *
 * @author Matthew Adams
 * @version 1.0
 * @since 1.0
 * @see UserViewModel
 */
public class User {
    private String userId;
    private String contactInfo;
    private boolean isAdmin = false;
    private boolean isBanned = false;

    private String deviceId;

    /**
     * A default constructor required for Firebase Firestore.
     * Firestone uses this constructor when reconstructing
     * User objects from the database.
     */
    public User() {} // for Firestone

    /**
     * Creates a user (entrant/organizer/admin)
     * with contact information.
     *
     * @param userId the unique identifier of the user
     * @param contactInfo the user's contact information
     * @param isAdmin true if admin
     * @param isBanned true if the user is banned
     */
    public User(String userId, String contactInfo, boolean isAdmin, boolean isBanned) {
        this.userId = userId;
        this.contactInfo = contactInfo;
        this.isAdmin = isAdmin;
        this.isBanned = isBanned;
    }

    /**
     * Creates a "guest" user for new devices that are
     * not found in the database.
     *
     * @param userId the userID
     * @return new user
     */
    public User createGuest(String userId) {
        return new User(userId, null, false, false);
    }

    /**
     * Gets the unique ID of the user
     *
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Gets the contact information of the user
     *
     * @return the user's contact information
     */
    public String getContactInfo() {
        return contactInfo;
    }

    /**
     * Checks if the user is an admin
     *
     * @return true if the user is an admin, otherwise returns false
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    /**
     * Checks if the user is banned
     *
     * @return true if the user is banned, otherwise returns false
     */
    public boolean isBanned() {
        return isBanned;
    }

    /**
     * Sets the contact information for the user
     *
     * @param contactInfo the new contact information
     */
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    /**
     * Sets the banned status for the user
     *
     * @param banned true to ban the user, false to unban
     */
    public void setBanned(boolean banned) {
        isBanned = banned;
    }


    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
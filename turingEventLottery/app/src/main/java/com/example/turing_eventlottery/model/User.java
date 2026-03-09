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
 * @version 1.1
 * @since 1.0
 * @see UserViewModel
 */
public class User {
    private String userId;
    private String userName;
    private String userEmail;
    private String userPhoneNumber;

    private boolean isAdmin = false;
    private boolean isBanned = false;

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
     * @param userName the user's full name
     * @param userEmail the user's email
     * @param userPhoneNumber the user's phone number
     * @param isAdmin true if admin
     * @param isBanned true if the user is banned
     */
    public User(String userId, String userName, String userEmail, String userPhoneNumber, boolean isAdmin, boolean isBanned) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhoneNumber = userPhoneNumber;
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
    public static User createGuest(String userId) {
        return new User(userId, "Guest", null, null, false, false);
    }

    /**
     * Gets the unique ID of the user
     *
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
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
     * Sets the banned status for the user
     *
     * @param banned true to ban the user, false to unban
     */
    public void setBanned(boolean banned) {
        isBanned = banned;
    }
}
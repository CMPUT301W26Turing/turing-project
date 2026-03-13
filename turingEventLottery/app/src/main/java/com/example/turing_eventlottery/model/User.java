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
 * @since 03-07-2026
 * @see UserViewModel
 */
public class User {
    private String userId;
    private String userName;
    private String userEmail;
    private String userPhoneNumber;

    private boolean isAdmin = false;
    private boolean isOrganizer = false;
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
     * @param userName the user's full name
     * @param userEmail the user's email
     * @param userPhoneNumber the user's phone number
     * @param isAdmin true if admin
     * @param isOrganizer true if organizer
     * @param isBanned true if the user is banned
     */
    public User(String userId, String userName, String userEmail, String userPhoneNumber, boolean isAdmin, boolean isOrganizer, boolean isBanned) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhoneNumber = userPhoneNumber;
        this.isAdmin = isAdmin;
        this.isOrganizer = isOrganizer;
        this.isBanned = isBanned;
    }

    /**
     * Creates a "guest" user for new devices that are
     * not found in the database.
     *
     * @param userId the userID
     * @return User a guest User object
     */
    public static User createGuest(String userId) {
        return new User(userId, "Guest", null, null, false, false, false);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isOrganizer() {
        return isOrganizer;
    }

    public void setOrganizer(boolean organizer) {
        isOrganizer = organizer;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

}

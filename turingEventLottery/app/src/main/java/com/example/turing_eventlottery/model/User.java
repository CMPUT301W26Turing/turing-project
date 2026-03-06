package com.example.turing_eventlottery.model;

public class User {
    private String userId;
    private String contactInfo;
    private boolean isAdmin = false;
    private boolean isBanned = false;
    private boolean isGuest = false;

    /**
     * Constructor for Firestone database
     */
    public User() {} // for Firestone

    /**
     * Constructor for guests, organizers, admins
     * @param userId
     * @param isGuest
     * @param isAdmin
     * @param isBanned
     */
    public User(String userId, boolean isGuest, boolean isAdmin, boolean isBanned) {
        this.userId = userId;
        this.isGuest = isGuest;
        this.isAdmin = isAdmin;
        this.isBanned = isBanned;
        this.contactInfo = null;
    }

    /**
     * Constructor for Entrants
     * @param userId
     * @param contactInfo
     * @param isBanned
     */
    public User(String userId, String contactInfo, boolean isBanned) {
        this.userId = userId;
        this.contactInfo = contactInfo;
        this.isAdmin = false;
        this.isGuest = false;
        this.isBanned = isBanned;
    }

    /**
     *
     * @return
     */
    public String getUserId() {
        return userId;
    }

    /**
     *
     * @return
     */
    public String getContactInfo() {
        return contactInfo;
    }

    /**
     *
     * @return
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    /**
     *
     * @return
     */
    public boolean isBanned() {
        return isBanned;
    }

    /**
     *
     * @return
     */
    public boolean isGuest() {
        return isGuest;
    }

    /**
     *
     * @param contactInfo
     */
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    /**
     *
     * @param banned
     */
    public void setBanned(boolean banned) {
        isBanned = banned;
    }
}
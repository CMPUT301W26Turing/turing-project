package com.example.turing_eventlottery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.example.turing_eventlottery.model.User;

public class UserTest {
    private User guestUser;
    private User adminUser;
    private User bannedUser;
    private User setBannedUser;

    @Before
    public void setUp() {
        // Guest user
        guestUser = new User("guest", true, false, false);

        // Admin user
        adminUser = new User("admin123", false, true, false);

        // Banned user
        bannedUser = new User("user01", "user01@example.com", true);

        // setBannedUser
        setBannedUser = new User("user02", "user02@example.com", false);
    }

    @Test
    public void testGuestUserFlags() {
        assertTrue(guestUser.isGuest());
        assertFalse(guestUser.isAdmin());
        assertFalse(guestUser.isBanned());
    }

    @Test
    public void testAdminUserFlags() {
        assertTrue(adminUser.isAdmin());
        assertFalse(adminUser.isGuest());
        assertFalse(adminUser.isBanned());
    }

    @Test
    public void testBannedUserFlagsAndContact() {
        assertTrue(bannedUser.isBanned());
        assertEquals("user01@example.com", bannedUser.getContactInfo());
        assertFalse(bannedUser.isGuest());
        assertFalse(bannedUser.isAdmin());
    }

    @Test
    public void testSetContactInfo() {
        bannedUser.setContactInfo("new01@example.com");
        assertEquals("new01@example.com", bannedUser.getContactInfo());
    }

    @Test
    public void testSetBanned() {
        setBannedUser.setBanned(true);
        assertTrue(setBannedUser.isBanned());
    }

    @Test
    public void testUserId() {
        assertEquals("guest", guestUser.getUserId());
        assertEquals("admin123", adminUser.getUserId());
        assertEquals("user01", bannedUser.getUserId());
        assertEquals("user02", setBannedUser.getUserId());
    }
}

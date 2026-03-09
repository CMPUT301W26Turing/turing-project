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

    /**
    @Before
    public void setUp() {
        // Guest user
        guestUser = new User("guest", null, false, false);

        // Admin user
        adminUser = new User("admin123", null, true, false);

        // Banned user
        bannedUser = new User("user01", "user01@example.com", false, true);

        // setBannedUser
        setBannedUser = new User("user02", "user02@example.com", false, false);
    }

    @Test
    public void testAdminUserFlags() {
        assertTrue(adminUser.isAdmin());
        assertFalse(adminUser.isBanned());
        assertNull(adminUser.getContactInfo());
    }

    @Test
    public void testBannedUserFlagsAndContact() {
        assertTrue(bannedUser.isBanned());
        assertEquals("user01@example.com", bannedUser.getContactInfo());
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
    */
}

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
    private User regularUser;

    @Before
    public void setUp() {
        guestUser = User.createGuest("guest123");
        adminUser = new User("admin123", "Admin User", "admin@example.com", null, true, false);
        bannedUser = new User("user01", "Banned User", "user01@example.com", "780-555-0001", false, true);
        regularUser = new User("user02", "Regular User", "user02@example.com", "780-555-0002", false, false);
    }

    @Test
    public void testGuestUser() {
        assertEquals("guest123", guestUser.getUserId());
        assertEquals("Guest", guestUser.getUserName());
        assertNull(guestUser.getUserEmail());
        assertNull(guestUser.getUserPhoneNumber());
        assertFalse(guestUser.isAdmin());
        assertFalse(guestUser.isBanned());
    }

    @Test
    public void testAdminUserFlags() {
        assertTrue(adminUser.isAdmin());
        assertFalse(adminUser.isBanned());
        assertEquals("admin@example.com", adminUser.getUserEmail());
        assertNull(adminUser.getUserPhoneNumber());
    }

    @Test
    public void testBannedUserFlags() {
        assertTrue(bannedUser.isBanned());
        assertFalse(bannedUser.isAdmin());
        assertEquals("user01@example.com", bannedUser.getUserEmail());
        assertEquals("780-555-0001", bannedUser.getUserPhoneNumber());
    }

    @Test
    public void testSetBanned() {
        regularUser.setBanned(true);
        assertTrue(regularUser.isBanned());
    }

    @Test
    public void testSetAdmin() {
        regularUser.setAdmin(true);
        assertTrue(regularUser.isAdmin());
    }

    @Test
    public void testSetUserName() {
        regularUser.setUserName("Updated Name");
        assertEquals("Updated Name", regularUser.getUserName());
    }

    @Test
    public void testSetUserEmail() {
        regularUser.setUserEmail("new@example.com");
        assertEquals("new@example.com", regularUser.getUserEmail());
    }

    @Test
    public void testSetUserPhoneNumber() {
        regularUser.setUserPhoneNumber("780-555-9999");
        assertEquals("780-555-9999", regularUser.getUserPhoneNumber());
    }

    @Test
    public void testUserId() {
        assertEquals("guest123", guestUser.getUserId());
        assertEquals("admin123", adminUser.getUserId());
        assertEquals("user01", bannedUser.getUserId());
        assertEquals("user02", regularUser.getUserId());
    }

    @Test
    public void testSetUserId() {
        regularUser.setUserId("newId");
        assertEquals("newId", regularUser.getUserId());
    }
}

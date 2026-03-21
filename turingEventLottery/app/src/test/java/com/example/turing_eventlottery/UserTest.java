package com.example.turing_eventlottery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.example.turing_eventlottery.model.User;

public class UserTest {
    private User user;
    private User emptyUser;
    private User guestUser;

    @Before
    public void setUp() {
        user = new User(
                "Cas",
                "Castiel",
                "castiel@angle.com",
                "1234567890",
                true,
                true,
                false
        );
        emptyUser = new User();
        guestUser = User.createGuest("guest67");
    }

    @Test
    public void testUserConstructor() {
        assertEquals("Cas", user.getUserId());
        assertEquals("Castiel", user.getUserName());
        assertEquals("castiel@angle.com", user.getUserEmail());
        assertEquals("1234567890", user.getUserPhoneNumber());
        assertTrue(user.isAdmin());
        assertFalse(user.isBanned());
    }

    @Test
    public void testEmptyConstructor() {
        assertNull(emptyUser.getUserId());
        assertNull(emptyUser.getUserName());
        assertNull(emptyUser.getUserEmail());
        assertNull(emptyUser.getUserPhoneNumber());
        assertFalse(emptyUser.isAdmin());
        assertFalse(emptyUser.isBanned());
    }

    @Test
    public void testCreateGuest() {
        assertEquals("guest67", guestUser.getUserId());
        assertEquals("Guest", guestUser.getUserName());
        assertNull(guestUser.getUserEmail());
        assertNull(guestUser.getUserPhoneNumber());
        assertFalse(guestUser.isAdmin());
        assertFalse(guestUser.isBanned());
    }

    @Test
    public void testSetUserId() {
        emptyUser.setUserId("newId");
        assertEquals("newId", emptyUser.getUserId());
    }

    @Test
    public void testSetUserName() {
        emptyUser.setUserName("Dean");
        assertEquals("Dean", emptyUser.getUserName());
    }

    @Test
    public void testSetUserEmail() {
        emptyUser.setUserEmail("dean@winchester.com");
        assertEquals("dean@winchester.com", emptyUser.getUserEmail());
    }

    @Test
    public void testSetUserPhoneNumber() {
        emptyUser.setUserPhoneNumber("0987654321");
        assertEquals("0987654321", emptyUser.getUserPhoneNumber());
    }

    @Test
    public void testSetAdmin() {
        emptyUser.setAdmin(true);
        assertTrue(emptyUser.isAdmin());
        emptyUser.setAdmin(false);
        assertFalse(emptyUser.isAdmin());
    }

    @Test
    public void testSetBanned() {
        emptyUser.setBanned(true);
        assertTrue(emptyUser.isBanned());
        emptyUser.setBanned(false);
        assertFalse(emptyUser.isBanned());
    }
}

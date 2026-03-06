package com.example.turing_eventlottery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.example.turing_eventlottery.model.User;

public class userTest {
    private User testUser;

    @Before
    public void setUp() {
        testUser = new User("device123", false, false, false);
    }

    @Test
    public void testUserIdMatchesStored() {
        assertEquals("device123", testUser.getUserId());
    }

    @Test
    public void testUsersContactInfoIsNull() {
        assertNull(testUser.getContactInfo());
    }

    @Test
    public void testUpdatedContactInfoCorrectlyChanged() {
        testUser.setContactInfo("newemail@example.com");
        assertEquals("newemail@example.com", testUser.getContactInfo());
    }

    @Test
    public void testUserIsNotAdminByDefault() {
        assertFalse(testUser.isAdmin());
    }

    @Test
    public void testUserIsNotBannedByDefault() {
        assertFalse(testUser.isAdmin());
    }
}

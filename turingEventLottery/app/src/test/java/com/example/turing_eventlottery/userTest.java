package com.example.turing_eventlottery;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class userTest {
    private User testUser;

    private static class TestUser extends User {
        public TestUser(String deviceId, String contactInfo) {
            super(deviceId, contactInfo);
        }
    }

    @Before
    public void setUp() {
        testUser = new TestUser("device123", "test@example.com");
    }

    @Test
    public void testUserIdMatchesStored() {
        assertEquals("device123", testUser.getUserId());
    }

    @Test
    public void testUserContactInfoMatchesStored() {
        assertEquals("test@example.com", testUser.getContactInfo());
    }

    @Test
    public void testUpdatedContactInfoCorrectlyChanged() {
        testUser.setContactInfo("newemail@example.com");
        assertEquals("newemail@example.com", testUser.getContactInfo());
    }
}

package com.example.turing_eventlottery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.example.turing_eventlottery.model.Notification;

import java.util.Arrays;
import java.util.List;

public class NotificationTest {
    private Notification notification1;
    private Notification notification2;
    private Notification emptyNotification;

    @Before
    public void setUp() {
        notification1 = new Notification(
                "user1",
                "event1",
                "New Year Party",
                "2027-01-01",
                "You are invited!",
                "unread"
        );
        notification2 = new Notification(
                "user2",
                "event2",
                "Workshop",
                "2026-11-11",
                "read"
        );
        emptyNotification = new Notification();
    }

    @Test
    public void testNotificationConstructorWithEverything() {
        assertEquals("user1", notification1.getUserId());
        assertEquals("event1", notification1.getEventId());
        assertEquals("New Year Party", notification1.getEventName());
        assertEquals("2027-01-01", notification1.getEventDate());
        assertEquals("You are invited!", notification1.getMessage());
        assertEquals("unread", notification1.getStatus());
        assertFalse(notification1.isSystemLog());
    }

    @Test
    public void testNotificationConstructorWithoutMessage() {
        assertEquals("user2", notification2.getUserId());
        assertEquals("event2", notification2.getEventId());
        assertEquals("Workshop", notification2.getEventName());
        assertEquals("2026-11-11", notification2.getEventDate());
        assertNull(notification2.getMessage());
        assertEquals("read", notification2.getStatus());
        assertFalse(notification2.isSystemLog());
    }

    @Test
    public void testEmptyConstructor() {
        assertNull(emptyNotification.getUserId());
        assertNull(emptyNotification.getEventId());
        assertNull(emptyNotification.getEventName());
        assertNull(emptyNotification.getEventDate());
        assertNull(emptyNotification.getMessage());
        assertNull(emptyNotification.getStatus());
        assertFalse(emptyNotification.isSystemLog());
    }

    @Test
    public void testSetId() {
        emptyNotification.setId("notifId");
        assertEquals("notifId", emptyNotification.getId());
    }

    @Test
    public void testSetUserId() {
        emptyNotification.setUserId("user123");
        assertEquals("user123", emptyNotification.getUserId());
    }

    @Test
    public void testSetEventId() {
        emptyNotification.setEventId("event123");
        assertEquals("event123", emptyNotification.getEventId());
    }

    @Test
    public void testSetEventName() {
        emptyNotification.setEventName("Concert");
        assertEquals("Concert", emptyNotification.getEventName());
    }

    @Test
    public void testSetEventDate() {
        emptyNotification.setEventDate("2026-12-25");
        assertEquals("2026-12-25", emptyNotification.getEventDate());
    }

    @Test
    public void testSetMessage() {
        emptyNotification.setMessage("Hello World");
        assertEquals("Hello World", emptyNotification.getMessage());
    }

    @Test
    public void testSetStatus() {
        emptyNotification.setStatus("seen");
        assertEquals("seen", emptyNotification.getStatus());
    }

    @Test
    public void testSetOrganizerId() {
        emptyNotification.setOrganizerId("org123");
        assertEquals("org123", emptyNotification.getOrganizerId());
    }

    @Test
    public void testSetRecipients() {
        List<String> recipients = Arrays.asList("user1", "user2");
        emptyNotification.setRecipients(recipients);
        assertEquals(recipients, emptyNotification.getRecipients());
    }

    @Test
    public void testSetSystemLog() {
        emptyNotification.setSystemLog(true);
        assertTrue(emptyNotification.isSystemLog());
        emptyNotification.setSystemLog(false);
        assertFalse(emptyNotification.isSystemLog());
    }
}

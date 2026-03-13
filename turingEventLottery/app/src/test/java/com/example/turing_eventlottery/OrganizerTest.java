package com.example.turing_eventlottery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.example.turing_eventlottery.model.Event;
import com.example.turing_eventlottery.model.User;

public class OrganizerTest {
    private User organizer;
    private User regularUser;
    private User bannedOrganizer;
    private Event organizerEvent;

    @Before
    public void setUp() {
        organizer = new User(
                "org1",
                "John Organizer",
                "john@example.com",
                "1234567890",
                false,
                true,
                false
        );
        regularUser = new User(
                "user1",
                "Jane User",
                "jane@example.com",
                "0987654321",
                false,
                false,
                false
        );
        bannedOrganizer = new User(
                "org2",
                "Banned Bob",
                "bob@example.com",
                "1112223333",
                false,
                true,
                true
        );
        organizerEvent = new Event(
                "event1",
                "org1",
                "Community Meetup",
                "Social",
                "Edmonton",
                "04/01/2026",
                "https://example.com/poster.jpg",
                "A community gathering",
                "03/20/2026, 09:00",
                "03/28/2026, 17:00",
                3,
                50,
                false
        );
    }

    @Test
    public void testOrganizerFlagTrue() {
        assertTrue(organizer.isOrganizer());
    }

    @Test
    public void testRegularUserNotOrganizer() {
        assertFalse(regularUser.isOrganizer());
    }

    @Test
    public void testSetOrganizerStatus() {
        assertFalse(regularUser.isOrganizer());
        regularUser.setOrganizer(true);
        assertTrue(regularUser.isOrganizer());
    }

    @Test
    public void testRemoveOrganizerStatus() {
        assertTrue(organizer.isOrganizer());
        organizer.setOrganizer(false);
        assertFalse(organizer.isOrganizer());
    }

    @Test
    public void testRemoveOrganizerDoesNotBan() {
        assertTrue(organizer.isOrganizer());
        assertFalse(organizer.isBanned());
        organizer.setOrganizer(false);
        assertFalse(organizer.isOrganizer());
        assertFalse(organizer.isBanned());
    }

    @Test
    public void testBannedOrganizerFlags() {
        assertTrue(bannedOrganizer.isOrganizer());
        assertTrue(bannedOrganizer.isBanned());
    }

    @Test
    public void testBannedOrganizerUnban() {
        assertTrue(bannedOrganizer.isBanned());
        bannedOrganizer.setBanned(false);
        assertFalse(bannedOrganizer.isBanned());
        assertTrue(bannedOrganizer.isOrganizer());
    }

    @Test
    public void testAutoOrganizerAfterSetStatus() {
        assertFalse(regularUser.isOrganizer());
        regularUser.setOrganizer(true);
        regularUser.setBanned(false);
        assertTrue(regularUser.isOrganizer());
        assertFalse(regularUser.isBanned());
    }

    @Test
    public void testOrganizerIsNotAdmin() {
        assertTrue(organizer.isOrganizer());
        assertFalse(organizer.isAdmin());
    }

    @Test
    public void testEventBelongsToOrganizer() {
        assertEquals("org1", organizerEvent.getOrganizerId());
        assertEquals(organizer.getUserId(), organizerEvent.getOrganizerId());
    }

    @Test
    public void testEventNotBelongsToOtherUser() {
        assertFalse(regularUser.getUserId().equals(organizerEvent.getOrganizerId()));
    }

    @Test
    public void testEventDetails() {
        assertEquals("Community Meetup", organizerEvent.getName());
        assertEquals("Social", organizerEvent.getCategory());
        assertEquals("Edmonton", organizerEvent.getLocation());
        assertEquals("04/01/2026", organizerEvent.getDate());
        assertEquals(3, organizerEvent.getWinnersToDraw());
        assertEquals(50, organizerEvent.getWaitlistCap());
        assertFalse(organizerEvent.isGeolocationRequired());
    }

    @Test
    public void testOrganizerContactInfo() {
        assertEquals("John Organizer", organizer.getUserName());
        assertEquals("john@example.com", organizer.getUserEmail());
        assertEquals("1234567890", organizer.getUserPhoneNumber());
    }

    @Test
    public void testGuestCanBecomeOrganizer() {
        User guest = User.createGuest("guest1");
        assertFalse(guest.isOrganizer());
        guest.setOrganizer(true);
        assertTrue(guest.isOrganizer());
    }

    @Test
    public void testOrganizerCanCreateMultipleEvents() {
        Event event1 = new Event(
                "e1", "org1", "Event One", "Tech",
                "Calgary", "05/01/2026", null,
                "First event", "04/20/2026, 09:00",
                "04/28/2026, 17:00", 2, 30, false
        );
        Event event2 = new Event(
                "e2", "org1", "Event Two", "Music",
                "Edmonton", "06/01/2026", null,
                "Second event", "05/20/2026, 09:00",
                "05/28/2026, 17:00", 5, 100, true
        );
        assertEquals("org1", event1.getOrganizerId());
        assertEquals("org1", event2.getOrganizerId());
        assertEquals(event1.getOrganizerId(), event2.getOrganizerId());
    }
}

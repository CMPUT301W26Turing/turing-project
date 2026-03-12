package com.example.turing_eventlottery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.example.turing_eventlottery.model.Event;

public class EventTest {
    private Event event;
    private Event emptyEvent;

    @Before
    public void setUp() {
        event = new Event(
                "event1",
                "organizer1",
                "Test Event",
                "Technology",
                "Edmonton",
                "03/15/2026",
                "https://example.com/poster.jpg",
                "A test event description",
                "03/01/2026, 09:00",
                "03/10/2026, 17:00",
                5,
                100,
                true
        );
        emptyEvent = new Event();
    }

    @Test
    public void testEventConstructor() {
        assertEquals("event1", event.getId());
        assertEquals("organizer1", event.getOrganizerId());
        assertEquals("Test Event", event.getName());
        assertEquals("Technology", event.getCategory());
        assertEquals("Edmonton", event.getLocation());
        assertEquals("03/15/2026", event.getDate());
        assertEquals("https://example.com/poster.jpg", event.getPosterUrl());
        assertEquals("A test event description", event.getDescription());
        assertEquals("03/01/2026, 09:00", event.getRegStart());
        assertEquals("03/10/2026, 17:00", event.getRegEnd());
        assertEquals(5, event.getWinnersToDraw());
        assertEquals(100, event.getWaitlistCap());
        assertTrue(event.isGeolocationRequired());
    }

    @Test
    public void testEmptyConstructor() {
        assertNull(emptyEvent.getId());
        assertNull(emptyEvent.getName());
        assertNull(emptyEvent.getOrganizerId());
        assertNull(emptyEvent.getCategory());
        assertNull(emptyEvent.getLocation());
        assertNull(emptyEvent.getDate());
        assertNull(emptyEvent.getPosterUrl());
        assertNull(emptyEvent.getDescription());
        assertNull(emptyEvent.getRegStart());
        assertNull(emptyEvent.getRegEnd());
        assertEquals(0, emptyEvent.getWinnersToDraw());
        assertEquals(0, emptyEvent.getWaitlistCap());
        assertFalse(emptyEvent.isGeolocationRequired());
    }

    @Test
    public void testSetId() {
        emptyEvent.setId("newId");
        assertEquals("newId", emptyEvent.getId());
    }

    @Test
    public void testSetName() {
        emptyEvent.setName("New Event Name");
        assertEquals("New Event Name", emptyEvent.getName());
    }

    @Test
    public void testSetOrganizerId() {
        emptyEvent.setOrganizerId("org123");
        assertEquals("org123", emptyEvent.getOrganizerId());
    }

    @Test
    public void testSetCategory() {
        emptyEvent.setCategory("Music");
        assertEquals("Music", emptyEvent.getCategory());
    }

    @Test
    public void testSetLocation() {
        emptyEvent.setLocation("Calgary");
        assertEquals("Calgary", emptyEvent.getLocation());
    }

    @Test
    public void testSetDate() {
        emptyEvent.setDate("04/20/2026");
        assertEquals("04/20/2026", emptyEvent.getDate());
    }

    @Test
    public void testSetPosterUrl() {
        emptyEvent.setPosterUrl("https://example.com/new.jpg");
        assertEquals("https://example.com/new.jpg", emptyEvent.getPosterUrl());
    }

    @Test
    public void testSetDescription() {
        emptyEvent.setDescription("Updated description");
        assertEquals("Updated description", emptyEvent.getDescription());
    }

    @Test
    public void testSetRegStart() {
        emptyEvent.setRegStart("04/01/2026, 08:00");
        assertEquals("04/01/2026, 08:00", emptyEvent.getRegStart());
    }

    @Test
    public void testSetRegEnd() {
        emptyEvent.setRegEnd("04/10/2026, 18:00");
        assertEquals("04/10/2026, 18:00", emptyEvent.getRegEnd());
    }

    @Test
    public void testSetWinnersToDraw() {
        emptyEvent.setWinnersToDraw(10);
        assertEquals(10, emptyEvent.getWinnersToDraw());
    }

    @Test
    public void testSetWaitlistCap() {
        emptyEvent.setWaitlistCap(200);
        assertEquals(200, emptyEvent.getWaitlistCap());
    }

    @Test
    public void testSetGeolocationRequired() {
        emptyEvent.setGeolocationRequired(true);
        assertTrue(emptyEvent.isGeolocationRequired());

        emptyEvent.setGeolocationRequired(false);
        assertFalse(emptyEvent.isGeolocationRequired());
    }
}

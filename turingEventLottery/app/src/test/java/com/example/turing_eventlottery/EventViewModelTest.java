package com.example.turing_eventlottery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import android.widget.ArrayAdapter;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.turing_eventlottery.model.Event;
import com.example.turing_eventlottery.model.EventRepository;
import com.example.turing_eventlottery.model.ModelCallback;
import com.example.turing_eventlottery.model.User;
import com.example.turing_eventlottery.viewmodel.EventViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class EventViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private EventRepository mockRepository;

    @Mock
    private User mockUser;

    private EventViewModel eventViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        eventViewModel = new EventViewModel(mockRepository);
    }

    // Helper to build Calendar date at UTC midnight
    private Calendar makeCalendar(int year, int month, int day) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(year, month - 1, day, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    // Helper to build events with only the information we're currently testing
    private Event makeEvent(String date, String regStart, String regEnd, int waitlistCap) {
        Event event = new Event();
        event.setDate(date);
        event.setRegStart(regStart);
        event.setRegEnd(regEnd);
        event.setWaitlistCap(waitlistCap);
        event.setId("test-event-1");
        return event;
    }

    // matchesAvailability tests
    @Test
    public void testFilterEventsAvailabilityOnlyEventWithinRangeIsIncluded() {
        Event event = makeEvent("03/25/2026, 10:00", "03/01/2026, 00:00", "03/31/2026, 00:00", 10);
        List<Event> events = Arrays.asList(event);

        doAnswer(invocation -> {
            ModelCallback<List<Event>> callback = invocation.getArgument(0);
            callback.onCallback(events);
            return null;
        }).when(mockRepository).getEvents(any());

        eventViewModel.loadAllEvents();

        Calendar start = makeCalendar(2026, 3, 23);
        Calendar end = makeCalendar(2026, 3, 28);

        eventViewModel.filterEvents(mockUser, start, end, false);

        List<Event> result = eventViewModel.getFilteredEventsLiveData().getValue();
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testFilterEventsAvailabilityOnlyEventsOutsideRangeIsExcluded() {
        Event event = makeEvent("03/30/2026, 10:00", "03/01/2026, 00:00", "03/31/2026, 00:00", 10);
        List<Event> events = Arrays.asList(event);

        doAnswer(invocation -> {
            ModelCallback<List<Event>> callback = invocation.getArgument(0);
            callback.onCallback(events);
            return null;
        }).when(mockRepository).getEvents(any());

        eventViewModel.loadAllEvents();

        Calendar start = makeCalendar(2026, 3, 23);
        Calendar end = makeCalendar(2026, 3, 28);

        eventViewModel.filterEvents(mockUser, start, end, false);

        List<Event> result = eventViewModel.getFilteredEventsLiveData().getValue();
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    // isRegOpen tests
    @Test
    public void testFilterEventsOpenWaitlistRegClosedIsExcluded() {
        // regStart and regEnd both in the past
        Event event = makeEvent("03/25/2026, 10:00", "01/01/2026, 00:00", "01/02/2026, 00:00", 10);
        List<Event> events = Arrays.asList(event);

        doAnswer(invocation -> {
            ModelCallback<List<Event>> callback = invocation.getArgument(0);
            callback.onCallback(events);
            return null;
        }).when(mockRepository).getEvents(any());

        eventViewModel.loadAllEvents();

        eventViewModel.filterEvents(mockUser, null, null, true);

        List<Event> result = eventViewModel.getFilteredEventsLiveData().getValue();
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testFilterEventsOpenWaitlistRegOpenCapNotHitIsIncluded() {
        // regStart in past, regEnd in future, waitlist not full
        Event event = makeEvent("03/25/2026, 10:00", "01/01/2026, 00:00", "12/31/2026, 00:00", 10);
        List<Event> events = Arrays.asList(event);

        doAnswer(invocation -> {
            ModelCallback<List<Event>> callback = invocation.getArgument(0);
            callback.onCallback(events);
            return null;
        }).when(mockRepository).getEvents(any());

        // Mock waitlist count returning 5 (under cap of 10)
        doAnswer(invocation -> {
            ModelCallback<Integer> callback = invocation.getArgument(1);
            callback.onCallback(5);
            return null;
        }).when(mockRepository).getWaitlistCount(eq("test-event-1"), any());

        eventViewModel.loadAllEvents();

        eventViewModel.filterEvents(mockUser, null, null, true);

        List<Event> result = eventViewModel.getFilteredEventsLiveData().getValue();
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testFilterEventsOpenWaitlistRegOpenCapHitIsExcluded() {
        // regStart in past, regEnd in future, waitlist full
        Event event = makeEvent("03/25/2026, 10:00", "01/01/2026, 00:00", "12/31/2026, 00:00", 10);
        List<Event> events = Arrays.asList(event);

        doAnswer(invocation -> {
            ModelCallback<List<Event>> callback = invocation.getArgument(0);
            callback.onCallback(events);
            return null;
        }).when(mockRepository).getEvents(any());

        // Mock waitlist count returning 10 (at cap)
        doAnswer(invocation -> {
            ModelCallback<Integer> callback = invocation.getArgument(1);
            callback.onCallback(10);
            return null;
        }).when(mockRepository).getWaitlistCount(eq("test-event-1"), any());

        eventViewModel.loadAllEvents();

        eventViewModel.filterEvents(mockUser, null, null, true);

        List<Event> result = eventViewModel.getFilteredEventsLiveData().getValue();
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testFilterEventsBothFiltersCombined() {
        // Event within date range, reg open, waitlist not full — should be included
        Event event = makeEvent("03/25/2026, 10:00", "01/01/2026, 00:00", "12/31/2026, 00:00", 10);
        List<Event> events = Arrays.asList(event);

        doAnswer(invocation -> {
            ModelCallback<List<Event>> callback = invocation.getArgument(0);
            callback.onCallback(events);
            return null;
        }).when(mockRepository).getEvents(any());

        doAnswer(invocation -> {
            ModelCallback<Integer> callback = invocation.getArgument(1);
            callback.onCallback(5);
            return null;
        }).when(mockRepository).getWaitlistCount(eq("test-event-1"), any());

        eventViewModel.loadAllEvents();

        Calendar start = makeCalendar(2026, 3, 23);
        Calendar end = makeCalendar(2026, 3, 28);

        eventViewModel.filterEvents(mockUser, start, end, true);

        List<Event> result = eventViewModel.getFilteredEventsLiveData().getValue();
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}

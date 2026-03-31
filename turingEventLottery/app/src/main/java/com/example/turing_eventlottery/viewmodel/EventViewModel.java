package com.example.turing_eventlottery.viewmodel;

import com.example.turing_eventlottery.model.Event;
import com.example.turing_eventlottery.model.ModelCallback;
import com.example.turing_eventlottery.model.EventRepository;
import com.example.turing_eventlottery.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * Manages event-related operations
 * <p>
 *     This class acts as an intermediary between the Views and the {@link EventRepository},
 *     providing methods to adding, deleting, querying events, managing waitlists,
 *     and formatting event dates.
 * </p>
 *
 * @author Matthew Adams
 * @author Miro
 * @version 1.1
 * @since 03-07-2026
 * @see EventRepository
 */
public class EventViewModel {
    private EventRepository eventRepository;
    private MutableLiveData<List<Event>> filteredEventsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Event>> allEventsLiveData = new MutableLiveData<>();
    private boolean matchesAvailability(Event event, Calendar start, Calendar end) {
        if (start == null || end == null) return true;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy, HH:mm", Locale.getDefault());
            Date eventDate = sdf.parse(event.getDate());
            if (eventDate == null) return true;

            Calendar eventCal = Calendar.getInstance();
            eventCal.setTime(eventDate);

            // compare only year, month, day
            Calendar startCopy = (Calendar) start.clone();
            Calendar endCopy = (Calendar) end.clone();
            clearTime(eventCal);
            clearTime(startCopy);
            clearTime(endCopy);

            return !eventCal.before(start) && !eventCal.after(end);
        } catch (Exception e) {
            return true;
        }
    }

    private void clearTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    /**
     * Initializes the repository for the eventViewModel.
     */
    public EventViewModel() {
        this.eventRepository = new EventRepository();
    }

    // Dependency injection for mock database testing
    public EventViewModel(EventRepository repository) {
        this.eventRepository = repository;
    }

    public LiveData<List<Event>> getFilteredEventsLiveData() {
        return filteredEventsLiveData;
    }

    public LiveData<List<Event>> getAllEventsLiveData() {
        return allEventsLiveData;
    }

    public void loadAllEvents() {
        eventRepository.getEvents(events -> {
            allEventsLiveData.setValue(events);
        });
    }

    /**
     * Adds a new event to the EventRepository.
     *
     * @param event The event to add
     * @param callback callback returning {@code true} if successful, {@code false} otherwise
     */
    public void addEvent(Event event, ModelCallback<Boolean> callback) {
        eventRepository.addEvent(event, callback);
    }

    /**
     * Retrieves all events from the repository.
     *
     * @param callback callback returning a list of events
     */
    public void getEvents(ModelCallback<List<Event>> callback) {
        eventRepository.getEvents(callback);
    }

    /**
     * Retrieves all events created by a specific organizer.
     *
     * @param organizerId The organizer ID
     * @param callback callback returning a list of events created from the organizer
     */
    public void getEventsByOrganizer(String organizerId, ModelCallback<List<Event>> callback) {
        eventRepository.getEventsByOrganizer(organizerId, callback);
    }

    /**
     * Retrieves an event by its unique ID.
     *
     * @param eventId The event ID
     * @param callback callback returning the event object
     */
    public void getEventById(String eventId, ModelCallback<Event> callback) {
        eventRepository.getEventById(eventId, callback);
    }

    /**
     * Deletes an event from the repository.
     *
     * @param eventId The event ID
     * @param callback callback returning {@code true} if event deletion succeeded
     */
    public void deleteEvent(String eventId, ModelCallback<Boolean> callback) {
        eventRepository.deleteEvent(eventId, callback);
    }

    /**
     * Gets the user's status for a specific event: "Enrolled", "Invited", "Waiting", or "None"
     */
    public void getUserEventStatus(String eventId, String userId, ModelCallback<String> callback) {
        eventRepository.getUserEventStatus(eventId, userId, callback);
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    /**
     * Accepts an invitation by registering the user as a participant
     */
    public void acceptInvitation(String eventId, String userId, ModelCallback<Boolean> callback) {
        eventRepository.registerParticipant(eventId, userId, callback);
    }

    /**
     * Checks whether registration is currently open for a given event.
     *
     * @param eventId The event ID
     * @param callback callback returning true if registration is open
     */
    public void checkRegistrationStatus(String eventId, ModelCallback<Boolean> callback) {
        eventRepository.getEventRegPeriod(eventId, period -> {
            if (period == null) return;

            Date now = new Date();
            Date regStart = period.first;
            Date regEnd = period.second;

            boolean isOpen = now.after(regStart) && now.before(regEnd);
            callback.onCallback(isOpen);
        });
    }

    /**
     * Adds a user to the event's waitlist.
     *
     * @param user The user to add
     * @param eventId The event ID
     * @param callback callback returning {@code true} if added successfully
     */
    public void joinWaitlist(User user, String eventId, ModelCallback<Boolean> callback) {
        joinWaitlist(user, eventId, null, null, callback);
    }

    /**
     * Adds a user to the event's waitlist with location.
     *
     * @param user The user to add
     * @param eventId The event ID
     * @param latitude The latitude of the user
     * @param longitude The longitude of the user
     * @param callback callback returning {@code true} if added successfully
     */
    public void joinWaitlist(User user, String eventId, Double latitude, Double longitude, ModelCallback<Boolean> callback) {
        if ("Guest".equals(user.getUserName())) {
            callback.onCallback(false);
            return;
        }

        eventRepository.addUserToWaitList(eventId, user, latitude, longitude, success -> {
            callback.onCallback(success);

            // debug in Logcat
            if (success) {
                System.out.println("User added to waitlist");
            } else {
                System.out.println("Failed to join waitlist");
            }
        });
    }

    /**
     * Removes a user from an event's waitlist.
     *
     * @param user The user to remove
     * @param eventId The event ID
     * @param callback callback returning {@code true} if removal succeeded
     */
    public void leaveWaitlist(User user, String eventId, ModelCallback<Boolean> callback) {
        eventRepository.removeUserFromWaitlist(eventId, user, success -> {
            callback.onCallback(success);
          
            // debug in Logcat
            if (success) {
                System.out.println("User removed from waitlist");
            } else {
                System.out.println("Failed to remove from waitlist");
            }
        });
    }

    /**
     * Check if a user is on a specific event's waitlist.
     *
     * @param user The user to check
     * @param eventId The event ID
     * @param callback callback returning {@code true} if user is on waitlist
     */
    public void isUserOnWaitlist(User user, String eventId, ModelCallback<Boolean> callback) {
        if (user == null || "Guest".equals(user.getUserName())) {
            callback.onCallback(false);
            return;
        }
        eventRepository.checkUserOnWaitlist(eventId, user, callback);
    }

    /**
     * Formats an event date string from "MM/dd/yyyy, HH:mm" to preferred format.
     *
     * @param dateStr the raw event date string
     * @return formatted date string, or original if parsing fails
     */
    public String formatEventDate(String dateStr) {
        if (dateStr == null) return "";

        try {
            SimpleDateFormat inputDate = new SimpleDateFormat("MM/dd/yyyy, HH:mm", Locale.getDefault());
            SimpleDateFormat outputDate = new SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault());

            Date date = inputDate.parse(dateStr);
            return outputDate.format(date);
        } catch (Exception e) {
            return dateStr;
        }
    }

    /**
     * Retrieves the number of users currently on the waitlist for an event.
     *
     * @param eventId The event ID
     * @param callback callback returning the waitlist count.
     */
    public void getWaitlistCount(String eventId, ModelCallback<Integer> callback) {
        eventRepository.getWaitlistCount(eventId, count -> {
            callback.onCallback(count);
        });
    }

    private boolean isRegOpen(Event event) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy, HH:mm", Locale.getDefault());
            Date regStart = sdf.parse(event.getRegStart());
            Date regEnd = sdf.parse(event.getRegEnd());
            if (regStart == null || regEnd == null) return false;
            Date now = new Date();
            return now.after(regStart) && now.before(regEnd);
        } catch (Exception e) {
            return false;
        }
    }

    public void filterEvents(User user, Calendar startRange, Calendar endRange, boolean onlyWithOpenWaitlist) {
        List<Event> allEvents = allEventsLiveData.getValue();
        if (allEvents == null) return;

        List<Event> filtered = Collections.synchronizedList(new ArrayList<>());

        if (!onlyWithOpenWaitlist) {
            // purely local filter (availability)
            for (Event event : allEvents) {
                if (matchesAvailability(event, startRange, endRange) && matchesSearch(event)) {
                    filtered.add(event);
                }
            }
            filteredEventsLiveData.postValue(filtered);
            return;
        }

        // Waitlist filtering: asynchronous
        final int total = allEvents.size();
        final int[] completed = {0};

        for (Event event : allEvents) {
            if (!matchesAvailability(event, startRange, endRange) || !isRegOpen(event) || !matchesSearch(event)) {
                completed[0]++;
                if (completed[0] == total) filteredEventsLiveData.postValue(new ArrayList<>(filtered));
                continue;
            }

            // check waitlist size asynchronously
            getWaitlistCount(event.getId(), count -> {
                if (count != null && count < event.getWaitlistCap()) {
                    filtered.add(event);
                }

                completed[0]++;
                if (completed[0] == total) {
                    // all events processed
                    filteredEventsLiveData.postValue(new ArrayList<>(filtered));
                }
            });
        }
    }

    private String searchQuery = "";

    public void setSearchQuery(String query) {
        this.searchQuery = query == null ? "" : query.trim().toLowerCase();
    }

    private boolean matchesSearch(Event event) {
        if (searchQuery.isEmpty()) return true;

        String name = event.getName() != null ? event.getName().toLowerCase() : "";
        String location = event.getLocation() != null ? event.getLocation().toLowerCase() : "";
        String category = event.getCategory() != null ? event.getCategory().toLowerCase() : "";
        String description = event.getDescription() != null ? event.getDescription().toLowerCase() : "";

        return name.contains(searchQuery)
                || location.contains(searchQuery)
                || category.contains(searchQuery)
                ||description.contains(searchQuery);
    }
}

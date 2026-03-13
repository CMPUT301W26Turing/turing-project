package com.example.turing_eventlottery.viewmodel;

import com.example.turing_eventlottery.model.Event;
import com.example.turing_eventlottery.model.EventCallback;
import com.example.turing_eventlottery.model.EventRepository;
import com.example.turing_eventlottery.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    /**
     * Initializes the repository for the eventViewModel.
     */
    public EventViewModel() {
        this.eventRepository = new EventRepository();
    }

    /**
     * Adds a new event to the EventRepository.
     *
     * @param event The event to add
     * @param callback callback returning {@code true} if successful, {@code false} otherwise
     */
    public void addEvent(Event event, EventCallback<Boolean> callback) {
        eventRepository.addEvent(event, callback);
    }

    /**
     * Retrieves all events from the repository.
     *
     * @param callback callback returning a list of events
     */
    public void getEvents(EventCallback<List<Event>> callback) {
        eventRepository.getEvents(callback);
    }

    /**
     * Retrieves all events created by a specific organizer.
     *
     * @param organizerId The organizer ID
     * @param callback callback returning a list of events created from the organizer
     */
    public void getEventsByOrganizer(String organizerId, EventCallback<List<Event>> callback) {
        eventRepository.getEventsByOrganizer(organizerId, callback);
    }

    /**
     * Retrieves an event by its unique ID.
     *
     * @param eventId The event ID
     * @param callback callback returning the event object
     */
    public void getEventById(String eventId, EventCallback<Event> callback) {
        eventRepository.getEventById(eventId, callback);
    }

    /**
     * Deletes an event from the repository.
     *
     * @param eventId The event ID
     * @param callback callback returning {@code true} if event deletion succeeded
     */
    public void deleteEvent(String eventId, EventCallback<Boolean> callback) {
        eventRepository.deleteEvent(eventId, callback);
    }

    /**
     * Checks whether registration is currently open for a given event.
     *
     * @param eventId The event ID
     * @param callback callback returning {@code true} if registration is open
     */
    public void checkRegistrationStatus(String eventId, EventCallback<Boolean> callback) {
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
    public void joinWaitlist(User user, String eventId, EventCallback<Boolean> callback) {
        if ("Guest".equals(user.getUserName())) {
            callback.onCallback(false);
            return;
        }

        eventRepository.addUserToWaitList(eventId, user, success -> {
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
    public void leaveWaitlist(User user, String eventId, EventCallback<Boolean> callback) {
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
    public void isUserOnWaitlist(User user, String eventId, EventCallback<Boolean> callback) {
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
    public void getWaitlistCount(String eventId, EventCallback<Integer> callback) {
        eventRepository.getWaitlistCount(eventId, count -> {
            callback.onCallback(count);
        });
    }
}

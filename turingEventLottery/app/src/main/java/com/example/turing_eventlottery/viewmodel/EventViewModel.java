package com.example.turing_eventlottery.viewmodel;

import com.example.turing_eventlottery.model.Event;
import com.example.turing_eventlottery.model.EventCallback;
import com.example.turing_eventlottery.model.EventRepository;
import com.example.turing_eventlottery.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventViewModel {
    private EventRepository eventRepository;

    public EventViewModel() {
        this.eventRepository = new EventRepository();
    }

    public void addEvent(Event event, EventCallback<Boolean> callback) {
        eventRepository.addEvent(event, callback);
    }

    public void getEvents(EventCallback<List<Event>> callback) {
        eventRepository.getEvents(callback);
    }

    public void getEventsByOrganizer(String organizerId, EventCallback<List<Event>> callback) {
        eventRepository.getEventsByOrganizer(organizerId, callback);
    }

    public void getEventById(String eventId, EventCallback<Event> callback) {
        eventRepository.getEventById(eventId, callback);
    }

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

    public void isUserOnWaitlist(User user, String eventId, EventCallback<Boolean> callback) {
        if (user == null || "Guest".equals(user.getUserName())) {
            callback.onCallback(false);
            return;
        }
        eventRepository.checkUserOnWaitlist(eventId, user, callback);
    }

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

    public void getWaitlistCount(String eventId, EventCallback<Integer> callback) {
        eventRepository.getWaitlistCount(eventId, count -> {
            callback.onCallback(count);
        });
    }
}

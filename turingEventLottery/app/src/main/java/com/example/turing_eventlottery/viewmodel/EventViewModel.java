package com.example.turing_eventlottery.viewmodel;

import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.turing_eventlottery.model.Event;
import com.example.turing_eventlottery.model.EventRepository;
import com.example.turing_eventlottery.model.EventCallback;
import com.example.turing_eventlottery.model.User;

import java.util.List;
import java.util.function.Consumer;

public class EventViewModel {
    private enum WaitlistResult {GUEST, SUCCESS, FAILURE}
    private EventRepository eventRepository;

    public EventViewModel() {
        this.eventRepository = new EventRepository();
    }

    public void getEvents(EventCallback<List<Event>> callback) {
        eventRepository.getEvents(callback);
    }

    public void getEventById(String eventId, EventCallback<Event> callback) {
        eventRepository.getEventById(eventId, callback);
    }

    public void joinWaitlist(User user, String eventId) {
        if ("Guest".equals(user.getUserName())) {
            return;
        }

        eventRepository.addUserToWaitList(eventId, user, success -> {
            // debug in Logcat
            if (success) {
                System.out.println("User added to waitlist");
            } else {
                System.out.println("Failed to join waitlist");
            }
        });
    }

    public void leaveWaitlist(User user, String eventId) {
        eventRepository.removeUserFromWaitlist(eventId, user, success -> {
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
}

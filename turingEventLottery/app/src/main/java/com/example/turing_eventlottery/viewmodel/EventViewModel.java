package com.example.turing_eventlottery.viewmodel;

import com.example.turing_eventlottery.model.Event;
import com.example.turing_eventlottery.model.EventRepository;
import com.example.turing_eventlottery.model.EventCallback;

import java.util.List;

public class EventViewModel {
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

    public void deleteEvent(String eventId, EventCallback<Boolean> callback) {
        eventRepository.deleteEvent(eventId, callback);
    }
}

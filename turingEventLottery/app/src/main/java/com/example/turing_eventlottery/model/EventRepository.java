package com.example.turing_eventlottery.model;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventRepository {
    private static final String TAG = "EventRepository";
    private static final String COLLECTION_NAME = "events";

    private final CollectionReference eventsCollection;

    public EventRepository() {
        eventsCollection = FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public void addEvent(Event event, EventCallback<Boolean> callback) {
        eventsCollection.add(event)
                .addOnSuccessListener(documentReference -> {
                    event.setId(documentReference.getId());
                    Log.d(TAG, "Event added with ID: " + documentReference.getId());
                    callback.onCallback(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding event", e);
                    callback.onCallback(false);
                });
    }

    public void getEvents(EventCallback<List<Event>> callback) {
        eventsCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Event> events = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Event event = document.toObject(Event.class);
                    event.setId(document.getId());
                    events.add(event);
                }
                callback.onCallback(events);
            } else {
                Log.e(TAG, "Error getting events", task.getException());
                callback.onCallback(null);
            }
        });
    }

    public void getEventsByOrganizer(String organizerId, EventCallback<List<Event>> callback) {
        eventsCollection.whereEqualTo("organizerId", organizerId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Event> events = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Event event = document.toObject(Event.class);
                    event.setId(document.getId());
                    events.add(event);
                }
                callback.onCallback(events);
            } else {
                Log.e(TAG, "Error getting events by organizer", task.getException());
                callback.onCallback(null);
            }
        });
    }

    public void getEventById(String eventId, EventCallback<Event> callback) {
        eventsCollection.document(eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Event event = task.getResult().toObject(Event.class);
                if (event != null) {
                    event.setId(task.getResult().getId());
                }
                callback.onCallback(event);
            } else {
                Log.e(TAG, "Error getting event by ID", task.getException());
                callback.onCallback(null);
            }
        });
    }

    public void deleteEvent(String eventId, EventCallback<Boolean> callback) {
        eventsCollection.document(eventId).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Event successfully deleted!");
                    callback.onCallback(true);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error deleting event", e);
                    callback.onCallback(false);
                });
    }
}

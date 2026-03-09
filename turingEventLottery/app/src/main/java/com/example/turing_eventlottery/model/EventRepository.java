package com.example.turing_eventlottery.model;

import com.google.firebase.Timestamp;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void addUserToWaitList(String eventId, User user, EventCallback<Boolean> callback) {
        if ("Guest".equals(user.getUserName())) {
            callback.onCallback(false);
            return;
        }

        DocumentReference eventRef = eventsCollection.document(eventId);
        Map<String, Object> waitlistEntry = new HashMap<>();
        waitlistEntry.put("username", user.getUserName());
        waitlistEntry.put("timestamp", Timestamp.now());

        eventRef.collection("waitlist")
                .document(user.getUserId())
                .set(waitlistEntry)
                .addOnSuccessListener(v -> callback.onCallback(true))
                .addOnFailureListener(e -> callback.onCallback(false));
    }

    public void removeUserFromWaitlist(String eventId, User user, EventCallback<Boolean> callback) {
        CollectionReference waitlistRef = eventsCollection.document(eventId).collection("waitlist");

        waitlistRef.document(user.getUserId())
                .delete()
                .addOnSuccessListener(v -> callback.onCallback(true))
                .addOnFailureListener(e -> callback.onCallback(false));
    }

    public void checkUserOnWaitlist(String eventId, User user, EventCallback<Boolean> callback) {
        eventsCollection
                .document(eventId)
                .collection("waitlist")
                .document(user.getUserId())
                .get()
                .addOnSuccessListener(document -> {
                    boolean onWaitlist = document.exists();
                    callback.onCallback(document.exists());
                });
    }
}

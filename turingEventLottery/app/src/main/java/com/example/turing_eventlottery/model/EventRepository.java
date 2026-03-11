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

    /**
     * Accept an event invitation for a user
     */
    public void acceptInvitation(String userId, String eventId, EventCallback<Boolean> callback) {
        eventsCollection.document(eventId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        // Add user to participants list
                        eventsCollection.document(eventId)
                                .update("participants",
                                        com.google.firebase.firestore.FieldValue.arrayUnion(userId))
                                .addOnSuccessListener(v -> {
                                    // Update invitation status to accepted
                                    updateInvitationStatus(eventId, userId, "accepted");
                                    callback.onCallback(true);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error accepting invitation", e);
                                    callback.onCallback(false);
                                });
                    } else {
                        Log.e(TAG, "Event not found: " + eventId);
                        callback.onCallback(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting event", e);
                    callback.onCallback(false);
                });
    }

    /**
     * Decline an event invitation for a user
     */
    public void declineInvitation(String userId, String eventId, EventCallback<Boolean> callback) {
        // Update invitation status to declined
        updateInvitationStatus(eventId, userId, "declined");
        callback.onCallback(true);
    }

    /**
     * Helper: Update invitation status for a user
     */
    private void updateInvitationStatus(String eventId, String userId, String status) {
        eventsCollection.document(eventId)
                .update("invitations." + userId, status)
                .addOnSuccessListener(v ->
                        Log.d(TAG, "Invitation status updated for user: " + userId))
                .addOnFailureListener(e ->
                        Log.e(TAG, "Error updating invitation status", e));
    }
}

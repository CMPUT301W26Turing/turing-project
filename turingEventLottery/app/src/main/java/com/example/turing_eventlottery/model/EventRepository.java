package com.example.turing_eventlottery.model;

import com.google.firebase.Timestamp;
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

    public void getEvents(EventCallback<List<Event>> callback) {
        eventsCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Event> events = new ArrayList<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Event event = new Event(
                            document.getId(),
                            document.getString("name"),
                            document.getString("category"),
                            document.getString("location"),
                            document.getString("date"),
                            document.getString("time"),
                            document.getString("posterUrl"),
                            document.getString("description")
                    );
                    events.add(event);
                }
                System.out.println("Fetched " + events.size() + " events"); // debug
                callback.onCallback(events);
            } else {
                System.err.println("Failed to fetch events: " + task.getException()); // debug
            }
        });
    }

    public void getEventById(String eventId, EventCallback<Event> callback) {
        eventsCollection.document(eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                var document = task.getResult();
                Event event = new Event(
                        document.getId(),
                        document.getString("name"),
                        document.getString("category"),
                        document.getString("location"),
                        document.getString("date"),
                        document.getString("time"),
                        document.getString("posterUrl"),
                        document.getString("description")
                );
                callback.onCallback(event);
            } else {
                System.err.println("Failed to fetch event: " + task.getException());
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
package com.example.turing_eventlottery.model;

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
}
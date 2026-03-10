package com.example.turing_eventlottery.model;

import com.google.firebase.Timestamp;
import android.util.Log;
import android.util.Pair;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EventRepository {
    private static final String TAG = "EventRepository";
    private static final String COLLECTION_NAME = "events";

    private final CollectionReference eventsCollection;
    private final UserRepository userRepository;

    public EventRepository() {
        eventsCollection = FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
        userRepository = new UserRepository();
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

    public void getEventRegPeriod(String eventId, EventCallback<Pair<Date, Date>> callback) {
        eventsCollection.document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    try {
                        String regStartStr = documentSnapshot.getString("regStart");
                        String regEndStr = documentSnapshot.getString("regEnd");

                        if (regStartStr == null || regEndStr == null) {
                            callback.onCallback(null);
                            return;
                        }

                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy, HH:mm", Locale.getDefault());
                        Date regStart = sdf.parse(regStartStr);
                        Date regEnd = sdf.parse(regEndStr);

                        callback.onCallback(new Pair<>(regStart, regEnd));
                    } catch (Exception e) {
                        callback.onCallback(null);
                    }
                })
                .addOnFailureListener(e -> callback.onCallback(null));
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
        waitlistEntry.put("status", "Waiting");

        eventRef.collection("waitlist")
                .document(user.getUserId())
                .set(waitlistEntry)
                .addOnSuccessListener(v -> callback.onCallback(true))
                .addOnFailureListener(e -> callback.onCallback(false));
    }

    public void removeUserFromWaitlist(String eventId, User user, EventCallback<Boolean> callback) {
        eventsCollection.document(eventId).collection("waitlist")
                .document(user.getUserId())
                .delete()
                .addOnSuccessListener(v -> callback.onCallback(true))
                .addOnFailureListener(e -> callback.onCallback(false));
    }

    public void getWaitlistUsers(String eventId, EventCallback<List<String>> callback) {
        eventsCollection.document(eventId).collection("waitlist")
                .whereEqualTo("status", "Waiting")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> userIds = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userIds.add(document.getId());
                        }
                        callback.onCallback(userIds);
                    } else {
                        callback.onCallback(null);
                    }
                });
    }

    public void updateWaitlistStatus(String eventId, String userId, String status, EventCallback<Boolean> callback) {
        eventsCollection.document(eventId).collection("waitlist")
                .document(userId)
                .update("status", status)
                .addOnSuccessListener(v -> callback.onCallback(true))
                .addOnFailureListener(e -> callback.onCallback(false));
    }

    public void registerParticipant(String eventId, String userId, EventCallback<Boolean> callback) {
        userRepository.getUser(userId, user -> {
            if (user != null) {
                // Remove from waitlist collection and add to participants array in event doc
                removeUserFromWaitlist(eventId, user, success -> {
                    if (success) {
                        eventsCollection.document(eventId)
                                .update("participants", FieldValue.arrayUnion(userId))
                                .addOnSuccessListener(v -> callback.onCallback(true))
                                .addOnFailureListener(e -> callback.onCallback(false));
                    } else {
                        callback.onCallback(false);
                    }
                });
            } else {
                callback.onCallback(false);
            }
        });
    }

    public void checkUserOnWaitlist(String eventId, User user, EventCallback<Boolean> callback) {
        eventsCollection
                .document(eventId)
                .collection("waitlist")
                .document(user.getUserId())
                .get()
                .addOnSuccessListener(document -> {
                    callback.onCallback(document.exists());
                });
    }

    public void getWaitlistCount(String eventId, EventCallback<Integer> callback) {
        eventsCollection
                .document(eventId)
                .collection("waitlist")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int count = queryDocumentSnapshots.size();
                    callback.onCallback(count);
                });
    }

    public void getParticipantsCount(String eventId, EventCallback<Integer> callback) {
        eventsCollection
                .document(eventId)
                .collection("participants")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int count = queryDocumentSnapshots.size();
                    callback.onCallback(count);
                });
    }

    public void getWaitlistEntrants(String eventId, EventCallback<List<Map<String, Object>>> callback) {
        eventsCollection
                .document(eventId)
                .collection("waitlist")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Map<String, Object>> entrants = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Map<String, Object> data = document.getData();
                        data.put("userId", document.getId());
                        entrants.add(data);
                    }
                    callback.onCallback(entrants);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting waitlist entrants", e);
                    callback.onCallback(null);
                });
    }
}

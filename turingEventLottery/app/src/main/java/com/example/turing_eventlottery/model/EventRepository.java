package com.example.turing_eventlottery.model;

import com.google.firebase.Timestamp;
import android.util.Log;
import android.util.Pair;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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

/**
 * Repository class for interacting with Event data within Firebase Firestore Database.
 * <p>
 *     This class handles CRUD operations for events; adding events, getting events,
 *     managing waitlists, and fetching registration periods.
 * </p>
 *
 * @author Matthew Adams
 * @author Miro
 * @version 1.1
 * @since 1.0
 * @see Event
 * @see User
 * @see EventCallback
 */
public class EventRepository {
    private static final String TAG = "EventRepository";
    private static final String COLLECTION_NAME = "events";

    private final CollectionReference eventsCollection;
    private final UserRepository userRepository;

    /**
     * Constructs a new EventRepository with a reference to the Firestore events collection
     */
    public EventRepository() {
        eventsCollection = FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
        userRepository = new UserRepository();
    }

    /**
     * Adds a new event to the database
     *
     * @param event the Event to add
     * @param callback callback returning true if successful, false otherwise
     */
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

    /**
     * Gets all events from the database.
     *
     * @param callback callback returning a list of events, or null if an error occurs
     */
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

    /**
     * Retrieves all events created by a specific organizer.
     *
     * @param organizerId the ID of the organizer
     * @param callback callback returning a list of events, or null if an error occurs
     */
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

    /**
     * Gets a single event by its ID.
     *
     * @param eventId the event ID
     * @param callback callback returning the Event, or null if not found or error occurs
     */
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
     * Deletes an event from the database.
     *
     * @param eventId the ID of the event to delete
     * @param callback callback returning true if successful, false otherwise
     */
    public void deleteEvent(String eventId, EventCallback<Boolean> callback) {
        eventsCollection.document(eventId).delete()
                .addOnSuccessListener(v -> callback.onCallback(true))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting event", e);
                    callback.onCallback(false);
                });
    }

    /**
     * Gets the registration period (start and end dates) for an event
     *
     * @param eventId the event ID
     * @param callback callback returning a {@link Pair} of {@link Date} objects for
     *                 start and end periods or {@code null} if an error occurs
     */
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

    /**
     * Adds a user to the waitlist for an event.
     *
     * @param eventId the event ID
     * @param user the user to add
     * @param callback callback returning {@code true} if successful, {@code false} otherwise
     */
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

    /**
     * Removes a user from the waitlist of an event.
     *
     * @param eventId the event ID
     * @param user the user to remove
     * @param callback callback returning {@code true} if the user is on the waitlist,
     *                 {@code false} otherwise
     */
    public void removeUserFromWaitlist(String eventId, User user, EventCallback<Boolean> callback) {
        eventsCollection.document(eventId).collection("waitlist")
                .document(user.getUserId())
                .delete()
                .addOnSuccessListener(v -> callback.onCallback(true))
                .addOnFailureListener(e -> callback.onCallback(false));
    }

    /**
     * Gets the IDs of all users currently on the waitlist for a specific event.
     * Only users whose waitlist status is "Waiting" will be returned.
     *
     * @param eventId The unique ID of the event whose waitlist is being retrieved
     * @param callback callback that returns a list of user IDs on the waitlist.
     *                 Returns null if the query fails
     */
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

    /**
     * Updates the waitlist status of a specific user for an event.
     *
     * @param eventId The unique ID of the event
     * @param userId The unique ID of the user whose status is being updated
     * @param status The new status to assign to the user
     * @param callback callback that returns true if the update was successful,
     *                 or false if the update failed
     */
    public void updateWaitlistStatus(String eventId, String userId, String status, EventCallback<Boolean> callback) {
        eventsCollection.document(eventId).collection("waitlist")
                .document(userId)
                .update("status", status)
                .addOnSuccessListener(v -> callback.onCallback(true))
                .addOnFailureListener(e -> callback.onCallback(false));
    }

    /**
     * Registers a user as a participant in an event.
     * The user is first removed from the event waitlist and then added
     * to the event's participants list.
     *
     * @param eventId The unique ID of the event
     * @param userId The unique ID of the user being registered
     * @param callback callback that returns true if the user was successfully
     *                 registered as a participant, or false if failed
     */
    public void registerParticipant(String eventId, String userId, EventCallback<Boolean> callback) {
        userRepository.getUser(userId, user -> {
            if (user != null) {
                // Remove from waitlist collection and add to participants array in event doc
                removeUserFromWaitlist(eventId, user, success -> {
                    if (success) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("username", user.getUserName());
                        data.put("timestamp", Timestamp.now());
                        data.put("status", "Enrolled");

                        eventsCollection.document(eventId)
                                .collection("participants")
                                .document(userId)
                                .set(data)
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

    /**
     * Checks if a user is on the event's waitlist.
     *
     * @param eventId the event ID
     * @param user the user to check
     * @param callback callback returning {@code true} if the user is on the waitlist,
     *                 {@code false} otherwise.
     */
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

    /**
     * Gets the number of users on the waitlist for an event.
     *
     * @param eventId the event ID
     * @param callback callback returning the waitlist count.
     */
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

    /**
     * Gets the number of participants for an event.
     *
     * @param eventId the event ID
     * @param callback callback returning the participants count
     */
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

    /**
     * Gets the waitlist entrants in descending order of timestamp
     *
     * @param eventId the event ID
     * @param callback callback returning a list of waitlist entries, or {@code null} if an error occurs
     */
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

    /**
     * Gets the participants in descending order of timestamp
     *
     * @param eventId the event ID
     * @param callback callback returning a list of participants, or null if an error occurs
     */
    public void getParticipants(String eventId, EventCallback<List<Map<String, Object>>> callback) {
        eventsCollection
                .document(eventId)
                .collection("participants")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Map<String, Object>> participants = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Map<String, Object> data = document.getData();
                        data.put("userId", document.getId());
                        participants.add(data);
                    }
                    callback.onCallback(participants);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting participants", e);
                    callback.onCallback(null);
                });
    }
}

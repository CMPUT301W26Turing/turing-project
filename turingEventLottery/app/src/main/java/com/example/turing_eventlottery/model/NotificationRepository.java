package com.example.turing_eventlottery.model;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for managing notifications in Firebase Firestore Database.
 * <p>
 *     Provides methods to add, remove, update, and query notifications
 *     for users and events. All notifications are stored in the "notifications" Firebase collection.
 * </p>
 *
 * @author Yuze
 * @version 1.0
 * @since 03-09-2026
 * @see Notification
 */
public class NotificationRepository {
    private static final String TAG = "NotificationRepository";
    private static final String COLLECTION_NAME = "notifications";

    private final CollectionReference notificationsCollection;

    /**
     * Initializes the repository with reference to the Firebase collection.
     */
    public NotificationRepository() {
        notificationsCollection = FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    /**
     * Adds a new notification to Firebase database.
     *
     * @param notification The notification object to add
     */
    public void addNotification(Notification notification) {
        notificationsCollection.add(notification)
                .addOnSuccessListener(documentReference -> {
                    notification.setId(documentReference.getId());
                    Log.d(TAG, "Notification added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding notification", e);
                });
    }

    /**
     * Removes a notification from Firebase database.
     *
     * @param notification The notification object to remove.
     *                     The notification's ID must be set
     */
    public void removeNotification(Notification notification) {
        notificationsCollection.document(notification.getId()).delete()
                .addOnSuccessListener(v -> {
                    Log.d(TAG, "Notification removed with ID: " + notification.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error removing notification", e);
                });
    }

    /**
     * Gets all notifications for a specific user, ordered by timestamp descending.
     *
     * @param userId The ID of the user whose notifications are retrieved
     * @param callback callback that returns the list of notifications, or null if the query fails
     */
    public void getNotificationsByUserId(String userId, EventCallback<List<Notification>> callback) {
        notificationsCollection.whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Notification> notifications = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Notification notification = document.toObject(Notification.class);
                            notification.setId(document.getId());
                            notifications.add(notification);
                        }
                        callback.onCallback(notifications);
                    } else {
                        Log.e(TAG, "Error getting notifications", task.getException());
                        callback.onCallback(null);
                    }
                });
    }

    /**
     * Updates the status of a specific notification.
     *
     * @param notificationId The ID of the notification to update
     * @param status The new status value
     * @param callback Callback that returns true if the update succeeded,
     *                 or false it if failed
     */
    public void updateNotificationStatus(String notificationId, String status, EventCallback<Boolean> callback) {
        notificationsCollection.document(notificationId)
                .update("status", status)
                .addOnSuccessListener(v -> callback.onCallback(true))
                .addOnFailureListener(e -> callback.onCallback(false));
    }

    /**
     * Retrieves all notifications in the Firebase collection, ordered by timestamp descending.
     * Primarily used to view system logs or all notifications for auditing purposes.
     *
     * @param callback Callback that returns the list of all notifications,
     *                 or null if teh query fails
     */
    public void getAllNotificationLogs(EventCallback<List<Notification>> callback) {
        notificationsCollection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Notification> logs = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Notification log = document.toObject(Notification.class);
                            log.setId(document.getId());
                            logs.add(log);
                        }
                        callback.onCallback(logs);
                    } else {
                        Log.e(TAG, "Error getting notification logs", task.getException());
                        callback.onCallback(null);
                    }
                });
    }

    /**
     * Retrieves all notification logs for a specific event, ordered by timestamp descending.
     *
     * @param eventId The ID of the event to filter notifications
     * @param callback Callback that returns the list of notifications for the event,
     *                 or null if the query fails
     */
    public void getNotificationLogsByEvent(String eventId, EventCallback<List<Notification>> callback) {
        notificationsCollection
                .whereEqualTo("eventId", eventId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Notification> logs = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Notification log = document.toObject(Notification.class);
                            log.setId(document.getId());
                            logs.add(log);
                        }
                        callback.onCallback(logs);
                    } else {
                        Log.e(TAG, "Error getting notification logs by event", task.getException());
                        callback.onCallback(null);
                    }
                });
    }
}

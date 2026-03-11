package com.example.turing_eventlottery.model;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationRepository {
    private static final String TAG = "NotificationRepository";
    private static final String COLLECTION_NAME = "notifications";

    private final CollectionReference notificationsCollection;

    public NotificationRepository() {
        notificationsCollection = FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

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

    public void removeNotification(Notification notification) {
        notificationsCollection.document(notification.getId()).delete()
                .addOnSuccessListener(v -> {
                    Log.d(TAG, "Notification removed with ID: " + notification.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error removing notification", e);
                });
    }

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

    public void updateNotificationStatus(String notificationId, String status, EventCallback<Boolean> callback) {
        notificationsCollection.document(notificationId)
                .update("status", status)
                .addOnSuccessListener(v -> callback.onCallback(true))
                .addOnFailureListener(e -> callback.onCallback(false));
    }
}

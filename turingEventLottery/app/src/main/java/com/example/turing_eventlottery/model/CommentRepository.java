package com.example.turing_eventlottery.model;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Repository class for managing comments.
 * Comments are stored redundantly under both users and events.
 *
 * @author Miro Straszynski
 * @version 1.0
 * @since 1.0
 */
public class CommentRepository {
    private static final String TAG = "CommentRepository";
    private final FirebaseFirestore db;

    public CommentRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Adds a comment to both the user's and the event's comment collections.
     *
     * @param userId the ID of the user
     * @param eventId the ID of the event
     * @param text the comment text
     * @param callback callback returning true if successful
     */
    public void addComment(String userId, String eventId, String text, EventCallback<Boolean> callback) {
        String commentId = UUID.randomUUID().toString();
        Comment comment = new Comment(commentId, userId, eventId, text, Timestamp.now());

        WriteBatch batch = db.batch();

        batch.set(db.collection("users").document(userId).collection("comments").document(commentId), comment);
        batch.set(db.collection("events").document(eventId).collection("comments").document(commentId), comment);

        batch.commit()
                .addOnSuccessListener(v -> callback.onCallback(true))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding comment", e);
                    callback.onCallback(false);
                });
    }

    /**
     * Deletes a single comment from both the user's and the event's collections.
     *
     * @param comment the comment to delete
     * @param callback callback returning true if successful
     */
    public void deleteComment(Comment comment, EventCallback<Boolean> callback) {
        WriteBatch batch = db.batch();

        batch.delete(db.collection("users").document(comment.getUserId()).collection("comments").document(comment.getCommentId()));
        batch.delete(db.collection("events").document(comment.getEventId()).collection("comments").document(comment.getCommentId()));

        batch.commit()
                .addOnSuccessListener(v -> callback.onCallback(true))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting comment", e);
                    callback.onCallback(false);
                });
    }

    /**
     * Gets all comments for a specific event.
     *
     * @param eventId the event ID
     * @param callback callback returning a list of comments
     */
    public void getCommentsByEvent(String eventId, EventCallback<List<Comment>> callback) {
        db.collection("events").document(eventId).collection("comments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Comment> comments = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        comments.add(document.toObject(Comment.class));
                    }
                    callback.onCallback(comments);
                })
                .addOnFailureListener(e -> callback.onCallback(null));
    }

    /**
     * Deletes all comments made by a user.
     *
     * @param userId the user ID
     * @param callback callback returning true when all deletions are complete
     */
    public void deleteAllUserComments(String userId, EventCallback<Boolean> callback) {
        CollectionReference userComments = db.collection("users").document(userId).collection("comments");

        userComments.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isEmpty()) {
                callback.onCallback(true);
                return;
            }

            int total = queryDocumentSnapshots.size();
            AtomicInteger count = new AtomicInteger(0);

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Comment comment = document.toObject(Comment.class);
                deleteComment(comment, success -> {
                    if (count.incrementAndGet() == total) {
                        callback.onCallback(true);
                    }
                });
            }
        }).addOnFailureListener(e -> callback.onCallback(false));
    }

    /**
     * Deletes all comments associated with an event.
     *
     * @param eventId the event ID
     * @param callback callback returning true when all deletions are complete
     */
    public void deleteAllEventComments(String eventId, EventCallback<Boolean> callback) {
        CollectionReference eventComments = db.collection("events").document(eventId).collection("comments");

        eventComments.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isEmpty()) {
                callback.onCallback(true);
                return;
            }

            int total = queryDocumentSnapshots.size();
            AtomicInteger count = new AtomicInteger(0);

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Comment comment = document.toObject(Comment.class);
                deleteComment(comment, success -> {
                    if (count.incrementAndGet() == total) {
                        callback.onCallback(true);
                    }
                });
            }
        }).addOnFailureListener(e -> callback.onCallback(false));
    }
}

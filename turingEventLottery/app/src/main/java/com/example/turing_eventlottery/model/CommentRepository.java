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
 * @version 1.1
 * @since 1.0
 */
public class CommentRepository {
    private static final String TAG = "CommentRepository";
    private final FirebaseFirestore db;

    public ModelCallback<Boolean> getCallback() {
        return callback;
    }

    public void setCallback(ModelCallback<Boolean> callback) {
        this.callback = callback;
    }

    private ModelCallback<Boolean> callback;

    public CommentRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Adds a comment to both the user's and the event's comment collections.
     *
     * @param userId the ID of the user
     * @param userName the name of the user
     * @param eventId the ID of the event
     * @param text the comment text
     * @param callback callback returning true if successful
     */
    public void addComment(String userId, String userName, String eventId, String text, ModelCallback<Boolean> callback) {
        addComment(userId, userName, eventId, text, null, callback);
    }

    /**
     * Adds a comment with a parent ID for threading.
     *
     * @param userId the ID of the user
     * @param userName the name of the user
     * @param eventId the ID of the event
     * @param text the comment text
     * @param parentId the ID of the parent comment
     * @param callback callback returning true if successful
     */
    public void addComment(String userId, String userName, String eventId, String text, String parentId, ModelCallback<Boolean> callback) {
        String commentId = UUID.randomUUID().toString();
        Comment comment = new Comment(commentId, userId, userName, eventId, text, parentId, Timestamp.now());

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
    public void deleteComment(Comment comment, ModelCallback<Boolean> callback) {
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
    public void getCommentsByEvent(String eventId, ModelCallback<List<Comment>> callback) {
        db.collection("events").document(eventId).collection("comments")
                .orderBy("timestamp", Query.Direction.ASCENDING)
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
    public void deleteAllUserComments(String userId, ModelCallback<Boolean> callback) {
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
    public void deleteAllEventComments(String eventId, ModelCallback<Boolean> callback) {
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

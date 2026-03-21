package com.example.turing_eventlottery.model;

import com.google.firebase.Timestamp;

/**
 * Represents a comment made by a user on an event.
 * Comments are stored redundantly in both the user's and the event's sub-collections
 * to facilitate efficient querying and cascading deletes.
 *
 * @author Miro Straszynski
 * @version 1.0
 * @since 1.0
 */
public class Comment {
    private String commentId;
    private String userId;
    private String eventId;
    private String text;
    private Timestamp timestamp;

    /**
     * Default constructor for Firebase Firestore.
     */
    public Comment() {}

    /**
     * Constructs a new Comment.
     *
     * @param commentId the unique identifier for the comment
     * @param userId the ID of the user who made the comment
     * @param eventId the ID of the event the comment is for
     * @param text the content of the comment
     * @param timestamp the time the comment was created
     */
    public Comment(String commentId, String userId, String eventId, String text, Timestamp timestamp) {
        this.commentId = commentId;
        this.userId = userId;
        this.eventId = eventId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}

package com.example.turing_eventlottery.model;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a comment made by a user on an event.
 * Comments are stored redundantly in both the user's and the event's sub-collections
 * to facilitate efficient querying and cascading deletes.
 *
 * @author Miro Straszynski
 * @version 1.2
 * @since 1.0
 */
public class Comment {
    private String commentId;
    private String userId;
    private String userName;
    private String eventId;
    private String text;
    private String parentId;
    private int likes;
    private int dislikes;
    private List<String> likedBy;
    private List<String> dislikedBy;
    private Timestamp timestamp;

    /**
     * Default constructor for Firebase Firestore.
     */
    public Comment() {
        this.likedBy = new ArrayList<>();
        this.dislikedBy = new ArrayList<>();
    }

    /**
     * Constructs a new Comment.
     *
     * @param commentId the unique identifier for the comment
     * @param userId the ID of the user who made the comment
     * @param userName the name of the user who made the comment
     * @param eventId the ID of the event the comment is for
     * @param text the content of the comment
     * @param timestamp the time the comment was created
     */
    public Comment(String commentId, String userId, String userName, String eventId, String text, Timestamp timestamp) {
        this(commentId, userId, userName, eventId, text, null, timestamp);
    }

    /**
     * Constructs a new Comment with a parent ID for threading.
     */
    public Comment(String commentId, String userId, String userName, String eventId, String text, String parentId, Timestamp timestamp) {
        this.commentId = commentId;
        this.userId = userId;
        this.userName = userName;
        this.eventId = eventId;
        this.text = text;
        this.parentId = parentId;
        this.likes = 0;
        this.dislikes = 0;
        this.likedBy = new ArrayList<>();
        this.dislikedBy = new ArrayList<>();
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public int getDislikes() { return dislikes; }
    public void setDislikes(int dislikes) { this.dislikes = dislikes; }

    public List<String> getLikedBy() {
        if (likedBy == null) likedBy = new ArrayList<>();
        return likedBy;
    }

    public void setLikedBy(List<String> likedBy) {
        this.likedBy = likedBy;
    }

    public List<String> getDislikedBy() {
        if (dislikedBy == null) dislikedBy = new ArrayList<>();
        return dislikedBy;
    }

    public void setDislikedBy(List<String> dislikedBy) {
        this.dislikedBy = dislikedBy;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}

package com.example.turing_eventlottery.model;

import com.google.firebase.firestore.Exclude;

public class Notification {
    private String id;
    private String userId;
    private String eventId;
    private String eventName;
    private String eventDate;
    private String message;
    private String status;
    private long timestamp;

    public Notification() {}

    public Notification(String userId, String eventId, String eventName, String eventDate, String message, String status) {
        this.userId = userId;
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.message = message;
        this.status = status;
        this.timestamp = System.currentTimeMillis();
    }

    public Notification(String userId, String eventId, String eventName, String eventDate, String status) {
        this.userId = userId;
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.status = status;
        this.timestamp = System.currentTimeMillis();
    }

    @Exclude
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getEventDate() { return eventDate; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

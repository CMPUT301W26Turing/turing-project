package com.example.turing_eventlottery.model;

import com.google.firebase.firestore.Exclude;
import java.util.List;

public class Notification {
    private String id;
    private String userId;
    private String eventId;
    private String eventName;
    private String eventDate;
    private String message;
    private String status;
    private long timestamp;
    private String organizerId;
    private List<String> recipients;
    private boolean isSystemLog;

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

    /**
     * Constructor for creating system notification logs (admin audit trail)
     * Used when organizers send notifications to entrants
     */
    public Notification(String organizerId, String eventId, String eventName, String eventDate,
                       String message, List<String> recipients) {
        this.organizerId = organizerId;
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.message = message;
        this.recipients = recipients;
        this.isSystemLog = true;
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

    public String getOrganizerId() { return organizerId; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }

    public List<String> getRecipients() { return recipients; }
    public void setRecipients(List<String> recipients) { this.recipients = recipients; }

    public boolean isSystemLog() { return isSystemLog; }
    public void setSystemLog(boolean systemLog) { isSystemLog = systemLog; }
}

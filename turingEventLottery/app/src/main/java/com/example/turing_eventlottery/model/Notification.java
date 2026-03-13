package com.example.turing_eventlottery.model;

import com.google.firebase.firestore.Exclude;
import java.util.List;

/**
 * This class represents a notification related to an event in the application.
 * <p>
 *     Notifications can be either user targeted (participants/waitlist)
 *     or system logs created by organizers for audit purposes.
 * </p>
 *
 * @author Yuze
 * @version 1.0
 * @since 03-09-2026
 * @see NotificationRepository
 */
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

    /**
     * Default constructor required for Firebase Firestone Database.
     */
    public Notification() {}

    /**
     * Constructor that creates a user targeted notification with a message.
     *
     * @param userId The ID of the recipient user
     * @param eventId The ID of the related event
     * @param eventName The name of the related event
     * @param eventDate The date of the event
     * @param message The message content of the notification
     * @param status The status of the notification
     */
    public Notification(String userId, String eventId, String eventName, String eventDate, String message, String status) {
        this.userId = userId;
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.message = message;
        this.status = status;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     *  Constructor that creates a user targeted notification without a message.
     *
     * @param userId The ID of the recipient user
     * @param eventId The ID of the related event
     * @param eventName The name of the event
     * @param eventDate The date of the event
     * @param status The status of the notification
     */
    public Notification(String userId, String eventId, String eventName, String eventDate, String status) {
        this.userId = userId;
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.status = status;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Constructor that creates a system notification log for organizers sending
     * notifications to multiple recipients.
     *
     * @param organizerId The ID of the organizer sending the notification
     * @param eventId The ID of the related event
     * @param eventName The name of the event
     * @param eventDate The date of the event
     * @param message The notification message content
     * @param recipients the list of recipient user IDs
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

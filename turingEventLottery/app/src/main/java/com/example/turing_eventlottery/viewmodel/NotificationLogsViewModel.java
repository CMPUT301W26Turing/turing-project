package com.example.turing_eventlottery.viewmodel;

import android.content.Context;
import android.util.Log;

import com.example.turing_eventlottery.model.EventCallback;
import com.example.turing_eventlottery.model.Notification;
import com.example.turing_eventlottery.model.NotificationRepository;

import java.util.List;

/**
 * ViewModel for managing notification logs.
 * Provides methods for retrieving and managing notification log entries.
 * Used by admin dashboard to audit all notifications sent by organizers.
 */
public class NotificationLogsViewModel {
    private static final String TAG = "NotificationLogsViewModel";
    private Context context;
    private NotificationRepository notificationRepository;

    public NotificationLogsViewModel(Context context) {
        this.context = context;
        this.notificationRepository = new NotificationRepository();
    }

    /**
     * Load all notification logs from the system
     * @param callback callback with list of logs or null on error
     */
    public void loadNotificationLogs(EventCallback<List<Notification>> callback) {
        Log.d(TAG, "Loading all notification logs");
        notificationRepository.getAllNotificationLogs(logs -> {
            if (logs != null) {
                Log.d(TAG, "Successfully loaded " + logs.size() + " notification logs");
                callback.onCallback(logs);
            } else {
                Log.e(TAG, "Failed to load notification logs");
                callback.onCallback(null);
            }
        });
    }

    /**
     * Load notification logs for a specific event
     * @param eventId the event ID to filter by
     * @param callback callback with list of logs for that event or null on error
     */
    public void loadNotificationLogsByEvent(String eventId, EventCallback<List<Notification>> callback) {
        Log.d(TAG, "Loading notification logs for event: " + eventId);
        notificationRepository.getNotificationLogsByEvent(eventId, logs -> {
            if (logs != null) {
                Log.d(TAG, "Successfully loaded " + logs.size() + " logs for event " + eventId);
                callback.onCallback(logs);
            } else {
                Log.e(TAG, "Failed to load logs for event " + eventId);
                callback.onCallback(null);
            }
        });
    }
}

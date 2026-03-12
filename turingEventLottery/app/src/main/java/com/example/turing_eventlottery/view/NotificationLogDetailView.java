package com.example.turing_eventlottery.view;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;

import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.Notification;
import com.example.turing_eventlottery.viewmodel.NotificationLogsViewModel;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Activity for viewing detailed information about a specific notification log
 * Shows organizer info, event details, recipients, timestamp, and message content
 */
public class NotificationLogDetailView extends AppCompatActivity {
    private NotificationLogsViewModel viewModel;
    private String notificationId;

    // UI Elements
    private MaterialButton backButton;
    private TextView organizerNameDetail;
    private TextView organizerIdDetail;
    private TextView eventNameDetail;
    private TextView eventDateDetail;
    private TextView timestampDetail;
    private TextView recipientsCountText;
    private LinearLayout recipientsListContainer;
    private TextView messageContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.notification_log_detail);

        viewModel = new NotificationLogsViewModel(this);
        initializeViews();

        notificationId = getIntent().getStringExtra("NOTIFICATION_ID");
        backButton.setOnClickListener(v -> finish());

        if (notificationId != null) {
            loadNotificationLog();
        }
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        organizerNameDetail = findViewById(R.id.logOrganizerNameDetail);
        organizerIdDetail = findViewById(R.id.logOrganizerIdDetail);
        eventNameDetail = findViewById(R.id.logEventNameDetail);
        eventDateDetail = findViewById(R.id.logEventDateDetail);
        timestampDetail = findViewById(R.id.logTimestampDetail);
        recipientsCountText = findViewById(R.id.logRecipientsCount);
        recipientsListContainer = findViewById(R.id.recipientsListContainer);
        messageContent = findViewById(R.id.logMessageContent);
    }

    private void loadNotificationLog() {
        viewModel.loadNotificationLogs(logs -> {
            if (logs != null) {
                for (Notification log : logs) {
                    if (notificationId.equals(log.getId())) {
                        displayNotificationLog(log);
                        return;
                    }
                }
            }
        });
    }

    private void displayNotificationLog(Notification log) {
        // Organizer info
        String displayName = log.getOrganizerId() != null ? log.getOrganizerId() :
                (log.getUserId() != null ? log.getUserId() : "Unknown");
        String displayId = log.getOrganizerId() != null ? log.getOrganizerId() :
                (log.getUserId() != null ? log.getUserId() : "N/A");
        organizerNameDetail.setText(displayName);
        organizerIdDetail.setText(displayId);

        // Event info
        eventNameDetail.setText(log.getEventName() != null ? log.getEventName() : "Unknown Event");
        eventDateDetail.setText(log.getEventDate() != null ? log.getEventDate() : "N/A");

        // Timestamp - show both relative and absolute time
        long now = System.currentTimeMillis();
        CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(
                log.getTimestamp(), now, DateUtils.MINUTE_IN_MILLIS);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault());
        String absoluteTime = sdf.format(new Date(log.getTimestamp()));
        timestampDetail.setText(absoluteTime + " (" + relativeTime + ")");

        // Recipients
        List<String> recipients = log.getRecipients();
        int recipientCount = recipients != null ? recipients.size() : 0;
        String recipientText = recipientCount + " recipient" + (recipientCount != 1 ? "s" : "");
        recipientsCountText.setText(recipientText);

        // Display individual recipients
        recipientsListContainer.removeAllViews();
        if (recipients != null && !recipients.isEmpty()) {
            for (String recipientId : recipients) {
                TextView recipientView = new TextView(this);
                recipientView.setText("• " + recipientId);
                recipientView.setTextSize(13);
                recipientView.setTextColor(getResources().getColor(R.color.headerText));
                recipientView.setPadding(0, 4, 0, 4);
                recipientsListContainer.addView(recipientView);
            }
        }

        // Message content
        messageContent.setText(log.getMessage() != null ? log.getMessage() : "No message content");
    }
}

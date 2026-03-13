package com.example.turing_eventlottery.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;

import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.Notification;
import com.example.turing_eventlottery.viewmodel.NotificationLogsViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

/**
 * Activity for browsing all notification logs (admin only)
 * Displays a list of all notifications sent by organizers to entrants
 * <p>
 *     Displays a list of notifications sent by organizers to entrants.
 *     Supports empty state handling. Each notification card navigates
 *     to a detail view when clicked.
 * </p>
 */
public class BrowseNotificationLogsView extends AppCompatActivity {
    private LinearLayout logsContainer;
    private LinearLayout emptyStateLayout;
    private TextView logsCountText;
    private NotificationLogsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.browse_notification_logs);

        logsContainer = findViewById(R.id.logsContainer);
        logsCountText = findViewById(R.id.logsCountText);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        viewModel = new NotificationLogsViewModel(this);

        // Back button
        MaterialButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotificationLogs();
    }

    private void loadNotificationLogs() {
        viewModel.loadNotificationLogs(logs -> {
            if (logs != null && !logs.isEmpty()) {
                displayNotificationLogs(logs);
            } else {
                showEmptyState();
            }
        });
    }

    private void displayNotificationLogs(List<Notification> logs) {
        logsContainer.removeAllViews();
        emptyStateLayout.setVisibility(View.GONE);
        logsCountText.setText(logs.size() + " Logs");

        for (Notification log : logs) {
            MaterialCardView cardView = (MaterialCardView) LayoutInflater.from(this)
                    .inflate(R.layout.browse_notification_logs_card, logsContainer, false);

            TextView organizerName = cardView.findViewById(R.id.logOrganizerName);
            TextView eventName = cardView.findViewById(R.id.logEventName);
            TextView timestamp = cardView.findViewById(R.id.logTimestamp);
            TextView messagePreview = cardView.findViewById(R.id.logMessagePreview);

            // Display organizer info - use organizerId if available, otherwise use userId
            String displayName = log.getOrganizerId() != null ? log.getOrganizerId() :
                    (log.getUserId() != null ? log.getUserId() : "Unknown");
            organizerName.setText(displayName);
            eventName.setText(log.getEventName() != null ? log.getEventName() : "Unknown Event");

            // Message preview
            if (log.getMessage() != null && !log.getMessage().isEmpty()) {
                messagePreview.setText(log.getMessage());
                messagePreview.setVisibility(View.VISIBLE);
            } else {
                messagePreview.setVisibility(View.GONE);
            }

            // Format timestamp
            long now = System.currentTimeMillis();
            CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(
                    log.getTimestamp(), now, DateUtils.MINUTE_IN_MILLIS);
            timestamp.setText(relativeTime);

            // Click listener to navigate to detail view
            cardView.setOnClickListener(v -> {
                Intent intent = new Intent(BrowseNotificationLogsView.this, NotificationLogDetailView.class);
                intent.putExtra("NOTIFICATION_ID", log.getId());
                startActivity(intent);
            });

            logsContainer.addView(cardView);
        }
    }

    private void showEmptyState() {
        logsContainer.removeAllViews();
        logsCountText.setText("0 Logs");
        emptyStateLayout.setVisibility(View.VISIBLE);
    }
}

package com.example.turing_eventlottery.view;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.Notification;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notifications;
    private OnNotificationActionListener listener;

    public interface OnNotificationActionListener {
        void onAccept(Notification notification);
        void onDecline(Notification notification);
    }

    public NotificationAdapter(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public void setOnNotificationActionListener(OnNotificationActionListener listener) {
        this.listener = listener;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        Context context = holder.itemView.getContext();

        holder.eventName.setText(notification.getEventName());
        
        String message = notification.getMessage();
        if (message != null && !message.trim().isEmpty()) {
            holder.messageText.setText(message);
            holder.messageText.setVisibility(View.VISIBLE);
        } else {
            holder.messageText.setVisibility(View.GONE);
        }

        long now = System.currentTimeMillis();
        CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(notification.getTimestamp(), now, DateUtils.MINUTE_IN_MILLIS);
        holder.timeText.setText(relativeTime);

        String status = notification.getStatus();
        holder.statusBadge.setText(status != null ? status.toUpperCase() : "UPDATE");

        if ("Invited".equalsIgnoreCase(status)) {
            holder.statusIndicator.setVisibility(View.VISIBLE);
            holder.actionButtonsLayout.setVisibility(View.VISIBLE);
            holder.subtitle.setText("Lottery Results");
            
            int invitedColor = ContextCompat.getColor(context, R.color.greenIconTint);
            int invitedBg = ContextCompat.getColor(context, R.color.greenIconBackground);
            
            holder.statusBadge.setTextColor(invitedColor);
            holder.iconContainer.setCardBackgroundColor(invitedBg);
            holder.notificationIcon.setImageResource(R.drawable.baseline_check_circle_24);
            holder.notificationIcon.setColorFilter(invitedColor);

            holder.acceptButton.setOnClickListener(v -> {
                if (listener != null) listener.onAccept(notification);
            });
            holder.declineButton.setOnClickListener(v -> {
                if (listener != null) listener.onDecline(notification);
            });
            
        } else if ("Waiting".equalsIgnoreCase(status)) {
            holder.statusIndicator.setVisibility(View.GONE);
            holder.actionButtonsLayout.setVisibility(View.GONE);
            holder.subtitle.setText("Waitlist Update");
            
            int waitingColor = ContextCompat.getColor(context, R.color.orangeIconTint);
            int waitingBg = ContextCompat.getColor(context, R.color.orangeIconBackground);
            
            holder.statusBadge.setTextColor(waitingColor);
            holder.iconContainer.setCardBackgroundColor(waitingBg);
            holder.notificationIcon.setImageResource(R.drawable.outline_hourglass_top_24);
            holder.notificationIcon.setColorFilter(waitingColor);

        } else if ("Cancelled".equalsIgnoreCase(status) || "Declined".equalsIgnoreCase(status) || "Not Selected".equalsIgnoreCase(status)) {
            holder.statusIndicator.setVisibility(View.GONE);
            holder.actionButtonsLayout.setVisibility(View.GONE);
            holder.subtitle.setText("Status Update");
            
            int neutralColor = ContextCompat.getColor(context, R.color.headerSubtext);
            
            holder.statusBadge.setTextColor(neutralColor);
            holder.iconContainer.setCardBackgroundColor(context.getResources().getColor(R.color.secondaryBackground));
            holder.notificationIcon.setImageResource(R.drawable.baseline_cancel_24);
            holder.notificationIcon.setColorFilter(neutralColor);
            
        } else {
            holder.statusIndicator.setVisibility(View.GONE);
            holder.actionButtonsLayout.setVisibility(View.GONE);
            holder.subtitle.setText("Event Update");
            
            holder.statusBadge.setTextColor(ContextCompat.getColor(context, R.color.primaryBlue));
            holder.iconContainer.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primaryBackground));
            holder.notificationIcon.setImageResource(R.drawable.baseline_notifications_24);
            holder.notificationIcon.setColorFilter(ContextCompat.getColor(context, R.color.primaryBlue));
        }
    }

    @Override
    public int getItemCount() {
        return notifications != null ? notifications.size() : 0;
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, subtitle, statusBadge, timeText, messageText;
        View actionButtonsLayout, statusIndicator;
        MaterialButton acceptButton, declineButton;
        ImageView notificationIcon;
        MaterialCardView iconContainer;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.notificationEventName);
            subtitle = itemView.findViewById(R.id.notificationSubtitle);
            statusBadge = itemView.findViewById(R.id.notificationStatusBadge);
            timeText = itemView.findViewById(R.id.timeText);
            messageText = itemView.findViewById(R.id.notificationMessage);
            actionButtonsLayout = itemView.findViewById(R.id.actionButtonsLayout);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            declineButton = itemView.findViewById(R.id.declineButton);
            notificationIcon = itemView.findViewById(R.id.notificationIcon);
            iconContainer = itemView.findViewById(R.id.iconContainer);
        }
    }
}

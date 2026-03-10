package com.example.turing_eventlottery.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.Notification;
import com.google.android.material.button.MaterialButton;

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
        holder.eventName.setText(notification.getEventName());
        holder.eventDateTime.setText(notification.getEventDate());
        holder.status.setText(notification.getStatus());

        if ("Invited".equals(notification.getStatus())) {
            holder.actionButtonsLayout.setVisibility(View.VISIBLE);
            holder.acceptButton.setOnClickListener(v -> {
                if (listener != null) listener.onAccept(notification);
            });
            holder.declineButton.setOnClickListener(v -> {
                if (listener != null) listener.onDecline(notification);
            });
        } else {
            holder.actionButtonsLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return notifications != null ? notifications.size() : 0;
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDateTime, status;
        View actionButtonsLayout;
        MaterialButton acceptButton, declineButton;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.notificationEventName);
            eventDateTime = itemView.findViewById(R.id.notificationEventDateTime);
            status = itemView.findViewById(R.id.notificationStatus);
            actionButtonsLayout = itemView.findViewById(R.id.actionButtonsLayout);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            declineButton = itemView.findViewById(R.id.declineButton);
        }
    }
}

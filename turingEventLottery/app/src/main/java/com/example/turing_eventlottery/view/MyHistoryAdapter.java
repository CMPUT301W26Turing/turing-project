package com.example.turing_eventlottery.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.Event;
import com.example.turing_eventlottery.model.EventRepository;
import com.google.android.material.card.MaterialCardView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter for the My History screen, displaying events the user has interacted with.
 */
public class MyHistoryAdapter extends RecyclerView.Adapter<MyHistoryAdapter.HistoryViewHolder> {

    private List<Event> eventList;
    private final String userId;
    private final EventRepository eventRepository;
    private final Map<String, String> statusCache = new HashMap<>();

    public MyHistoryAdapter(List<Event> eventList, String userId) {
        this.eventList = eventList;
        this.userId = userId;
        this.eventRepository = new EventRepository();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_event, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventName.setText(event.getName());
        holder.eventDateTime.setText(event.getDate());

        if (statusCache.containsKey(event.getId())) {
            displayStatus(holder, statusCache.get(event.getId()));
        } else {
            eventRepository.getUserEventStatus(event.getId(), userId, status -> {
                statusCache.put(event.getId(), status);
                displayStatus(holder, status);
            });
        }
    }

    private void displayStatus(HistoryViewHolder holder, String status) {
        String displayStatus;
        int backgroundColor;
        int textColor;

        switch (status) {
            case "Invited":
                displayStatus = "Selected";
                backgroundColor = 0xFFE3F2FD;
                textColor = 0xFF1976D2;
                break;
            case "Enrolled":
                displayStatus = "Accepted";
                backgroundColor = 0xFFE8F5E9;
                textColor = 0xFF2E7D32;
                break;
            case "Cancelled":
                displayStatus = "Declined";
                backgroundColor = 0xFFFFEBEE;
                textColor = 0xFFC62828;
                break;
            case "Not Selected":
                displayStatus = "Not Selected";
                backgroundColor = 0xFFF5F5F5;
                textColor = 0xFF616161;
                break;
            case "Waiting":
            default:
                displayStatus = "Waitlist";
                backgroundColor = 0xFFFFF3E0;
                textColor = 0xFFE65100;
                break;
        }

        holder.statusText.setText(displayStatus);
        holder.statusBadge.setCardBackgroundColor(backgroundColor);
        holder.statusText.setTextColor(textColor);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void updateEvents(List<Event> newEvents) {
        this.eventList = newEvents;
        notifyDataSetChanged();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDateTime, statusText;
        MaterialCardView statusBadge;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.eventName);
            eventDateTime = itemView.findViewById(R.id.eventDateTime);
            statusText = itemView.findViewById(R.id.statusText);
            statusBadge = itemView.findViewById(R.id.statusBadge);
        }
    }
}

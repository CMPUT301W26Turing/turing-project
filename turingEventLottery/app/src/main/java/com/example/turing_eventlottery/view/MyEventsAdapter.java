package com.example.turing_eventlottery.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.Event;
import com.example.turing_eventlottery.model.EventRepository;

import java.util.List;

/**
 * RecyclerView Adapter for displaying a list of events that the user manages.
 * <p>
 *     Each list item shows event name, event date, waitlist count / capacity, registration status.
 * </p>
 * Clicking an event opens the ManageEventView for that event.
 */
// Need to fix MVVM architecture for part 4
public class MyEventsAdapter extends RecyclerView.Adapter<MyEventsAdapter.EventViewHolder> {

    private List<Event> eventList;
    private final EventRepository eventRepository;

    public MyEventsAdapter(List<Event> eventList) {
        this.eventList = eventList;
        this.eventRepository = new EventRepository();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventName.setText(event.getName());
        holder.eventDateTime.setText(event.getDate());

        eventRepository.getWaitlistCount(event.getId(), waitlistSize -> {
            int waitlistCap = event.getWaitlistCap();
            if (waitlistCap > 0 && waitlistSize >= waitlistCap) {
                holder.eventStats.setText("Full");
                holder.statusText.setText("Registration Closed");
            } else {
                holder.eventStats.setText(waitlistSize + "/" + waitlistCap + " Applied");
                holder.statusText.setText("Registration Open");
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            if (event.getId() == null) {
                Toast.makeText(context, "Event ID is null, cannot open dashboard.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(context, ManageEventView.class);
            intent.putExtra("EVENT_ID", event.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void updateEvents(List<Event> newEvents) {
        this.eventList = newEvents;
        notifyDataSetChanged();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDateTime, eventStats, statusText;
        LinearLayout actionButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.eventName);
            eventDateTime = itemView.findViewById(R.id.eventDateTime);
            eventStats = itemView.findViewById(R.id.eventStats);
            statusText = itemView.findViewById(R.id.statusText);
            actionButton = itemView.findViewById(R.id.actionButton);
        }
    }
}
package com.example.turing_eventlottery.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.Event;

import java.util.List;

public class MyEventsAdapter extends RecyclerView.Adapter<MyEventsAdapter.EventViewHolder> {

    private List<Event> eventList;

    public MyEventsAdapter(List<Event> eventList) {
        this.eventList = eventList;
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
        
        int waitlistSize = (event.getWaitlist() != null) ? event.getWaitlist().size() : 0;
        int waitlistCap = event.getWaitlistCap();

        if (waitlistCap > 0 && waitlistSize >= waitlistCap) {
            holder.eventStats.setText("Full");
            holder.statusText.setText("Registration Closed");
        } else {
            holder.eventStats.setText(waitlistSize + "/" + waitlistCap + " Applied");
            holder.statusText.setText("Registration Open");
        }

        holder.actionButton.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, ManageEventView.class);
            intent.putExtra("EVENT_ID", event.getId());
            context.startActivity(intent);
        });

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, ManageEventView.class);
            if (event.getId() == null)
                Toast.makeText(context, "Event ID is null", Toast.LENGTH_SHORT).show();
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

package com.example.turing_eventlottery.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        holder.eventDateTime.setText(event.getDate() + " • " + event.getTime());
        
        // Mocking some data for stats and status as they might not be in the model yet
        holder.eventStats.setText("0/" + event.getWaitlistCap() + " Applied");
        holder.statusText.setText("Registration Open");
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

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.eventName);
            eventDateTime = itemView.findViewById(R.id.eventDateTime);
            eventStats = itemView.findViewById(R.id.eventStats);
            statusText = itemView.findViewById(R.id.statusText);
        }
    }
}

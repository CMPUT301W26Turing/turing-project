package com.example.turing_eventlottery.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.turing_eventlottery.R;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WaitingEntrantsAdapter extends RecyclerView.Adapter<WaitingEntrantsAdapter.EntrantViewHolder> {

    private List<Map<String, Object>> entrants;

    public WaitingEntrantsAdapter(List<Map<String, Object>> entrants) {
        this.entrants = entrants;
    }

    @NonNull
    @Override
    public EntrantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_waiting_entrant, parent, false);
        return new EntrantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntrantViewHolder holder, int position) {
        Map<String, Object> entrant = entrants.get(position);
        String username = (String) entrant.get("username");
        if (username == null) username = "Unknown User";
        
        holder.entrantName.setText(username);

        String initials = "";
        String[] parts = username.split(" ");
        if (parts.length > 0 && !parts[0].isEmpty()) {
            initials += parts[0].substring(0, 1).toUpperCase();
            if (parts.length > 1 && !parts[1].isEmpty()) {
                initials += parts[1].substring(0, 1).toUpperCase();
            }
        }
        holder.avatarText.setText(initials);

        Object timestampObj = entrant.get("timestamp");
        if (timestampObj instanceof Timestamp) {
            Timestamp timestamp = (Timestamp) timestampObj;
            Date date = timestamp.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd • HH:mm", Locale.getDefault());
            holder.entrantDetails.setText("Applied " + sdf.format(date));
        } else {
            holder.entrantDetails.setText("Waiting");
        }

        holder.statusText.setText("Pending");
    }

    @Override
    public int getItemCount() {
        return entrants.size();
    }

    public void updateEntrants(List<Map<String, Object>> newEntrants) {
        this.entrants = newEntrants;
        notifyDataSetChanged();
    }

    static class EntrantViewHolder extends RecyclerView.ViewHolder {
        TextView entrantName, entrantDetails, statusText, avatarText;

        public EntrantViewHolder(@NonNull View itemView) {
            super(itemView);
            entrantName = itemView.findViewById(R.id.entrantName);
            entrantDetails = itemView.findViewById(R.id.entrantDetails);
            statusText = itemView.findViewById(R.id.statusText);
            avatarText = itemView.findViewById(R.id.avatarText);
        }
    }
}

package com.example.turing_eventlottery.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
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

/**
 * Adapter for displaying a list of entrants who are waiting for event.
 */
// View handles business logic, will fix for part 4 to follow proper MVVM architecture
public class WaitingEntrantsAdapter extends RecyclerView.Adapter<WaitingEntrantsAdapter.EntrantViewHolder> {

    private List<Map<String, Object>> entrants;
    private OnEntrantActionListener actionListener;

    public interface OnEntrantActionListener {
        void onCancelInvitation(String userId);
    }

    public WaitingEntrantsAdapter(List<Map<String, Object>> entrants) {
        this.entrants = entrants;
    }

    public void setOnEntrantActionListener(OnEntrantActionListener listener) {
        this.actionListener = listener;
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
        String userId = (String) entrant.get("userId");
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

        String status = (String) entrant.get("status");
        if (status == null) status = "Waiting";
        holder.statusText.setText(status);

        holder.moreButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.getMenu().add("Cancel Invitation");
            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Cancel Invitation")) {
                    if (actionListener != null && userId != null) {
                        actionListener.onCancelInvitation(userId);
                    }
                    return true;
                }
                return false;
            });
            popup.show();
        });
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
        ImageView moreButton;

        public EntrantViewHolder(@NonNull View itemView) {
            super(itemView);
            entrantName = itemView.findViewById(R.id.entrantName);
            entrantDetails = itemView.findViewById(R.id.entrantDetails);
            statusText = itemView.findViewById(R.id.statusText);
            avatarText = itemView.findViewById(R.id.avatarText);
            moreButton = itemView.findViewById(R.id.moreButton);
        }
    }
}

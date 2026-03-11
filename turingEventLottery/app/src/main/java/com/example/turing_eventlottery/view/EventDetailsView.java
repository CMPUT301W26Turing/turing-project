package com.example.turing_eventlottery.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.Event;
import com.example.turing_eventlottery.model.User;
import com.example.turing_eventlottery.model.UserCallback;
import com.example.turing_eventlottery.viewmodel.EventViewModel;
import com.example.turing_eventlottery.viewmodel.UserViewModel;

public class EventDetailsView extends AppCompatActivity {
    private EventViewModel eventViewModel;
    private UserViewModel userViewModel;

    private ImageView posterView;
    private TextView nameView;
    private TextView locationView;
    private TextView dateTimeView;
    private TextView descriptionView;
    private Button deleteEventButton;

    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.event_details);

        posterView = findViewById(R.id.eventPoster);
        nameView = findViewById(R.id.eventName);
        locationView = findViewById(R.id.eventLocation);
        dateTimeView = findViewById(R.id.eventDateTime);
        descriptionView = findViewById(R.id.eventDescription);
        deleteEventButton = findViewById(R.id.deleteEventButton);

        eventViewModel = new EventViewModel();
        userViewModel = new UserViewModel(this);

        eventId = getIntent().getStringExtra("EVENT_ID");

        if (eventId != null) {
            eventViewModel.getEventById(eventId, this::displayEvent);
        }

        checkAdminStatus();
    }

    private void checkAdminStatus() {
        userViewModel.loadUser(new UserCallback() {
            @Override
            public void onSuccess(User user) {
                if (user != null && user.isAdmin()) {
                    deleteEventButton.setVisibility(View.VISIBLE);
                    deleteEventButton.setOnClickListener(v -> showDeleteConfirmation());
                } else {
                    deleteEventButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Exception e) {
                deleteEventButton.setVisibility(View.GONE);
            }
        });
    }

    private void displayEvent(Event event) {
        if (event == null) {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        nameView.setText(event.getName());
        locationView.setText(event.getLocation());
        dateTimeView.setText(event.getDate());
        descriptionView.setText(event.getDescription());

        String posterUrl = event.getPosterUrl();

        if (posterUrl != null && !posterUrl.isEmpty()) {
            Glide.with(this)
                    .load(posterUrl)
                    .into(posterView);
        }
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteEvent())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteEvent() {
        if (eventId != null) {
            eventViewModel.deleteEvent(eventId, result -> {
                if (result) {
                    Toast.makeText(EventDetailsView.this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EventDetailsView.this, "Failed to delete event", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

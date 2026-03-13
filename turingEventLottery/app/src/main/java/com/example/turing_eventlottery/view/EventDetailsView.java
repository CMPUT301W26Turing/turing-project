package com.example.turing_eventlottery.view;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.Event;
import com.example.turing_eventlottery.model.EventCallback;
import com.example.turing_eventlottery.model.User;
import com.example.turing_eventlottery.viewmodel.EventViewModel;
import com.example.turing_eventlottery.viewmodel.UserViewModel;
import com.google.android.material.button.MaterialButton;

/**
 * View for displaying a specific event's details.
 * <p>
 *     Handles displaying event information (poster, name, location, date/time, description)
 *     Admin actions and user actions.
 * </p>
 */
public class EventDetailsView extends AppCompatActivity {
    private EventViewModel eventViewModel;
    private UserViewModel userViewModel;

    private ImageView posterView;
    private TextView nameView;
    private TextView locationView;
    private TextView dateTimeView;
    private TextView demandCountView;
    private TextView descriptionView;
    private TextView organizerIdView;
    private View organizerBox;
    private MaterialButton deleteEventButton;
    private MaterialButton waitlistButton;

    private boolean isOnWaitlist;
    private String eventId;
    private boolean fromAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.event_details);

        // Bind views
        posterView = findViewById(R.id.eventPoster);
        nameView = findViewById(R.id.eventName);
        locationView = findViewById(R.id.eventLocation);
        dateTimeView = findViewById(R.id.eventDateTime);
        demandCountView = findViewById(R.id.demandTotal);
        descriptionView = findViewById(R.id.eventDescription);
        organizerIdView = findViewById(R.id.organizerId);
        organizerBox = findViewById(R.id.organizerBox);
        deleteEventButton = findViewById(R.id.deleteEventButton);
        waitlistButton = findViewById(R.id.waitlistButton);
        MaterialButton backButton = findViewById(R.id.backButton);

        eventViewModel = new EventViewModel();
        userViewModel = new UserViewModel(this);

        eventId = getIntent().getStringExtra("EVENT_ID");
        fromAdmin = getIntent().getBooleanExtra("fromAdmin", false);

        backButton.setOnClickListener(v -> finish());

        // Show organizer info and hide waitlist if accessed by admin
        if (fromAdmin) {
            organizerBox.setVisibility(View.VISIBLE);
            waitlistButton.setVisibility(View.GONE);
        }

        if (eventId != null) {
            eventViewModel.getEventById(eventId, this::displayEvent);

            // Enable waitlist button only if registration is open
            eventViewModel.checkRegistrationStatus(eventId, isOpen -> {
                waitlistButton.setEnabled(isOpen);
            });
        }

        userViewModel.loadUser(loadedUser -> {
            // Show delete button for admins
            if (loadedUser.isAdmin() && fromAdmin) {
                deleteEventButton.setVisibility(View.VISIBLE);
                deleteEventButton.setOnClickListener(v -> showDeleteConfirmation());
            }

            // Check if the user is on the waitlist
            eventViewModel.isUserOnWaitlist(loadedUser, eventId, onWaitlist -> {
                isOnWaitlist = onWaitlist;
                updateWaitlistButton();
            });

            // Handle waitlist join/leave
            waitlistButton.setOnClickListener(v -> {
                if ("Guest".equals(loadedUser.getUserName())) {
                    Toast.makeText(this,
                            "You must create an account first, go to My Profile",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isOnWaitlist) {
                    eventViewModel.leaveWaitlist(loadedUser, eventId, success -> {
                        if (success) {
                            isOnWaitlist = false;
                            Toast.makeText(this, "You have left the waitlist", Toast.LENGTH_SHORT).show();
                            updateWaitlistButton();
                        } else {
                            Toast.makeText(this, "Failed to leave waitlist, try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    eventViewModel.joinWaitlist(loadedUser, eventId, success -> {
                        if (success) {
                            isOnWaitlist = true;
                            Toast.makeText(this, "You have joined the waitlist", Toast.LENGTH_SHORT).show();
                            updateWaitlistButton();
                        } else {
                            Toast.makeText(this, "Failed to join waitlist, try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        });
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    eventViewModel.deleteEvent(eventId, success -> {
                        if (success) {
                            Toast.makeText(this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to delete event", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void displayEvent(Event event) {
        if (event == null) return;
        nameView.setText(event.getName());
        locationView.setText(event.getLocation());
        dateTimeView.setText(eventViewModel.formatEventDate(event.getDate()));
        eventViewModel.getWaitlistCount(event.getId(), count ->
                demandCountView.setText(String.valueOf(count)));
        descriptionView.setText(event.getDescription());

        if (fromAdmin && event.getOrganizerId() != null) {
            organizerIdView.setText(event.getOrganizerId());
        }

        String posterUrl = event.getPosterUrl();
        if (posterUrl != null && !posterUrl.isEmpty()) {
            Glide.with(this).load(posterUrl).into(posterView);
        }

    }

    private void updateWaitlistButton() {
        if (isOnWaitlist) {
            waitlistButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
            waitlistButton.setTextColor(getColor(R.color.red));
            waitlistButton.setText("Leave Waitlist");
        } else {
            waitlistButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.primaryBlue)));
            waitlistButton.setTextColor(getColor(R.color.white));
            waitlistButton.setText("Join Waitlist");
        }
    }
}

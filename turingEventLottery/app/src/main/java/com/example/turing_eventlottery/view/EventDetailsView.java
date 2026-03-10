package com.example.turing_eventlottery.view;

import static androidx.databinding.DataBindingUtil.setContentView;

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
import com.example.turing_eventlottery.model.User;
import com.example.turing_eventlottery.viewmodel.EventViewModel;
import com.example.turing_eventlottery.viewmodel.UserViewModel;
import com.google.android.material.button.MaterialButton;

public class EventDetailsView extends AppCompatActivity {
    private EventViewModel eventViewModel;
    private UserViewModel userViewModel;

    private ImageView posterView;
    private TextView nameView;
    private TextView locationView;
    private TextView dateTimeView;
    private TextView descriptionView;

    private boolean isOnWaitlist;

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
        MaterialButton waitlistButton = findViewById(R.id.waitlistButton);

        eventViewModel = new EventViewModel();
        userViewModel = new UserViewModel(this);

        String eventId = getIntent().getStringExtra("EVENT_ID");

        if (eventId != null) {
            eventViewModel.getEventById(eventId, this::displayEvent);

            eventViewModel.checkRegistrationStatus(eventId, isOpen -> {
                if (isOpen) {
                    waitlistButton.setEnabled(true);
                } else {
                    waitlistButton.setEnabled(false);
                }
            });
        }

        userViewModel.loadUser(loadedUser -> {
            eventViewModel.isUserOnWaitlist(loadedUser, eventId, onWaitlist -> {
                isOnWaitlist = onWaitlist;
                updateWaitlistButton(waitlistButton);
            });

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
                            Toast.makeText(this,
                                    "You have left the waitlist",
                                    Toast.LENGTH_SHORT).show();
                            updateWaitlistButton(waitlistButton);
                        } else {
                            Toast.makeText(this,
                                    "Failed to leave waitlist, try again",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    eventViewModel.joinWaitlist(loadedUser, eventId, success -> {
                        if (success) {
                            isOnWaitlist = true;
                            Toast.makeText(this,
                                    "You have joined the waitlist",
                                    Toast.LENGTH_SHORT).show();
                            updateWaitlistButton(waitlistButton);
                        } else {
                            Toast.makeText(this,
                                    "Failed to join waitlist, try again",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        });
    }

    private void displayEvent(Event event) {
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

    private void updateWaitlistButton(MaterialButton button) {
        if (isOnWaitlist) {
            button.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
            button.setTextColor(getColor(R.color.red));
            button.setText("Leave Waitlist");
        } else {
            button.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.primaryBlue)));
            button.setTextColor(getColor(R.color.white));
            button.setText("Join Waitlist");
        }
    }
}

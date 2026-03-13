package com.example.turing_eventlottery.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Toast;

import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.EventRepository;
import com.example.turing_eventlottery.viewmodel.UserViewModel;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class OrganizerDashboardView extends AppCompatActivity {

    MaterialCardView myEventsButton;
    MaterialCardView sendNotificationButton;
    TextView activeLotteriesNumber;

    EventRepository eventRepository;
    UserViewModel userViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.organizer_dashboard);

        FrameLayout createEventCard = findViewById(R.id.createEventCard);
        FloatingActionButton fabCreate = findViewById(R.id.fabCreate);
        myEventsButton = findViewById(R.id.myEventsButton);
        sendNotificationButton = findViewById(R.id.sendNotificationButton);
        activeLotteriesNumber = findViewById(R.id.activeLotteriesNumber);

        View.OnClickListener startCreateEvent = v -> {
            Intent intent = new Intent(OrganizerDashboardView.this, CreateEventView.class);
            startActivity(intent);
        };

        if (createEventCard != null) {
            createEventCard.setOnClickListener(startCreateEvent);
        }

        if (fabCreate != null) {
            fabCreate.setOnClickListener(startCreateEvent);
        }

        myEventsButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerDashboardView.this, MyEventsView.class);
            startActivity(intent);
        });

        sendNotificationButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerDashboardView.this, SendNotificationView.class);
            startActivity(intent);
        });


        eventRepository = new EventRepository();
        userViewModel = new UserViewModel(this);

        // Check if organizer is banned
        userViewModel.loadUser(user -> {
            if (user != null && user.isBanned()) {
                Toast.makeText(this, "Your organizer account has been suspended", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        });

        eventRepository.getEventsByOrganizer(userViewModel.getDeviceId(), events -> {
            if (events != null) {
                activeLotteriesNumber.setText(String.valueOf(events.size()));
            } else {
                activeLotteriesNumber.setText("0");
            }
        });
    }
}

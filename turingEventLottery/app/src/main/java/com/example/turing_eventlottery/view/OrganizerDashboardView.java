package com.example.turing_eventlottery.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.turing_eventlottery.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class OrganizerDashboardView extends AppCompatActivity {

    MaterialCardView myEventsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.organizer_dashboard);

        FrameLayout createEventCard = findViewById(R.id.createEventCard);
        FloatingActionButton fabCreate = findViewById(R.id.fabCreate);
        myEventsButton = findViewById(R.id.myEventsButton);


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
    }
}

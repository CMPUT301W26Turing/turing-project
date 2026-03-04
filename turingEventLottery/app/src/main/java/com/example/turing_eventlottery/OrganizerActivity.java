package com.example.turing_eventlottery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class OrganizerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.organizer_dashboard);

        FrameLayout createEventCard = findViewById(R.id.createEventCard);
        FloatingActionButton fabCreate = findViewById(R.id.fabCreate);

        View.OnClickListener startCreateEvent = v -> {
            Intent intent = new Intent(OrganizerActivity.this, CreateEventActivity.class);
            startActivity(intent);
        };

        if (createEventCard != null) {
            createEventCard.setOnClickListener(startCreateEvent);
        }

        if (fabCreate != null) {
            fabCreate.setOnClickListener(startCreateEvent);
        }
    }
}

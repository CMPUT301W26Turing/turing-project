package com.example.turing_eventlottery.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.turing_eventlottery.R;
import com.google.android.material.card.MaterialCardView;

public class GreetingView extends AppCompatActivity {
    MaterialCardView entrantCard;
    MaterialCardView organizerCard;
    MaterialCardView administratorCard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.greeting_screen);
        
        entrantCard = findViewById(R.id.entrantCard);
        organizerCard = findViewById(R.id.organizerCard);
        administratorCard = findViewById(R.id.administratorCard);
        
        entrantCard.setOnClickListener(v -> {
            Intent intent = new Intent(GreetingView.this, EntrantDashboardView.class);
            startActivity(intent);
        });

        organizerCard.setOnClickListener(v -> {
            Intent intent = new Intent(GreetingView.this, OrganizerDashboardView.class);
            startActivity(intent);
        });
    }
}

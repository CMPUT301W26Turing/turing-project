package com.example.turing_eventlottery.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.User;
import com.example.turing_eventlottery.model.UserCallback;
import com.example.turing_eventlottery.viewmodel.UserViewModel;
import com.google.android.material.card.MaterialCardView;

public class GreetingView extends AppCompatActivity {
    MaterialCardView entrantCard;
    MaterialCardView organizerCard;
    MaterialCardView administratorCard;
    UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.greeting_screen);

        entrantCard = findViewById(R.id.entrantCard);
        organizerCard = findViewById(R.id.organizerCard);
        administratorCard = findViewById(R.id.administratorCard);
        userViewModel = new UserViewModel(this);

        entrantCard.setOnClickListener(v -> {
            Intent intent = new Intent(GreetingView.this, EntrantDashboardView.class);
            startActivity(intent);
        });

        organizerCard.setOnClickListener(v -> {
            Intent intent = new Intent(GreetingView.this, OrganizerDashboardView.class);
            startActivity(intent);
        });

        administratorCard.setOnClickListener(v -> {
            userViewModel.loadUser(new UserCallback() {
                @Override
                public void onSuccess(User user) {
                    if (user != null && user.isAdmin()) {
                        Intent intent = new Intent(GreetingView.this, AdminDashboardView.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(GreetingView.this, "Access Denied: Not an Administrator", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(GreetingView.this, "Error verifying administrator status", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}

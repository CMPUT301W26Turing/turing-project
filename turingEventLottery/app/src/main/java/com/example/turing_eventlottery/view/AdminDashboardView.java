package com.example.turing_eventlottery.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.User;
import com.example.turing_eventlottery.model.UserCallback;
import com.example.turing_eventlottery.viewmodel.UserViewModel;
import com.google.android.material.card.MaterialCardView;

public class AdminDashboardView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.admin_dashboard);

        TextView welcomeText = findViewById(R.id.textView4);
        MaterialCardView browseEventsButton = findViewById(R.id.myEventsButton);
        MaterialCardView browseProfilesButton = findViewById(R.id.analyticsButton);

        UserViewModel userViewModel = new UserViewModel(this);
        userViewModel.loadUser(new UserCallback() {
            @Override
            public void onSuccess(User user) {
                if (user != null && user.getUserName() != null) {
                    welcomeText.setText("Welcome back, " + user.getUserName());
                } else {
                    welcomeText.setText("Welcome back, Admin");
                }
            }
        });

        browseEventsButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardView.this, BrowseEventsView.class);
            intent.putExtra("fromAdmin", true);
            startActivity(intent);
        });

        browseProfilesButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardView.this, BrowseProfilesView.class);
            intent.putExtra("fromAdmin", true);
            startActivity(intent);
        });
    }
}

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
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

        MaterialCardView manageOrganizersButton = findViewById(R.id.manageOrganizersButton);
        manageOrganizersButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardView.this, BrowseOrganizersView.class);
            startActivity(intent);
        });

        MaterialCardView notificationLogsButton = findViewById(R.id.notificationLogsButton);
        notificationLogsButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardView.this, BrowseNotificationLogsView.class);
            startActivity(intent);
        });

        // Bottom Navigation Bar
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(AdminDashboardView.this, UserDashboardView.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                Intent intent = new Intent(AdminDashboardView.this, MyProfileView.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }
}

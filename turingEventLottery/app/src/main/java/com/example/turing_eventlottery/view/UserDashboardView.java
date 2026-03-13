package com.example.turing_eventlottery.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.User;
import com.example.turing_eventlottery.model.UserCallback;
import com.example.turing_eventlottery.viewmodel.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Activity representing the main dashboard for a user.
 * This view provides navigation to different user features
 *
 * <p>
 *     This class acts as the View component in the MVVM architecture
 *     and interacts with the UserViewModel to manage user data.
 * </p>
 *
 * @author Matthew Adams
 * @version 1.0
 * @since 1.0
 * @see UserViewModel
 */

public class UserDashboardView extends AppCompatActivity {
    private TextView greetingMessage;
    private TextView profileInitialsText;

    /**
     * Initializes the dashboard UI and sets up navigation
     * for dashboard options.
     *
     * @param savedInstanceState previously saved activity state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.entrant_dashboard);

        UserViewModel userViewModel = new UserViewModel(this);

        greetingMessage = findViewById(R.id.greetingMessage);
        profileInitialsText = findViewById(R.id.profileInitialsText);
        MaterialButton adminButton = findViewById(R.id.adminButton);

        MaterialButton adminButton = findViewById(R.id.adminButton);

        userViewModel.loadUser(new UserCallback() {
            @Override
            public void onSuccess(User user) {
                if (user != null) {
                    String username = user.getUserName();
                    if (username != null && !username.isEmpty()) {
                        greetingMessage.setText("Hello, " + username.split(" ")[0]);

                        // this code sets the initials of the profile icon on the top left
                        String initials = "";
                        String[] parts = username.split(" ");
                        if (parts.length > 0 && !parts[0].isEmpty()) {
                            initials += parts[0].substring(0, 1).toUpperCase();
                            if (parts.length > 1 && !parts[1].isEmpty()) {
                                initials += parts[1].substring(0, 1).toUpperCase();
                            }
                        }
                        profileInitialsText.setText(initials);
                    }
                    if (user.isAdmin()) {
                        adminButton.setVisibility(View.VISIBLE);
                    }
                }

                // Admin button
                if (user.isAdmin()) {
                    adminButton.setVisibility(View.VISIBLE);;

                    adminButton.setOnClickListener(v -> {
                        Intent intent = new Intent(UserDashboardView.this, AdminDashboardView.class);
                        startActivity(intent);
                    });
                }
            }
        });

        adminButton.setOnClickListener(v -> {
            startActivity(new Intent(UserDashboardView.this, AdminDashboardView.class));
        });

        setupNavigation();

        MaterialCardView joinByQrCode = findViewById(R.id.joinByQrCode);
        MaterialCardView browseEvents = findViewById(R.id.browseEvents);
        MaterialCardView myNotifications = findViewById(R.id.myNotifications);
        MaterialCardView myHistory = findViewById(R.id.myHistory);
        MaterialCardView myProfile = findViewById(R.id.myProfile);

//        joinByQrCode.setOnClickListener(v -> {
//            Intent intent = new Intent(EntrantDashboardView.this, JoinByQrCode.class);
//            startActivity(intent);
//        });
        browseEvents.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardView.this, BrowseEventsView.class);
            startActivity(intent);
        });
        myNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardView.this, MyNotificationsView.class);
            startActivity(intent);
        });
//        myHistory.setOnClickListener(v -> {
//            Intent intent = new Intent(this, MyHistory.class);
//            startActivity(intent);
//        });
        myProfile.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardView.this, MyProfileView.class);
            startActivity(intent);
        });
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        FloatingActionButton fabCreate = findViewById(R.id.fabCreate);

        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_events) {
                startActivity(new Intent(this, BrowseEventsView.class));
                return true;
            } else if (itemId == R.id.nav_alerts) {
                startActivity(new Intent(this, MyNotificationsView.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, MyProfileView.class));
                return true;
            }
            return false;
        });

        fabCreate.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateEventView.class));
        });
    }
}
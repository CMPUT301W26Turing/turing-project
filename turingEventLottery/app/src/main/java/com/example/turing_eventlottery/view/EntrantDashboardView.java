package com.example.turing_eventlottery.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.User;
import com.example.turing_eventlottery.model.UserCallback;
import com.example.turing_eventlottery.viewmodel.UserViewModel;
import com.google.android.material.card.MaterialCardView;

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

public class EntrantDashboardView extends AppCompatActivity {

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

        userViewModel.loadUser(new UserCallback() {
            @Override
            public void onSuccess(User user) {
                // Update UI here
            }
        });

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
            Intent intent = new Intent(EntrantDashboardView.this, BrowseEventsView.class);
            startActivity(intent);
        });
        myNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(EntrantDashboardView.this, MyNotificationsView.class);
            startActivity(intent);
        });
//        myHistory.setOnClickListener(v -> {
//            Intent intent = new Intent(this, MyHistory.class);
//            startActivity(intent);
//        });
        myProfile.setOnClickListener(v -> {
            Intent intent = new Intent(EntrantDashboardView.this, MyProfileView.class);
            startActivity(intent);
        });
    }
}
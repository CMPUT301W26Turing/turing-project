package com.example.turing_eventlottery.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.User;
import com.example.turing_eventlottery.viewmodel.UserViewModel;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class BrowseProfilesView extends AppCompatActivity {
    private LinearLayout profilesContainer;
    private TextView profilesCountText;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.browse_profiles);

        profilesContainer = findViewById(R.id.profilesContainer);
        profilesCountText = findViewById(R.id.profilesCountText);
        userViewModel = new UserViewModel(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfiles();
    }

    private void loadProfiles() {
        userViewModel.getAllUsers(users -> {
            if (users != null) {
                displayProfiles(users);
            }
        });
    }

    private void displayProfiles(List<User> users) {
        profilesContainer.removeAllViews();
        profilesCountText.setText(users.size() + " Profiles");

        LayoutInflater inflater = LayoutInflater.from(this);
        for (User user : users) {
            MaterialCardView card = (MaterialCardView) inflater.inflate(R.layout.browse_events_card, profilesContainer, false);
            
            // Reusing browse_events_card but mapping fields to user data
            TextView nameText = card.findViewById(R.id.eventName);
            TextView infoText = card.findViewById(R.id.locationText);
            TextView subInfoText = card.findViewById(R.id.dateTimeText);
            
            String displayName = user.getUserName();
            if (displayName == null || displayName.isEmpty()) {
                displayName = "Anonymous User";
            }
            nameText.setText(displayName);
            infoText.setText("ID: " + user.getUserId());
            subInfoText.setText(user.isAdmin() ? "Role: Admin" : "Role: Entrant");
            
            card.setOnClickListener(v -> {
                Intent intent = new Intent(this, ProfileDetailsView.class);
                intent.putExtra("USER_ID", user.getUserId());
                startActivity(intent);
            });

            profilesContainer.addView(card);
        }
    }
}

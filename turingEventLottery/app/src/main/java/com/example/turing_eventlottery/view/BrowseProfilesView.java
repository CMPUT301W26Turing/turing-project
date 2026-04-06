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
import com.google.android.material.chip.Chip;

import java.util.List;

/**
 * Activity for browsing all user profiles.
 * <p>
 *     Displays a list of all users with basic information (name, ID, role).
 *     Supports navigation to detailed profile view.
 * </p>
 */
public class BrowseProfilesView extends AppCompatActivity {
    private LinearLayout profilesContainer;
    private TextView profilesCountText;
    private UserViewModel userViewModel;
    private boolean fromAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.browse_profiles);

        profilesContainer = findViewById(R.id.profilesContainer);
        profilesCountText = findViewById(R.id.profilesCountText);
        userViewModel = new UserViewModel(this);
        fromAdmin = getIntent().getBooleanExtra("fromAdmin", false);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
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
            MaterialCardView card = (MaterialCardView) inflater.inflate(
                    R.layout.browse_organizers_card, profilesContainer, false);

            TextView nameText = card.findViewById(R.id.organizerCardName);
            TextView emailText = card.findViewById(R.id.organizerCardEmail);
            Chip roleChip = card.findViewById(R.id.organizerCardStatus);

            // Fallback if missing name/ID
            String displayName = user.getUserName();
            if (displayName == null || displayName.isEmpty()) {
                displayName = "Anonymous User";
            }
            nameText.setText(displayName);
            emailText.setText("ID: " + user.getUserId());
            roleChip.setText(user.isAdmin() ? "Role: Admin" : "Role: Entrant");

            card.setOnClickListener(v -> {
                Intent intent = new Intent(this, ProfileDetailsView.class);
                intent.putExtra("USER_ID", user.getUserId());
                if (fromAdmin) {
                    intent.putExtra("fromAdmin", true);
                }
                startActivity(intent);
            });

            profilesContainer.addView(card);
        }
    }
}
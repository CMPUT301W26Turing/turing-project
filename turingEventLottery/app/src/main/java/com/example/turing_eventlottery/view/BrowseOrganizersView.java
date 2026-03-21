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

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for browsing all organizers (admin view).
 * <p>
 *     Displays a list of all non-banned organizers, showing their user details
 *     such as, name, email, and status. Each organizer card navigates
 *     to the organizer detail view when clicked.
 * </p>
 */
public class BrowseOrganizersView extends AppCompatActivity {
    private LinearLayout organizersContainer;
    private TextView organizersCountText;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.browse_organizers);

        organizersContainer = findViewById(R.id.organizersContainer);
        organizersCountText = findViewById(R.id.organizersCountText);
        userViewModel = new UserViewModel(this);

        // Back button that closes activity
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrganizers(); // Refresh the list of organizers when the view resumes
    }

    private void loadOrganizers() {
        // Async call to fetch all organizers
        userViewModel.getAllOrganizers(organizers -> {
            if (organizers != null) {
                displayOrganizers(organizers);
            }
        });
    }

    private void displayOrganizers(List<User> organizers) {
        organizersContainer.removeAllViews();

        // Filter out all banned organizers
        List<User> activeOrganizers = new ArrayList<>();
        for (User organizer : organizers) {
            if (!organizer.isBanned()) {
                activeOrganizers.add(organizer);
            }
        }

        // Update count text
        organizersCountText.setText(activeOrganizers.size() + " Organizer" + (activeOrganizers.size() != 1 ? "s" : ""));

        LayoutInflater inflater = LayoutInflater.from(this);
        for (User organizer : activeOrganizers) {
            MaterialCardView card = (MaterialCardView) inflater.inflate(R.layout.browse_organizers_card, organizersContainer, false);

            TextView nameText = card.findViewById(R.id.organizerCardName);
            TextView emailText = card.findViewById(R.id.organizerCardEmail);
            com.google.android.material.chip.Chip statusChip = card.findViewById(R.id.organizerCardStatus);

            // Fallback if missing name/email
            String displayName = organizer.getUserName();
            if (displayName == null || displayName.isEmpty()) {
                displayName = "Anonymous Organizer";
            }
            nameText.setText(displayName);
            emailText.setText(organizer.getUserEmail() != null ? organizer.getUserEmail() : "No Email");
            statusChip.setText("Active");

            // Click card to view organizer details
            card.setOnClickListener(v -> {
                Intent intent = new Intent(this, OrganizerDetailView.class);
                intent.putExtra("ORGANIZER_ID", organizer.getUserId());
                startActivity(intent);
            });

            organizersContainer.addView(card);
        }
    }
}

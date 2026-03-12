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

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrganizers();
    }

    private void loadOrganizers() {
        userViewModel.getAllOrganizers(organizers -> {
            if (organizers != null) {
                displayOrganizers(organizers);
            }
        });
    }

    private void displayOrganizers(List<User> organizers) {
        organizersContainer.removeAllViews();

        List<User> activeOrganizers = new ArrayList<>();
        for (User organizer : organizers) {
            if (!organizer.isBanned()) {
                activeOrganizers.add(organizer);
            }
        }

        organizersCountText.setText(activeOrganizers.size() + " Organizer" + (activeOrganizers.size() != 1 ? "s" : ""));

        LayoutInflater inflater = LayoutInflater.from(this);
        for (User organizer : activeOrganizers) {
            MaterialCardView card = (MaterialCardView) inflater.inflate(R.layout.browse_organizers_card, organizersContainer, false);

            TextView nameText = card.findViewById(R.id.organizerCardName);
            TextView emailText = card.findViewById(R.id.organizerCardEmail);
            com.google.android.material.chip.Chip statusChip = card.findViewById(R.id.organizerCardStatus);

            String displayName = organizer.getUserName();
            if (displayName == null || displayName.isEmpty()) {
                displayName = "Anonymous Organizer";
            }
            nameText.setText(displayName);
            emailText.setText(organizer.getUserEmail() != null ? organizer.getUserEmail() : "No Email");
            statusChip.setText("Active");

            card.setOnClickListener(v -> {
                Intent intent = new Intent(this, OrganizerDetailView.class);
                intent.putExtra("ORGANIZER_ID", organizer.getUserId());
                startActivity(intent);
            });

            organizersContainer.addView(card);
        }
    }
}

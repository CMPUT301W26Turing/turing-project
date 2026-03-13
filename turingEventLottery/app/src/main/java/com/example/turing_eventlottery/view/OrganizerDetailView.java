package com.example.turing_eventlottery.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.User;
import com.example.turing_eventlottery.model.UserCallback;
import com.example.turing_eventlottery.viewmodel.UserViewModel;

public class OrganizerDetailView extends AppCompatActivity {
    private UserViewModel userViewModel;
    private TextView organizerNameText, organizerIdText, organizerEmailText, organizerStatusText;
    private Button removeOrganizerButton;
    private String targetOrganizerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.organizer_detail);

        organizerNameText = findViewById(R.id.organizerName);
        organizerIdText = findViewById(R.id.organizerId);
        organizerEmailText = findViewById(R.id.organizerEmail);
        organizerStatusText = findViewById(R.id.organizerStatus);
        removeOrganizerButton = findViewById(R.id.removeOrganizerButton);

        userViewModel = new UserViewModel(this);
        targetOrganizerId = getIntent().getStringExtra("ORGANIZER_ID");

        if (targetOrganizerId != null) {
            loadOrganizerProfile();
        }

        removeOrganizerButton.setOnClickListener(v -> showRemoveConfirmation());
    }

    private void loadOrganizerProfile() {
        userViewModel.loadUserById(targetOrganizerId, new UserCallback() {
            @Override
            public void onSuccess(User organizer) {
                if (organizer != null) {
                    organizerNameText.setText(organizer.getUserName());
                    organizerIdText.setText(organizer.getUserId());
                    organizerEmailText.setText(organizer.getUserEmail() != null ? organizer.getUserEmail() : "No Email");
                    organizerStatusText.setText(organizer.isBanned() ? "Banned" : "Active");
                }
            }
        });
    }

    private void showRemoveConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Remove Organizer")
                .setMessage("Are you sure you want to remove this organizer? This action cannot be undone. The organizer will no longer be able to create or manage events.")
                .setPositiveButton("Remove", (dialog, which) -> removeOrganizer())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void removeOrganizer() {
        userViewModel.removeOrganizer(targetOrganizerId, result -> {
            if (result) {
                Toast.makeText(OrganizerDetailView.this, "Organizer removed successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(OrganizerDetailView.this, "Failed to remove organizer", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

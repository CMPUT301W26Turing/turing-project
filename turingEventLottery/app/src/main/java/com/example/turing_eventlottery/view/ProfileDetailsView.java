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

public class ProfileDetailsView extends AppCompatActivity {
    private UserViewModel userViewModel;
    private TextView userIdText, contactInfoText, adminStatusText;
    private Button deleteProfileButton;
    private String targetUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.profile_details);

        userIdText = findViewById(R.id.profileUserId);
        contactInfoText = findViewById(R.id.profileContactInfo);
        adminStatusText = findViewById(R.id.profileAdminStatus);
        deleteProfileButton = findViewById(R.id.deleteProfileButton);

        userViewModel = new UserViewModel(this);
        targetUserId = getIntent().getStringExtra("USER_ID");

        if (targetUserId != null) {
            loadUserProfile();
        }

        deleteProfileButton.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void loadUserProfile() {
        userViewModel.loadUserById(targetUserId, new UserCallback() {
            @Override
            public void onSuccess(User user) {
                if (user != null) {
                    userIdText.setText(user.getUserId());
                    contactInfoText.setText(user.getUserEmail() != null ? user.getUserEmail() : "No Contact Info");
                    adminStatusText.setText(String.valueOf(user.isAdmin()));
                }
            }
        });
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Profile")
                .setMessage("Are you sure you want to delete this profile? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteProfile())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteProfile() {
        userViewModel.deleteUser(targetUserId, result -> {
            if (result) {
                Toast.makeText(ProfileDetailsView.this, "Profile deleted successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(ProfileDetailsView.this, "Failed to delete profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

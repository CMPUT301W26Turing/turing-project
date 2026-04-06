package com.example.turing_eventlottery.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.User;
import com.example.turing_eventlottery.viewmodel.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Activity that displays and allows editing of the current user's profile.
 * <p>
 *     Users can update their name, email, phone number.
 *     Also includes navigation to other parts of the application via bottom navigation and FAB.
 * </p>
 */
public class MyProfileView extends AppCompatActivity {
    private EditText fullNameText;
    private EditText emailText;
    private EditText phoneText;
    private ImageView profilePicture;

    private User currentUser;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.my_profile);

        fullNameText = findViewById(R.id.fullNameValue);
        emailText = findViewById(R.id.emailValue);
        phoneText = findViewById(R.id.phoneValue);
        profilePicture = findViewById(R.id.profilePicture);
        MaterialButton deleteProfileButton = findViewById(R.id.deleteProfileButton);
        deleteProfileButton.setOnClickListener(v -> showDeleteProfileConfirmation());

        UserViewModel userViewModel = new UserViewModel(this);
        userViewModel.loadUser(user -> {
            currentUser = user;
            fullNameText.setText(user.getUserName());
            emailText.setText(user.getUserEmail());
            phoneText.setText(user.getUserPhoneNumber());

            if ("Guest".equals(user.getUserName())) {
                deleteProfileButton.setVisibility(View.GONE);
            } else {
                deleteProfileButton.setVisibility(View.VISIBLE);
            }

            // TODO: load profile picture from stored URL with glide
        });

        MaterialButton saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            String name = fullNameText.getText().toString().trim();
            String email = emailText.getText().toString().trim();
            String phone = phoneText.getText().toString().trim();

            // Validate email format
            if (!email.contains("@") || !email.endsWith(".com")) {
                Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (phone.isEmpty()) {
                phone = null;
            }

            // Update user profile
            userViewModel.updateUserProfile(name, email, phone);
            Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        setupNavigation();
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        FloatingActionButton fabCreate = findViewById(R.id.fabCreate);

        bottomNav.setSelectedItemId(R.id.nav_profile);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, UserDashboardView.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_events) {
                startActivity(new Intent(this, BrowseEventsView.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_alerts) {
                startActivity(new Intent(this, MyNotificationsView.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                return true;
            }
            return false;
        });

        fabCreate.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateEventView.class);
            startActivity(intent);
        });
    }

    private void showDeleteProfileConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Delete Profile")
                .setMessage("Are you sure you want to delete your Profile? This action cannot be undone.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        android.widget.Button confirmButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        confirmButton.setEnabled(false);
        confirmButton.setText("Delete (5)");

        new android.os.CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                confirmButton.setText("Delete (" + (millisUntilFinished / 1000) + ")");
            }

            @Override
            public void onFinish() {
                confirmButton.setEnabled(true);
                confirmButton.setText("Delete");
            }
        }.start();

        confirmButton.setOnClickListener(V -> {
            dialog.dismiss();
            confirmButton.setEnabled(false);

            if (currentUser == null) {
                Toast.makeText(this, "User not loaded, please try again", Toast.LENGTH_SHORT).show();
                return;
            }

            userViewModel.deleteUser(currentUser.getUserId(), success -> {
                if (success) {
                    Toast.makeText(this, "Profile deleted successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, UserDashboardView.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Failed to delete profile, please try again", Toast.LENGTH_SHORT).show();
                    confirmButton.setEnabled(true);
                }
            });
        });
    }
}
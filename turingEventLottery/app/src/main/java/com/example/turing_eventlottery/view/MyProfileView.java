package com.example.turing_eventlottery.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.User;
import com.example.turing_eventlottery.viewmodel.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MyProfileView extends AppCompatActivity {
    private EditText fullNameText;
    private EditText emailText;
    private EditText phoneText;
    private ImageView profilePicture;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.my_profile);

        fullNameText = findViewById(R.id.fullNameValue);
        emailText = findViewById(R.id.emailValue);
        phoneText = findViewById(R.id.phoneValue);
        profilePicture = findViewById(R.id.profilePicture);

        UserViewModel userViewModel = new UserViewModel(this);
        userViewModel.loadUser(user -> {
            currentUser = user;
            fullNameText.setText(user.getUserName());
            emailText.setText(user.getUserEmail());
            phoneText.setText(user.getUserPhoneNumber());

            // TODO: load profile picture from stored URL with glide
        });

        MaterialButton saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            String name = fullNameText.getText().toString().trim();
            String email = emailText.getText().toString().trim();
            String phone = phoneText.getText().toString().trim();

            if (!email.contains("@") || !email.endsWith(".com")) {
                Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (phone.isEmpty()) {
                phone = null;
            }

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
}
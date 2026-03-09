package com.example.turing_eventlottery.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.User;
import com.example.turing_eventlottery.model.UserCallback;
import com.example.turing_eventlottery.viewmodel.UserViewModel;
import com.google.android.material.button.MaterialButton;

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
                        Toast.makeText(this, "Please enter a valid email address. ", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (phone.isEmpty()) {
                        phone = null;
                    }

                    userViewModel.updateUserProfile(name, email, phone);
                    Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show();
            });
    }
}

package com.example.turing_eventlottery.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.EventRepository;
import com.google.android.material.button.MaterialButton;

/**
 * Activity for displaying detailed information of a specific image.
 * Admins can view the image and delete it from the system.
 */
public class ImageDetailView extends AppCompatActivity {
    private EventRepository eventRepository;
    private String eventId;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.image_detail);

        ImageView detailImage = findViewById(R.id.detailImage);
        TextView detailEventName = findViewById(R.id.detailEventName);
        TextView detailOrganizerId = findViewById(R.id.detailOrganizerId);
        TextView detailEventId = findViewById(R.id.detailEventId);
        MaterialButton deleteImageButton = findViewById(R.id.deleteImageButton);

        eventRepository = new EventRepository();

        eventId = getIntent().getStringExtra("EVENT_ID");
        imageUrl = getIntent().getStringExtra("IMAGE_URL");
        String eventName = getIntent().getStringExtra("EVENT_NAME");
        String organizerId = getIntent().getStringExtra("ORGANIZER_ID");

        detailEventName.setText(eventName != null ? eventName : "Unknown Event");
        detailOrganizerId.setText("Organizer: " + (organizerId != null ? organizerId : "Unknown"));
        detailEventId.setText("Event ID: " + (eventId != null ? eventId : "Unknown"));

        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).into(detailImage);
        }

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        deleteImageButton.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Image")
                .setMessage("Are you sure you want to delete this image? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteImage())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteImage() {
        if (eventId == null || imageUrl == null) {
            Toast.makeText(this, "Failed to delete image", Toast.LENGTH_SHORT).show();
            return;
        }

        eventRepository.deleteEventPoster(eventId, imageUrl, success -> {
            if (success) {
                Toast.makeText(ImageDetailView.this, "Image deleted successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(ImageDetailView.this, "Failed to delete image", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

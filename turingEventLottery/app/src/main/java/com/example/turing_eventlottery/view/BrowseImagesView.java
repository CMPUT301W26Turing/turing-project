package com.example.turing_eventlottery.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.Event;
import com.example.turing_eventlottery.model.EventRepository;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for browsing all uploaded images in the system (admin only).
 * Displays a list of event poster images. Each image card navigates
 * to the image detail view when clicked.
 */
public class BrowseImagesView extends AppCompatActivity {
    private LinearLayout imagesContainer;
    private TextView imagesCountText;
    private EventRepository eventRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.browse_images);

        imagesContainer = findViewById(R.id.imagesContainer);
        imagesCountText = findViewById(R.id.imagesCountText);
        eventRepository = new EventRepository();

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadImages();
    }

    private void loadImages() {
        eventRepository.getEvents(events -> {
            if (events != null) {
                List<Event> eventsWithImages = new ArrayList<>();
                for (Event event : events) {
                    if (event.getPosterUrl() != null && !event.getPosterUrl().isEmpty()) {
                        eventsWithImages.add(event);
                    }
                }
                displayImages(eventsWithImages);
            }
        });
    }

    private void displayImages(List<Event> events) {
        imagesContainer.removeAllViews();
        imagesCountText.setText(events.size() + " Image" + (events.size() != 1 ? "s" : ""));

        LayoutInflater inflater = LayoutInflater.from(this);
        for (Event event : events) {
            MaterialCardView card = (MaterialCardView) inflater.inflate(
                    R.layout.browse_images_card, imagesContainer, false);

            ImageView imagePreview = card.findViewById(R.id.imagePreview);
            TextView eventName = card.findViewById(R.id.imageEventName);
            TextView uploadInfo = card.findViewById(R.id.imageUploadInfo);

            String displayName = event.getName();
            if (displayName == null || displayName.isEmpty()) {
                displayName = "Unnamed Event";
            }
            eventName.setText(displayName);
            uploadInfo.setText("Organizer: " + (event.getOrganizerId() != null ? event.getOrganizerId() : "Unknown"));

            Glide.with(this)
                    .load(event.getPosterUrl())
                    .centerCrop()
                    .into(imagePreview);

            card.setOnClickListener(v -> {
                Intent intent = new Intent(this, ImageDetailView.class);
                intent.putExtra("EVENT_ID", event.getId());
                intent.putExtra("IMAGE_URL", event.getPosterUrl());
                intent.putExtra("EVENT_NAME", event.getName());
                intent.putExtra("ORGANIZER_ID", event.getOrganizerId());
                startActivity(intent);
            });

            imagesContainer.addView(card);
        }
    }
}

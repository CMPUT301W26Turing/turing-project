package com.example.turing_eventlottery.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.Event;
import com.example.turing_eventlottery.viewmodel.EventViewModel;
import com.example.turing_eventlottery.viewmodel.UserViewModel;
import com.google.android.material.card.MaterialCardView;
import com.bumptech.glide.Glide;

import java.util.List;

public class BrowseEventsView extends AppCompatActivity {
    private LinearLayout eventsContainer;
    private EventViewModel eventViewModel;
    private UserViewModel userViewModel;
    private TextView resultsText;
    private TextView upcomingLotteries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.browse_events);

        eventsContainer = findViewById(R.id.eventsContainer);
        resultsText = findViewById(R.id.resultsText);
        upcomingLotteries = findViewById(R.id.upcomingText);
        eventViewModel = new EventViewModel();
        userViewModel = new UserViewModel(this);

        // Print device ID to find and set as admin in Firebase Console if needed
        Log.d("ADMIN_CHECK", "My Device ID is: " + userViewModel.getDeviceId());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data every time the activity is resumed to ensure the list is up to date
        eventViewModel.getEvents(this::displayEvents);
    }

    private void displayEvents(List<Event> events) {
        eventsContainer.removeAllViews();

        if (events == null || events.isEmpty()) {
            resultsText.setText("0 Results");
            upcomingLotteries.setText("No upcoming lotteries");
            return;
        }

        resultsText.setText(events.size() + " Results");
        upcomingLotteries.setText("Upcoming Lotteries");

        for (Event event : events) {
            MaterialCardView card = (MaterialCardView) LayoutInflater.from(this)
                    .inflate(R.layout.browse_events_card, eventsContainer, false);

            String posterUrl = event.getPosterUrl();

            ImageView posterView = card.findViewById(R.id.eventPoster);
            if (posterUrl == null || posterUrl.isEmpty()) {
                posterView.setBackgroundColor(getColor(R.color.secondaryClickableCardsAndSpinners));
                posterView.setImageDrawable(null);
            } else {
                Glide.with(this)
                        .load(posterUrl)
                        .placeholder(R.drawable.placeholder_image)
                        .into(posterView);
            }

            TextView location = card.findViewById(R.id.locationText);
            location.setText(event.getLocation());

            TextView name = card.findViewById(R.id.eventName);
            name.setText(event.getName());

            TextView dateTime = card.findViewById(R.id.dateTimeText);
            dateTime.setText(event.getDate());

            card.setOnClickListener(v -> openEventDetails(event.getId()));

            eventsContainer.addView(card);
        }
    }

   private void openEventDetails(String eventId) {
        Intent intent = new Intent(this, EventDetailsView.class);
        intent.putExtra("EVENT_ID", eventId);
        startActivity(intent);
   }
}

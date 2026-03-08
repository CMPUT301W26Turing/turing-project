package com.example.turing_eventlottery.view;

import static androidx.databinding.DataBindingUtil.setContentView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.Event;
import com.example.turing_eventlottery.viewmodel.EventViewModel;

public class EventDetailsView extends AppCompatActivity {
    private EventViewModel eventViewModel;

    private ImageView posterView;
    private TextView nameView;
    private TextView locationView;
    private TextView dateTimeView;
    private TextView descriptionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.event_details);

        posterView = findViewById(R.id.eventPoster);
        nameView = findViewById(R.id.eventName);
        locationView = findViewById(R.id.eventLocation);
        dateTimeView = findViewById(R.id.eventDateTime);
        descriptionView = findViewById(R.id.eventDescription);

        eventViewModel = new EventViewModel();

        String eventId = getIntent().getStringExtra("EVENT_ID");

        if (eventId != null) {
            eventViewModel.getEventById(eventId, this::displayEvent);
        }
    }

    private void displayEvent(Event event) {
        nameView.setText(event.getName());
        locationView.setText(event.getLocation());
        dateTimeView.setText(event.getDate() + " • " + event.getTime());
        descriptionView.setText(event.getDescription());

        String posterUrl = event.getPosterUrl();

        if (posterUrl != null && !posterUrl.isEmpty()) {
            Glide.with(this)
                    .load(posterUrl)
                    .into(posterView);
        }
    }
}

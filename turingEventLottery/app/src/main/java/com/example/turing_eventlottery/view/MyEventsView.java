package com.example.turing_eventlottery.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.Event;
import com.example.turing_eventlottery.model.EventRepository;
import com.example.turing_eventlottery.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity to display a list of events created by the current organizer. Users
 * a RecyclerView with MyEventsAdapter to show each event and its registration status.
 */
// Need to fix MVVM architecture for part 4
public class MyEventsView extends AppCompatActivity {

    private RecyclerView myEventsRecyclerView;
    private MyEventsAdapter myEventsAdapter;
    private EventRepository eventRepository;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.my_events);

        eventRepository = new EventRepository();
        userViewModel = new UserViewModel(this);

        ImageView backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        myEventsRecyclerView = findViewById(R.id.myEventsRecyclerView);
        myEventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myEventsAdapter = new MyEventsAdapter(new ArrayList<>());
        myEventsRecyclerView.setAdapter(myEventsAdapter);

        loadMyEvents();
    }

    private void loadMyEvents() {
        String currentUserId = userViewModel.getDeviceId();
        eventRepository.getEventsByOrganizer(currentUserId, events -> {
            if (events != null) {
                myEventsAdapter.updateEvents(events);
            } else {
                Toast.makeText(MyEventsView.this, "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

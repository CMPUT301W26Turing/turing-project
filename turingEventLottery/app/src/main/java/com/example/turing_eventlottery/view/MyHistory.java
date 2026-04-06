package com.example.turing_eventlottery.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.Event;
import com.example.turing_eventlottery.model.EventRepository;
import com.example.turing_eventlottery.model.User;
import com.example.turing_eventlottery.viewmodel.UserViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Activity to display the history of events an entrant has registered for.
 */
public class MyHistory extends AppCompatActivity {

    private RecyclerView myHistoryRecyclerView;
    private MyHistoryAdapter myHistoryAdapter;
    private TextView emptyMessage;
    private EventRepository eventRepository;
    private UserViewModel userViewModel;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy, HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.my_history);

        eventRepository = new EventRepository();
        userViewModel = new UserViewModel(this);

        ImageView backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        emptyMessage = findViewById(R.id.emptyMessage);
        myHistoryRecyclerView = findViewById(R.id.myHistoryRecyclerView);
        myHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        myHistoryAdapter = new MyHistoryAdapter(new ArrayList<>(), userViewModel.getDeviceId());
        myHistoryRecyclerView.setAdapter(myHistoryAdapter);
        
        loadMyHistory();
    }

    private void loadMyHistory() {
        userViewModel.loadUser(user -> {
            if (user != null) {
                List<String> associatedEventIds = user.getAssociatedEvents();
                if (associatedEventIds == null || associatedEventIds.isEmpty()) {
                    showEmptyState();
                } else {
                    fetchEvents(associatedEventIds, user.getUserId());
                }
            } else {
                Toast.makeText(this, "Error loading user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchEvents(List<String> eventIds, String userId) {
        List<Event> events = new ArrayList<>();
        AtomicInteger count = new AtomicInteger(0);
        int total = eventIds.size();

        for (String id : eventIds) {
            eventRepository.getEventById(id, event -> {
                if (event != null) {
                    events.add(event);
                }
                if (count.incrementAndGet() == total) {
                    sortAndDisplayEvents(events, userId);
                }
            });
        }
    }

    private void sortAndDisplayEvents(List<Event> events, String userId) {
        // Sort events by date descending (most recent first)
        Collections.sort(events, (e1, e2) -> {
            try {
                Date d1 = dateFormat.parse(e1.getDate());
                Date d2 = dateFormat.parse(e2.getDate());
                if (d1 == null || d2 == null) return 0;
                return d2.compareTo(d1);
            } catch (ParseException e) {
                return 0;
            }
        });

        if (events.isEmpty()) {
            showEmptyState();
        } else {
            emptyMessage.setVisibility(View.GONE);
            myHistoryRecyclerView.setVisibility(View.VISIBLE);
            myHistoryAdapter.updateEvents(events);
        }
    }

    private void showEmptyState() {
        emptyMessage.setVisibility(View.VISIBLE);
        myHistoryRecyclerView.setVisibility(View.GONE);
    }
}

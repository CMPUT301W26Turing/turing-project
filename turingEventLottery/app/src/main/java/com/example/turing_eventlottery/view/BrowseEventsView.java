package com.example.turing_eventlottery.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.Event;
import com.example.turing_eventlottery.model.EventRepository;
import com.example.turing_eventlottery.viewmodel.EventViewModel;
import com.example.turing_eventlottery.viewmodel.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * View class for browsing and managing events.
 * <p>
 *     Displays a browse view for all available events and a manage view for
 *     organizer-created events. Supports filtering via spinners and tab navigation.
 *     Includes bottom navigation and a floating action button for creating new events.
 * </p>
 */
public class BrowseEventsView extends AppCompatActivity {
    private View browseContainer;
    private LinearLayout eventsContainer;
    private TextView resultsText;
    private TextView upcomingLotteries;

    private View manageContainer;
    private RecyclerView myEventsRecyclerView;
    private MyEventsAdapter myEventsAdapter;
    
    private EventViewModel eventViewModel;
    private EventRepository eventRepository;
    private UserViewModel userViewModel;
    private boolean fromAdmin;
    private List<String> selectedAvailability = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.browse_events);

        eventViewModel = new EventViewModel();
        eventRepository = new EventRepository();
        userViewModel = new UserViewModel(this);
        fromAdmin = getIntent().getBooleanExtra("fromAdmin", false);

        initViews();
        setupSpinners();
        setupTabs();
        setupNavigation();
    }

    private void initViews() {
        browseContainer = findViewById(R.id.browseContainer);
        manageContainer = findViewById(R.id.manageContainer);

        eventsContainer = findViewById(R.id.eventsContainer);
        resultsText = findViewById(R.id.resultsText);
        upcomingLotteries = findViewById(R.id.upcomingText);

        myEventsRecyclerView = findViewById(R.id.myEventsRecyclerView);
        myEventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myEventsAdapter = new MyEventsAdapter(new ArrayList<>());
        myEventsRecyclerView.setAdapter(myEventsAdapter);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void setupSpinners() {
        Spinner availabilitySpinner = findViewById(R.id.browseEventsAvailabilitySpinner);

        ArrayAdapter<CharSequence> availabilityAdapter = ArrayAdapter.createFromResource(this,
                R.array.browse_events_availability_spinner, R.layout.custom_spinner_item);
        availabilityAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        availabilitySpinner.setAdapter(availabilityAdapter);

        availabilitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if (selected.equals("All")) {
                    selectedAvailability.clear(); // no filter
                } else {
                    selectedAvailability.clear();
                    selectedAvailability.add(selected);
                }
                loadFilteredEvents();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedAvailability.clear();
                loadFilteredEvents();
            }
        });
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        FloatingActionButton fabCreate = findViewById(R.id.fabCreate);

        bottomNav.setSelectedItemId(R.id.nav_events);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, UserDashboardView.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_events) {
                return true;
            } else if (itemId == R.id.nav_alerts) {
                startActivity(new Intent(this, MyNotificationsView.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, MyProfileView.class));
                finish();
                return true;
            }
            return false;
        });

        fabCreate.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateEventView.class);
            startActivity(intent);
        });
    }

    private void setupTabs() {
        TabLayout tabs = findViewById(R.id.dashboardTabs);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    showBrowseView();
                } else {
                    showManageView();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void showBrowseView() {
        browseContainer.setVisibility(View.VISIBLE);
        manageContainer.setVisibility(View.GONE);
        loadAllEvents();
    }

    private void showManageView() {
        browseContainer.setVisibility(View.GONE);
        manageContainer.setVisibility(View.VISIBLE);
        loadOrganizerEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data depending on selected tab
        TabLayout tabs = findViewById(R.id.dashboardTabs);
        if (tabs.getSelectedTabPosition() == 0) {
            loadAllEvents();
        } else {
            loadOrganizerEvents();
        }
    }

    private void loadAllEvents() {
        // Async call to get all events; displayEvents handles rendering
        eventViewModel.getEvents(this::displayEvents);
    }

    private void loadOrganizerEvents() {
        String currentUserId = userViewModel.getDeviceId();
        eventRepository.getEventsByOrganizer(currentUserId, events -> {
            if (events != null) {
                myEventsAdapter.updateEvents(events);
            } else {
                Toast.makeText(this, "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFilteredEvents() {
        // Get current user first
        userViewModel.loadUser(user -> {
            if (user != null) {
                eventViewModel.getFilteredEvents(user, selectedAvailability, this::displayEvents);
            }
        });
    }

    private void displayEvents(List<Event> events) {
        eventsContainer.removeAllViews();

        resultsText.setText(events.size() + " Results");
        upcomingLotteries.setText("Upcoming Lotteries");

        for (Event event : events) {
            MaterialCardView card = (MaterialCardView) LayoutInflater.from(this)
                    .inflate(R.layout.browse_events_card, eventsContainer, false);

            String posterUrl = event.getPosterUrl();
            ImageView posterView = card.findViewById(R.id.eventPoster);

            // If no poster URL, show placeholder background
            if (posterUrl == null || posterUrl.isEmpty()) {
                posterView.setBackgroundColor(getColor(R.color.secondaryClickableCardsAndSpinners));
                posterView.setImageDrawable(null);
            } else {
                Glide.with(this).load(posterUrl).into(posterView);
            }

            ((TextView) card.findViewById(R.id.locationText)).setText(event.getLocation());
            ((TextView) card.findViewById(R.id.eventName)).setText(event.getName());
            ((TextView) card.findViewById(R.id.dateTimeText)).setText(event.getDate());

            // Click card to open event details
            card.setOnClickListener(v -> openEventDetails(event.getId()));
            eventsContainer.addView(card);
        }
    }

    private void openEventDetails(String eventId) {
        Intent intent = new Intent(this, EventDetailsView.class);
        intent.putExtra("EVENT_ID", eventId);
        if (fromAdmin) {
            intent.putExtra("fromAdmin", true);
        }
        startActivity(intent);
    }
}

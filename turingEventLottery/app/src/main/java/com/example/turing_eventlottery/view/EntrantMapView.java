package com.example.turing_eventlottery.view;

import static android.content.Intent.getIntent;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.EventRepository;
import com.example.turing_eventlottery.model.UserRepository;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Map;

public class EntrantMapView extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String eventId;
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private TextView emptyStateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrant_map);

        eventId = getIntent().getStringExtra("EVENT_ID");
        eventRepository = new EventRepository();
        userRepository = new UserRepository();
        emptyStateText = findViewById(R.id.emptyStateText);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        loadEntrantLocations();
    }

    private void loadEntrantLocations() {
        if (eventId == null) return;

        eventRepository.getWaitlistEntrants(eventId, entrants -> {
            if (entrants == null || entrants.isEmpty()) {
                emptyStateText.setVisibility(View.VISIBLE);
                return;
            }

            boolean hasLocationData = false;
            for (Map<String, Object> entrant : entrants) {
                Double lat = (Double) entrant.get("latitude");
                Double lon = (Double) entrant.get("longitude");
                String userId = (String) entrant.get("userId");
                String username = (String) entrant.get("username");

                if (lat != null && lon != null) {
                    hasLocationData = true;
                    final LatLng location = new LatLng(lat, lon);
                    
                    // Verify user still exists
                    userRepository.getUser(userId, user -> {
                        if (user != null) {
                            mMap.addMarker(new MarkerOptions()
                                    .position(location)
                                    .title(username));
                        }
                    });
                }
            }

            if (!hasLocationData) {
                emptyStateText.setVisibility(View.VISIBLE);
            } else {
                emptyStateText.setVisibility(View.GONE);
                // Center map on event location if available
                eventRepository.getEventById(eventId, event -> {
                    if (event != null && event.isGeolocationRequired()) {
                        LatLng eventLoc = new LatLng(event.getLatitude(), event.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLoc, 10));
                        mMap.addMarker(new MarkerOptions()
                                .position(eventLoc)
                                .title("Event Location")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    }
                });
            }
        });
    }
}

package com.example.turing_eventlottery.view;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.Event;
import com.example.turing_eventlottery.model.EventRepository;
import com.example.turing_eventlottery.model.Notification;
import com.example.turing_eventlottery.model.NotificationRepository;
import com.example.turing_eventlottery.viewmodel.UserViewModel;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SendNotificationView extends AppCompatActivity {

    private AutoCompleteTextView eventDropdown;
    private MaterialCardView audienceWaiting, audienceSelected, audienceCancelled;
    private TextView audienceSummary, charCount;
    private EditText messageInput;
    private EventRepository eventRepository;
    private NotificationRepository notificationRepository;
    private UserViewModel userViewModel;
    
    private List<Event> organizerEvents = new ArrayList<>();
    private Event selectedEvent;
    private String selectedAudience = "Waiting List";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.new_notification);

        eventRepository = new EventRepository();
        notificationRepository = new NotificationRepository();
        userViewModel = new UserViewModel(this);

        initViews();
        loadOrganizerEvents();
    }

    private void initViews() {
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        
        eventDropdown = findViewById(R.id.eventDropdown);
        audienceWaiting = findViewById(R.id.audienceWaiting);
        audienceSelected = findViewById(R.id.audienceSelected);
        audienceCancelled = findViewById(R.id.audienceCancelled);
        audienceSummary = findViewById(R.id.audienceSummary);
        messageInput = findViewById(R.id.messageInput);
        charCount = findViewById(R.id.charCount);

        setupAudienceSelection();
        setupMessageInput();
        
        findViewById(R.id.sendButton).setOnClickListener(v -> sendNotification());
    }

    private void loadOrganizerEvents() {
        String currentUserId = userViewModel.getDeviceId();
        eventRepository.getEventsByOrganizer(currentUserId, events -> {
            if (events != null) {
                organizerEvents = events;
                List<String> eventNames = new ArrayList<>();
                for (Event e : events) eventNames.add(e.getName());
                
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                        android.R.layout.simple_dropdown_item_1line, eventNames);
                eventDropdown.setAdapter(adapter);
                
                eventDropdown.setOnItemClickListener((parent, view, position, id) -> {
                    selectedEvent = organizerEvents.get(position);
                    updateAudienceSummary();
                });
            }
        });
    }

    private void setupAudienceSelection() {
        View.OnClickListener listener = v -> {
            resetAudienceUI();
            MaterialCardView card = (MaterialCardView) v;
            card.setStrokeColor(getColor(R.color.primaryBlue));
            card.setStrokeWidth(4);
            
            if (v.getId() == R.id.audienceWaiting) selectedAudience = "Waiting List";
            else if (v.getId() == R.id.audienceSelected) selectedAudience = "Selected";
            else if (v.getId() == R.id.audienceCancelled) selectedAudience = "Cancelled";
            
            updateAudienceSummary();
        };

        audienceWaiting.setOnClickListener(listener);
        audienceSelected.setOnClickListener(listener);
        audienceCancelled.setOnClickListener(listener);
    }

    private void resetAudienceUI() {
        int gray = Color.parseColor("#EEEEEE");
        audienceWaiting.setStrokeColor(gray);
        audienceWaiting.setStrokeWidth(2);
        audienceSelected.setStrokeColor(gray);
        audienceSelected.setStrokeWidth(2);
        audienceCancelled.setStrokeColor(gray);
        audienceCancelled.setStrokeWidth(2);
    }

    private void updateAudienceSummary() {
        if (selectedEvent == null) {
            audienceSummary.setText("Select an event to see reach.");
            return;
        }
        
        String targetCollection = selectedAudience.equals("Waiting List") ? "waitlist" : 
                                 selectedAudience.equals("Selected") ? "participants list" : "cancelled list";
        
        audienceSummary.setText("This will reach all entrants in the " + targetCollection + " of " + selectedEvent.getName() + ".");
    }

    private void setupMessageInput() {
        messageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                charCount.setText(s.length() + "/500");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void sendNotification() {
        String message = messageInput.getText().toString().trim();
        if (selectedEvent == null) {
            Toast.makeText(this, "Please select an event", Toast.LENGTH_SHORT).show();
            return;
        }
        if (message.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedAudience.equals("Waiting List")) {
            eventRepository.getWaitlistEntrants(selectedEvent.getId(), entrants -> {
                if (entrants != null) {
                    processSending(entrants, message, "Announcement");
                }
            });
        } else {
        // need to complete for all selected audiences
        }
    }

    private void processSending(List<Map<String, Object>> users, String message, String status) {
        if (users == null || users.isEmpty()) {
            Toast.makeText(this, "No users in this list to notify.", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Map<String, Object> userData : users) {
            String userId = (String) userData.get("userId");
            if (userId != null) {
                Notification notification = new Notification(
                        userId,
                        selectedEvent.getId(),
                        selectedEvent.getName(),
                        selectedEvent.getDate(),
                        message,
                        status
                );
                notificationRepository.addNotification(notification);
            }
        }

        Toast.makeText(this, "Notification sent to " + users.size() + " users!", Toast.LENGTH_SHORT).show();
        finish();
    }
}

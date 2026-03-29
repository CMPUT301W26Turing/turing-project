package com.example.turing_eventlottery.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.turing_eventlottery.BuildConfig;
import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.Event;
import com.example.turing_eventlottery.model.EventRepository;
import com.example.turing_eventlottery.model.Notification;
import com.example.turing_eventlottery.model.NotificationRepository;
import com.example.turing_eventlottery.model.User;
import com.example.turing_eventlottery.viewmodel.UserViewModel;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

/**
 * Activity for creating new events (organizer).
 * <p>
 *     Allows an organizer to enter event details, pick date/times, upload poster image,
 *     and publish the event. Validates input fields and handles asynchronous poster upload
 *     before saving the event.
 * </p>
 */

/*
Currently, this class does not follow proper MVVM architecture and fill be fixed for part 4.
(Repository -> Model) -> ViewModel -> View.
View should not know anything about the Repository. (Should not directly access EventRepository)
 */
public class CreateEventView extends AppCompatActivity {

    private static final String TAG = "CreateEventView";

    private TextInputEditText eventDateInput;
    private TextInputEditText regStartInput;
    private TextInputEditText regEndInput;

    private MaterialCardView uploadPosterCard;
    private ImageView eventPosterPreview;
    private LinearLayout uploadPlaceholder;
    
    private TextInputEditText eventNameInput;
    private TextInputEditText eventDescriptionInput;
    private TextInputEditText eventCategoryInput;
    private String selectedLocationAddress;
    private EditText winnersToDrawInput;
    private EditText waitlistCapInput;
    private MaterialSwitch geoSwitch;
    private MaterialButton publishButton;
    private AutoCompleteTextView coOrganizerInput;
    private User selectedCoOrganizer;

    private EventRepository eventRepository;
    private UserViewModel userViewModel;
    private NotificationRepository notificationRepository;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        uploadPlaceholder.setVisibility(View.GONE);
                        eventPosterPreview.setVisibility(View.VISIBLE);
                        Glide.with(this).load(selectedImageUri).into(eventPosterPreview);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.create_event);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
        }

        eventRepository = new EventRepository();
        userViewModel = new UserViewModel(this);
        notificationRepository = new NotificationRepository();

        ImageView closeButton = findViewById(R.id.closeButton);
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> finish());
        }

        eventDateInput = findViewById(R.id.eventDateInput);
        regStartInput = findViewById(R.id.regStartInput);
        regEndInput = findViewById(R.id.regEndInput);
        
        uploadPosterCard = findViewById(R.id.uploadPosterCard);
        eventPosterPreview = findViewById(R.id.eventPosterPreview);
        uploadPlaceholder = findViewById(R.id.uploadPlaceholder);
        
        eventNameInput = findViewById(R.id.eventNameInput);
        eventDescriptionInput = findViewById(R.id.eventDescriptionInput);
        eventCategoryInput = findViewById(R.id.eventCategoryInput);
        winnersToDrawInput = findViewById(R.id.winnersToDrawInput);
        waitlistCapInput = findViewById(R.id.waitlistCapInput);
        geoSwitch = findViewById(R.id.geoSwitch);
        publishButton = findViewById(R.id.publishButton);
        coOrganizerInput = findViewById(R.id.autoCompleteTextView);

        setupDateTimePicker(eventDateInput);
        setupDateTimePicker(regStartInput);
        setupDateTimePicker(regEndInput);
        setupPlacesAutocomplete();
        setupUserAutocomplete();

        uploadPosterCard.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        userViewModel.loadUser(user -> {
            if (user != null && user.isBanned()) {
                Toast.makeText(this, "Your organizer account has been suspended. You cannot create events.", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        if (publishButton != null) {
            publishButton.setOnClickListener(v -> saveEvent());
        }
    }

    private void setupUserAutocomplete() {
        if (coOrganizerInput != null) {
            UserAutocompleteAdapter adapter = new UserAutocompleteAdapter(this, userViewModel);
            coOrganizerInput.setAdapter(adapter);
            coOrganizerInput.setThreshold(1); // Start searching after 1 character

            coOrganizerInput.setOnItemClickListener((parent, view, position, id) -> {
                selectedCoOrganizer = (User) parent.getItemAtPosition(position);
                coOrganizerInput.setText(selectedCoOrganizer.getUserName());
                Log.d(TAG, "Selected co-organizer: " + selectedCoOrganizer.getUserName() + " (ID: " + selectedCoOrganizer.getUserId() + ")");
            });
        }
    }

    private void setupPlacesAutocomplete() {
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.DISPLAY_NAME, Place.Field.FORMATTED_ADDRESS));
            autocompleteFragment.setHint("Location");
            View view = autocompleteFragment.getView();
            if (view != null) {
                Log.d(TAG, "autoCompView: FOUND");
                view.setPadding(0, 0, 0, 0);
                EditText searchInput = view.findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_input);
                if (searchInput != null) {
                    Log.d(TAG, "searchInput: FOUND");
                    searchInput.setTextSize(14f);
                    searchInput.setPadding(0, 0, 0, 0);
                    searchInput.setHintTextColor(getResources().getColor(R.color.headerSubtext, getTheme()));
                }
                else {
                    Log.d(TAG, "COULD NOT FIND SEARCH INPUT");
                }
                View searchButton = view.findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_button);
                if (searchButton != null) {
                    searchButton.setScaleX(0.7f);
                    searchButton.setScaleY(0.7f);
                }
                else
                {
                    Log.d(TAG, "COULD NOT FIND SEARCH BUTTON");
                }
            }
            else
            {
                Log.d(TAG, "COULD NOT FIND AUTOCOMPLETEFRAGMENT VIEW");
            }

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    selectedLocationAddress = place.getFormattedAddress();
                }

                @Override
                public void onError(@NonNull Status status) {
                    Log.e(TAG, "An error occurred: " + status);
                }
            });
        }
    }

    private void saveEvent() {
        String name = eventNameInput.getText().toString().trim();
        String description = eventDescriptionInput.getText().toString().trim();
        String category = eventCategoryInput.getText().toString().trim();
        String location = selectedLocationAddress;
        String dateStr = eventDateInput.getText().toString().trim();
        String regStart = regStartInput.getText().toString().trim();
        String regEnd = regEndInput.getText().toString().trim();
        String winnersStr = winnersToDrawInput.getText().toString().trim();
        String capStr = waitlistCapInput.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter an event name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (location == null || location.isEmpty()) {
            Toast.makeText(this, "Please select a location", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prevent adding oneself as co-organizer
        if (selectedCoOrganizer != null && selectedCoOrganizer.getUserId().equals(userViewModel.getDeviceId())) {
            Toast.makeText(this, "You cannot be a co-organizer of your own event.", Toast.LENGTH_SHORT).show();
            return;
        }

        publishButton.setEnabled(false);
        if (selectedImageUri != null) {
            publishButton.setText("Uploading...");
            eventRepository.uploadEventPoster(selectedImageUri, url -> {
                if (url != null) {
                    completeSaveEvent(name, description, category, location, dateStr, regStart, regEnd, winnersStr, capStr, url);
                } else {
                    publishButton.setEnabled(true);
                    publishButton.setText("Publish Event");
                    Toast.makeText(this, "Failed to upload poster", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            completeSaveEvent(name, description, category, location, dateStr, regStart, regEnd, winnersStr, capStr, null);
        }
    }

    private void completeSaveEvent(String name, String description, String category, String location, 
                                   String dateStr, String regStart, String regEnd, String winnersStr, 
                                   String capStr, String posterUrl) {
        Event newEvent = new Event();
        newEvent.setName(name);
        newEvent.setDescription(description);
        newEvent.setCategory(category);
        newEvent.setLocation(location);
        newEvent.setDate(dateStr);
        newEvent.setRegStart(regStart);
        newEvent.setRegEnd(regEnd);
        newEvent.setPosterUrl(posterUrl);
        newEvent.setOrganizerId(userViewModel.getDeviceId());

        if (selectedCoOrganizer != null) {
            newEvent.setCoOrganizerId(selectedCoOrganizer.getUserId());
        }
        
        try {
            if (winnersStr.isEmpty() || Integer.parseInt(winnersStr) <= 0) {
                Toast.makeText(this, "Invalid number of winners", Toast.LENGTH_SHORT).show();
                publishButton.setEnabled(true);
                return;
            }
            newEvent.setWinnersToDraw(Integer.parseInt(winnersStr));
            newEvent.setWaitlistCap(capStr.isEmpty() ? 0 : Integer.parseInt(capStr));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
            publishButton.setEnabled(true);
            return;
        }
        
        newEvent.setGeolocationRequired(geoSwitch.isChecked());

        eventRepository.addEvent(newEvent, success -> {
            if (success) {
                // Send a notification to the co-organizer
                if (selectedCoOrganizer != null) {
                    Notification inviteNotification = new Notification(
                        selectedCoOrganizer.getUserId(),
                        newEvent.getId(),
                        newEvent.getName(),
                        newEvent.getDate(),
                        "You have been added as a co-organizer for the event: " + newEvent.getName(),
                        "Unread"
                    );
                    notificationRepository.addNotification(inviteNotification);
                }

                userViewModel.setOrganizerStatus(result -> {
                    Toast.makeText(CreateEventView.this, "Event published successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } else {
                publishButton.setEnabled(true);
                publishButton.setText("Publish Event");
                Toast.makeText(CreateEventView.this, "Failed to publish event", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDateTimePicker(TextInputEditText editText) {
        editText.setFocusable(false);
        editText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view1, hourOfDay, minute1) -> {
                    String dateTime = String.format(Locale.getDefault(), "%02d/%02d/%d, %02d:%02d",
                            monthOfYear + 1, dayOfMonth, year1, hourOfDay, minute1);
                    editText.setText(dateTime);
                }, hour, minute, true);
                timePickerDialog.show();
            }, year, month, day);
            datePickerDialog.show();
        });
    }
}

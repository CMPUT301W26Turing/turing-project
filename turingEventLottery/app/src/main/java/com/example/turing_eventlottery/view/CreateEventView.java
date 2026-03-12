package com.example.turing_eventlottery.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;


import com.bumptech.glide.Glide;
import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.Event;
import com.example.turing_eventlottery.model.EventRepository;
import com.example.turing_eventlottery.viewmodel.UserViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

public class CreateEventView extends AppCompatActivity {

    private TextInputEditText eventDateInput;
    private TextInputEditText regStartInput;
    private TextInputEditText regEndInput;

    private MaterialCardView uploadPosterCard;
    private ImageView eventPosterPreview;
    private LinearLayout uploadPlaceholder;
    
    private TextInputEditText eventNameInput;
    private TextInputEditText eventDescriptionInput;
    private TextInputEditText eventCategoryInput;
    private TextInputEditText eventLocationInput;
    private EditText winnersToDrawInput;
    private EditText waitlistCapInput;
    private MaterialSwitch geoSwitch;
    private MaterialButton publishButton;

    private EventRepository eventRepository;
    private UserViewModel userViewModel;
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

        eventRepository = new EventRepository();
        userViewModel = new UserViewModel(this);

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
        eventLocationInput = findViewById(R.id.eventLocationInput);
        winnersToDrawInput = findViewById(R.id.winnersToDrawInput);
        waitlistCapInput = findViewById(R.id.waitlistCapInput);
        geoSwitch = findViewById(R.id.geoSwitch);
        publishButton = findViewById(R.id.publishButton);


        setupDateTimePicker(eventDateInput);
        setupDateTimePicker(regStartInput);
        setupDateTimePicker(regEndInput);
        
        uploadPosterCard.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        userViewModel.loadUser(user -> {
            if (user != null && user.isBanned()) {
                Toast.makeText(this, "Your organizer account has been suspended. You cannot create events.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        });

        if (publishButton != null) {
            publishButton.setOnClickListener(v -> {
                saveEvent();
            });
        }
    }

    private void saveEvent() {
        String name = eventNameInput.getText().toString().trim();
        String description = eventDescriptionInput.getText().toString().trim();
        String category = eventCategoryInput.getText().toString().trim();
        String location = eventLocationInput.getText().toString().trim();
        String dateStr = eventDateInput.getText().toString().trim();
        String regStart = regStartInput.getText().toString().trim();
        String regEnd = regEndInput.getText().toString().trim();
        String winnersStr = winnersToDrawInput.getText().toString().trim();
        String capStr = waitlistCapInput.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter an event name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri != null) {
            publishButton.setEnabled(false);
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
        
        try {
            if (winnersStr.isEmpty() || Integer.parseInt(winnersStr) <= 0) {
                Toast.makeText(this, "Invalid number of winners", Toast.LENGTH_SHORT).show();
                return;
            }
            newEvent.setWinnersToDraw(Integer.parseInt(winnersStr));
            newEvent.setWaitlistCap(capStr.isEmpty() ? 0 : Integer.parseInt(capStr));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
            return;
        }
        
        newEvent.setGeolocationRequired(geoSwitch.isChecked());

        eventRepository.addEvent(newEvent, success -> {
            if (success) {
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

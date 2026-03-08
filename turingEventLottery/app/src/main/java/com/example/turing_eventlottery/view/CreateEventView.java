package com.example.turing_eventlottery.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

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

        Event newEvent = new Event();
        newEvent.setName(name);
        newEvent.setDescription(description);
        newEvent.setCategory(category);
        newEvent.setLocation(location);
        newEvent.setDate(dateStr);
        newEvent.setRegStart(regStart);
        newEvent.setRegEnd(regEnd);
        newEvent.setOrganizerId(userViewModel.getDeviceId());
        
        try {
            newEvent.setWinnersToDraw(winnersStr.isEmpty() ? 0 : Integer.parseInt(winnersStr));
            newEvent.setWaitlistCap(capStr.isEmpty() ? 0 : Integer.parseInt(capStr));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
            return;
        }
        
        newEvent.setGeolocationRequired(geoSwitch.isChecked());

        eventRepository.addEvent(newEvent, success -> {
            if (success) {
                Toast.makeText(CreateEventView.this, "Event published successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
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

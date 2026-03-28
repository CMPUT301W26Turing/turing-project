package com.example.turing_eventlottery.view;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.Event;
import com.example.turing_eventlottery.model.EventRepository;
import com.example.turing_eventlottery.model.QRCodeModel;
import com.google.android.material.button.MaterialButton;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Activity for editing an existing event.
 */
public class EditEventView extends AppCompatActivity {

    private String eventId;
    private Event currentEvent;
    private EventRepository eventRepository;
    private Uri selectedImageUri;

    private EditText eventNameInput, eventDescriptionInput;
    private TextView startDateText, endDateText, drawDateText, maxEntrantsValue;
    private ImageView eventPosterImage;

    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    eventPosterImage.setImageURI(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.edit_event);

        eventId = getIntent().getStringExtra("EVENT_ID");
        eventRepository = new EventRepository();

        initViews();
        loadEventData();
    }

    private void initViews() {
        eventNameInput = findViewById(R.id.eventNameInput);
        eventDescriptionInput = findViewById(R.id.eventDescriptionInput);
        startDateText = findViewById(R.id.startDateText);
        endDateText = findViewById(R.id.endDateText);
        drawDateText = findViewById(R.id.drawDateText);
        maxEntrantsValue = findViewById(R.id.maxEntrantsValue);
        eventPosterImage = findViewById(R.id.eventPosterImage);

        findViewById(R.id.cancelButton).setOnClickListener(v -> finish());
        findViewById(R.id.saveButton).setOnClickListener(v -> saveChanges());
        findViewById(R.id.posterCard).setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        findViewById(R.id.incrementButton).setOnClickListener(v -> {
            int current = Integer.parseInt(maxEntrantsValue.getText().toString());
            maxEntrantsValue.setText(String.valueOf(current + 1));
        });

        findViewById(R.id.decrementButton).setOnClickListener(v -> {
            int current = Integer.parseInt(maxEntrantsValue.getText().toString());
            if (current > 0) maxEntrantsValue.setText(String.valueOf(current - 1));
        });

        startDateText.setOnClickListener(v -> showDateTimePicker(startDateText));
        endDateText.setOnClickListener(v -> showDateTimePicker(endDateText));
        drawDateText.setOnClickListener(v -> showDateTimePicker(drawDateText));

        findViewById(R.id.deleteEventText).setOnClickListener(v -> showDeleteConfirmation());
        
        findViewById(R.id.manageQrCard).setOnClickListener(v -> {
            if (eventId != null) {
                showQRCodeDialog(eventId);
            }
        });
    }

    private void loadEventData() {
        if (eventId == null) return;

        eventRepository.getEventById(eventId, event -> {
            if (event != null) {
                currentEvent = event;
                eventNameInput.setText(event.getName());
                eventDescriptionInput.setText(event.getDescription());
                startDateText.setText(event.getRegStart());
                endDateText.setText(event.getRegEnd());
                drawDateText.setText(event.getDate());
                maxEntrantsValue.setText(String.valueOf(event.getWaitlistCap()));

                if (event.getPosterUrl() != null && !event.getPosterUrl().isEmpty()) {
                    Glide.with(this).load(event.getPosterUrl()).into(eventPosterImage);
                }
            }
        });
    }

    private void showDateTimePicker(TextView targetView) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy, HH:mm", Locale.getDefault());
                targetView.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveChanges() {
        if (currentEvent == null) return;

        currentEvent.setName(eventNameInput.getText().toString());
        currentEvent.setDescription(eventDescriptionInput.getText().toString());
        currentEvent.setRegStart(startDateText.getText().toString());
        currentEvent.setRegEnd(endDateText.getText().toString());
        currentEvent.setDate(drawDateText.getText().toString());
        currentEvent.setWaitlistCap(Integer.parseInt(maxEntrantsValue.getText().toString()));

        if (selectedImageUri != null) {
            eventRepository.uploadEventPoster(selectedImageUri, url -> {
                if (url != null) {
                    currentEvent.setPosterUrl(url);
                }
                updateEventInDb();
            });
        } else {
            updateEventInDb();
        }
    }

    private void updateEventInDb() {
        eventRepository.updateEvent(currentEvent, success -> {
            if (success) {
                Toast.makeText(this, "Event updated!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    eventRepository.deleteEvent(eventId, success -> {
                        if (success) {
                            Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showQRCodeDialog(String eventId) {
        try {
            Bitmap qrBitmap = QRCodeModel.generateQRCode(eventId, 500, 500);

            Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_qr_code);
            
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            ImageView qrImageView = dialog.findViewById(R.id.qrCodeImageView);
            qrImageView.setImageBitmap(qrBitmap);

            MaterialButton saveButton = dialog.findViewById(R.id.saveQrButton);
            if (saveButton != null) {
                saveButton.setOnClickListener(v -> saveBitmapToGallery(qrBitmap, "QR_" + eventId));
            }

            MaterialButton closeButton = dialog.findViewById(R.id.closeDialogButton);
            if (closeButton != null) {
                closeButton.setOnClickListener(v -> dialog.dismiss());
            }

            dialog.show();
        } catch (Exception e) {
            Toast.makeText(this, "Error generating QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveBitmapToGallery(Bitmap bitmap, String filename) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename + ".png");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/TuringEvents");

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uri != null) {
            try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                Toast.makeText(this, "QR Code saved to gallery!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

package com.example.turing_eventlottery.view;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
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
import com.example.turing_eventlottery.model.QRCodeModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class ManageEventView extends AppCompatActivity {

    private String eventId;
    private EventRepository eventRepository;
    
    private TextView eventName, eventDateTime, capacityValue, spotsRemaining;
    private LinearProgressIndicator capacityProgress;
    private MaterialSwitch geoSwitch;
    private TabLayout statusTabs;
    private RecyclerView entrantsRecyclerView;
    private MaterialButton runLotteryButton;
    private ImageView exportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.manage_event);

        eventId = getIntent().getStringExtra("EVENT_ID");
        eventRepository = new EventRepository();

        initViews();
        loadEventDetails();
    }

    private void initViews() {
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        
        eventName = findViewById(R.id.eventName);
        eventDateTime = findViewById(R.id.eventDateTime);
        capacityValue = findViewById(R.id.capacityValue);
        spotsRemaining = findViewById(R.id.spotsRemaining);
        capacityProgress = findViewById(R.id.capacityProgress);
        geoSwitch = findViewById(R.id.geoSwitch);
        statusTabs = findViewById(R.id.statusTabs);
        entrantsRecyclerView = findViewById(R.id.entrantsRecyclerView);
        runLotteryButton = findViewById(R.id.runLotteryButton);
        exportButton = findViewById(R.id.exportButton);

        if (exportButton != null) {
            exportButton.setOnClickListener(v -> {
                if (eventId != null) {
                    showQRCode(eventId);
                }
            });
        }

        entrantsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadEventDetails() {
        if (eventId == null) return;

        eventRepository.getEventById(eventId, event -> {
            if (event != null) {
                displayEvent(event);
            } else {
                Toast.makeText(this, "Error loading event", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayEvent(Event event) {
        eventName.setText(event.getName());
        eventDateTime.setText(event.getDate());
        
        int currentWaitlist = event.getWaitlist() != null ? event.getWaitlist().size() : 0;
        int cap = event.getWaitlistCap();
        
        capacityValue.setText(currentWaitlist + "/" + cap);
        spotsRemaining.setText((cap - currentWaitlist) + " spots remaining");
        
        if (cap > 0) {
            capacityProgress.setProgress((currentWaitlist * 100) / cap);
        }
        
        geoSwitch.setChecked(event.isGeolocationRequired());
        
        runLotteryButton.setText("Run Lottery (" + event.getWinnersToDraw() + ")");
        
        // Update tab counts
        statusTabs.getTabAt(0).setText("Waiting " + currentWaitlist);
        int enrolled = event.getParticipants() != null ? event.getParticipants().size() : 0;
        statusTabs.getTabAt(2).setText("Enrolled " + enrolled);
    }

    private void showQRCode(String eventId) {
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

            MaterialButton closeButton = dialog.findViewById(R.id.closeDialogButton);
            if (closeButton != null) {
                closeButton.setOnClickListener(v -> dialog.dismiss());
            }

            dialog.show();
        } catch (Exception e) {
            Toast.makeText(this, "Error generating QR Code", Toast.LENGTH_SHORT).show();
        }
    }
}

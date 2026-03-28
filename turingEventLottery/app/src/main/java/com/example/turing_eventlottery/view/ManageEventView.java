package com.example.turing_eventlottery.view;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.Event;
import com.example.turing_eventlottery.model.EventRepository;
import com.example.turing_eventlottery.model.Notification;
import com.example.turing_eventlottery.model.NotificationRepository;
import com.example.turing_eventlottery.model.QRCodeModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Activity for managing a single event.
 * <p>
 *     Allows organizers to view event details, see waitlist and status,
 *     run lotteries or invite single users, sort entrants by name, or registration date, and
 *     export QR code for entrant registration.
 * </p>
 */
/*
Currently this view class handles business logic, but the ViewModel should handle that instead. Will be fixed for part 4.
Also accesses repositories directly, will be fixed for part 4.
 */
public class ManageEventView extends AppCompatActivity implements WaitingEntrantsAdapter.OnEntrantActionListener {

    private String eventId;
    private EventRepository eventRepository;
    private NotificationRepository notificationRepository;

    private TextView eventName, eventDateTime, capacityValue, spotsRemaining, sortText;
    private TextView emptyStateText, listTitle;
    private LinearProgressIndicator capacityProgress;
    private MaterialSwitch geoSwitch;
    private TabLayout statusTabs;
    private RecyclerView entrantsRecyclerView;
    private MaterialButton runLotteryButton, drawSingleButton;
    private WaitingEntrantsAdapter entrantsAdapter;

    private List<Map<String, Object>> waitingList = new ArrayList<>();
    private List<Map<String, Object>> invitedList = new ArrayList<>();
    private List<Map<String, Object>> enrolledList = new ArrayList<>();
    private List<Map<String, Object>> cancelledList = new ArrayList<>();

    private ImageView exportButton, editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.manage_event);

        eventId = getIntent().getStringExtra("EVENT_ID");
        eventRepository = new EventRepository();
        notificationRepository = new NotificationRepository();

        initViews();
        loadEventDetails();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEventDetails(); // Reload data when returning from Edit
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
        drawSingleButton = findViewById(R.id.drawSingleButton);
        exportButton = findViewById(R.id.exportButton);
        editButton = findViewById(R.id.editButton);
        sortText = findViewById(R.id.sortText);
        emptyStateText = findViewById(R.id.emptyStateText);
        listTitle = findViewById(R.id.listTitle);

        if (exportButton != null) {
            exportButton.setOnClickListener(v -> {
                if (eventId != null) {
                    showQRCode(eventId);
                }
            });
        }

        if (editButton != null) {
            editButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, EditEventView.class);
                intent.putExtra("EVENT_ID", eventId);
                startActivity(intent);
            });
        }

        findViewById(R.id.editEventIcon).setOnClickListener(v -> {
            Intent intent = new Intent(this, EditEventView.class);
            intent.putExtra("EVENT_ID", eventId);
            startActivity(intent);
        });

        runLotteryButton.setOnClickListener(v -> runLottery());
        drawSingleButton.setOnClickListener(v -> drawSingle());
        if (sortText != null) {
            sortText.setOnClickListener(v -> showSortMenu());
        }

        entrantsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        entrantsAdapter = new WaitingEntrantsAdapter(new ArrayList<>());
        entrantsAdapter.setOnEntrantActionListener(this);
        entrantsRecyclerView.setAdapter(entrantsAdapter);

        statusTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateListForTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadEventDetails() {
        if (eventId == null) return;

        eventRepository.getEventById(eventId, event -> {
            if (event != null) {
                loadAllParticipants();
                displayEvent(event);
            } else {
                Toast.makeText(this, "Error loading event", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayEvent(Event event) {
        eventName.setText(event.getName());
        eventDateTime.setText(event.getDate());
        geoSwitch.setChecked(event.isGeolocationRequired());
        int possibleSpots = event.getWinnersToDraw() - enrolledList.size() - invitedList.size();
        Log.d("ManageEventView", "enrolledList.size(): " + enrolledList.size());
        Log.d("ManageEventView", "invitedList.size(): " + invitedList.size());
        Log.d("ManageEventView", "Possible spots: " + possibleSpots);
        runLotteryButton.setText("Run Lottery (" + possibleSpots + ")");
    }

    private void loadAllParticipants() {
        if (eventId == null) return;

        eventRepository.getWaitlistEntrants(eventId, entrants -> {
            waitingList.clear();
            invitedList.clear();
            cancelledList.clear();
            if (entrants != null) {
                for (Map<String, Object> entrant : entrants) {
                    String status = (String) entrant.get("status");
                    if ("Invited".equals(status)) {
                        invitedList.add(entrant);
                    } else if ("Cancelled".equals(status)) {
                        cancelledList.add(entrant);
                    } else {
                        waitingList.add(entrant);
                    }
                }
            }

            eventRepository.getParticipants(eventId, participants -> {
                enrolledList.clear();
                if (participants != null) {
                    enrolledList.addAll(participants);
                }

                updateUIWithData();
            });
        });
    }

    private void updateUIWithData() {
        statusTabs.getTabAt(0).setText("Waiting " + waitingList.size());
        statusTabs.getTabAt(1).setText("Invited " + invitedList.size());
        statusTabs.getTabAt(2).setText("Enrolled " + enrolledList.size());
        statusTabs.getTabAt(3).setText("Cancelled " + cancelledList.size());

        eventRepository.getEventById(eventId, event -> {
            if (event != null) {
                int cap = event.getWaitlistCap();
                int totalWaitlist = waitingList.size() + invitedList.size();

                if (cap != 0) {
                    capacityValue.setText(totalWaitlist + "/" + cap);
                    spotsRemaining.setText(Math.max(0, cap - totalWaitlist) + " spots remaining");
                    if (cap > 0) {
                        capacityProgress.setProgress(Math.min(100, (totalWaitlist * 100) / cap));
                    }
                } else {
                    capacityValue.setText(String.valueOf(totalWaitlist));// was(capacityValue.setText(totalWaitlist))
                    capacityProgress.setProgress(0);
                    spotsRemaining.setText("N/A");
                }
                displayEvent(event);
            }
        });

        updateListForTab(statusTabs.getSelectedTabPosition());
    }

    private void updateListForTab(int position) {
        List<Map<String, Object>> dataToDisplay;
        String emptyMsg;
        String title;

        switch (position) {
            case 1:
                dataToDisplay = invitedList;
                emptyMsg = "No entrants invited";
                title = "INVITED LIST";
                break;
            case 2:
                dataToDisplay = enrolledList;
                emptyMsg = "No entrants enrolled";
                title = "ENROLLED LIST";
                break;
            case 3:
                dataToDisplay = cancelledList;
                emptyMsg = "No entrants cancelled";
                title = "CANCELLED LIST";
                break;
            case 0:
            default:
                dataToDisplay = waitingList;
                emptyMsg = "No entrants in the waitlist";
                title = "WAITING LIST";
                break;
        }

        listTitle.setText(title);
        entrantsAdapter.updateEntrants(new ArrayList<>(dataToDisplay));

        if (dataToDisplay.isEmpty()) {
            emptyStateText.setText(emptyMsg);
            emptyStateText.setVisibility(View.VISIBLE);
            entrantsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateText.setVisibility(View.GONE);
            entrantsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showSortMenu() {
        PopupMenu popup = new PopupMenu(this, sortText);
        popup.getMenu().add("Name (A-Z)");
        popup.getMenu().add("Date Applied (Newest first)");

        popup.setOnMenuItemClickListener(item -> {
            if (item.getTitle().equals("Name (A-Z)")) {
                sortCurrentList(true);
            } else {
                sortCurrentList(false);
            }
            return true;
        });
        popup.show();
    }

    private void sortCurrentList(boolean byName) {
        int position = statusTabs.getSelectedTabPosition();
        List<Map<String, Object>> listToSort;
        switch (position) {
            case 1: listToSort = invitedList; break;
            case 2: listToSort = enrolledList; break;
            case 3: listToSort = cancelledList; break;
            default: listToSort = waitingList; break;
        }

        if (listToSort == null || listToSort.isEmpty()) return;

        if (byName) {
            Collections.sort(listToSort, (a, b) -> {
                String nameA = (String) a.get("username");
                String nameB = (String) b.get("username");
                if (nameA == null) nameA = "";
                if (nameB == null) nameB = "";
                return nameA.compareToIgnoreCase(nameB);
            });
        } else {
            Collections.sort(listToSort, (a, b) -> {
                Timestamp tsA = (Timestamp) a.get("timestamp");
                Timestamp tsB = (Timestamp) b.get("timestamp");
                if (tsA == null || tsB == null) return 0;
                return tsB.compareTo(tsA); // Descending
            });
        }
        entrantsAdapter.updateEntrants(new ArrayList<>(listToSort));
    }

    @Override
    public void onCancelInvitation(String userId) {
        eventRepository.updateWaitlistStatus(eventId, userId, "Cancelled", success -> {
            if (success) {
                Toast.makeText(this, "Invitation cancelled", Toast.LENGTH_SHORT).show();

                NotificationRepository notificationRepository = getNotificationRepositoryFromUserId(userId);

                notificationRepository.getNotificationsByUserId(userId, notifications -> {
                    if (notifications != null && !notifications.isEmpty()) {
                        for (Notification notification : notifications)
                            if (notification.getEventId().equals(eventId) && notification.getStatus().equals("Invited"))
                                notificationRepository.removeNotification(notification);
                    }
                });

                loadAllParticipants();
            } else {
                Toast.makeText(this, "Failed to cancel invitation", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    private NotificationRepository getNotificationRepositoryFromUserId(String userId) {
        EventRepository eventRepository = new EventRepository();
        NotificationRepository notificationRepository = new NotificationRepository();

        eventRepository.getEventById(eventId, event -> {
            if (event == null) return;
            String eventName = event.getName();
            String eventDateTime = event.getDate();
            notificationRepository.addNotification(new Notification(
                    userId,
                    eventId,
                    eventName,
                    eventDateTime,
                    "Cancelled"
            ));
        });
        return notificationRepository;
    }


    private void runLottery() {
        eventRepository.getEventById(eventId, event -> {
            if (event == null) return;
            if (waitingList.isEmpty()) {
                Toast.makeText(this, "No users in waiting list", Toast.LENGTH_SHORT).show();
                return;
            }

            int numToDraw = Math.min(event.getWinnersToDraw(), waitingList.size());

            int numEnrolled = enrolledList.size();
            int numInvited = invitedList.size();

            if ((numToDraw - numEnrolled - numInvited) <= 0) {
                Toast.makeText(this, "No more spots for event", Toast.LENGTH_SHORT).show();
                return;
            }

            List<Map<String, Object>> pool = new ArrayList<>(waitingList);
            Collections.shuffle(pool);
            List<Map<String, Object>> winners = pool.subList(0, numToDraw);

            for (Map<String, Object> winner : winners) {
                String userId = (String) winner.get("userId");
                notifyWinner(userId, event);
            }

            // users who were NOT selected (US 01.04.02)
            for (Map<String, Object> entrant : waitingList) {

                String userId = (String) entrant.get("userId");

                boolean isWinner = false;

                for (Map<String, Object> winner : winners) {
                    if (winner.get("userId").equals(userId)) {
                        isWinner = true;
                        break;
                    }
                }

                // notify losers
                if (!isWinner) {

                    Notification notification = new Notification(
                            userId,
                            event.getId(),
                            event.getName(),
                            event.getDate(),
                            "Not Selected"
                    );

                    notificationRepository.addNotification(notification);
                }
            }

            Toast.makeText(this, "Lottery completed. " + numToDraw + " entrants invited.", Toast.LENGTH_SHORT).show();
            loadAllParticipants();
        });

    }

    private void drawSingle() {
        eventRepository.getEventById(eventId, event -> {
            if (event == null) return;
            if (waitingList.isEmpty()) {
                Toast.makeText(this, "No users in waiting list", Toast.LENGTH_SHORT).show();
                return;
            }

            int numParticipants = enrolledList.size();
            int numInvited = invitedList.size();

            if (numParticipants + numInvited >= event.getWinnersToDraw()) {
                Toast.makeText(this, "No more spots for event; cancel an invitation or wait for users to enroll", Toast.LENGTH_SHORT).show();
                return;
            }

            Collections.shuffle(waitingList);
            String winnerId = (String) waitingList.get(0).get("userId");
            notifyWinner(winnerId, event);
            // check if lottery is finished,if yes notify loser(US 01.04.02)
            if (invitedList.size() + 1 >= event.getWinnersToDraw()) {

                for (Map<String, Object> entrant : waitingList) {

                    String entrantId = (String) entrant.get("userId");

                    if (!entrantId.equals(winnerId)) {

                        Notification notification = new Notification(
                                entrantId,
                                event.getId(),
                                event.getName(),
                                event.getDate(),
                                "Not Selected"
                        );

                        notificationRepository.addNotification(notification);
                    }
                }
            }



            Toast.makeText(this, "One entrant invited.", Toast.LENGTH_SHORT).show();
            loadAllParticipants();
        });
    }

    private void notifyWinner(String userId, Event event) {
        eventRepository.updateWaitlistStatus(event.getId(), userId, "Invited", success -> {
            if (success) {
                Notification notification = new Notification(
                        userId,
                        event.getId(),
                        event.getName(),
                        event.getDate(),
                        "Invited"
                );
                notificationRepository.addNotification(notification);
            }
        });
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

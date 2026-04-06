package com.example.turing_eventlottery.view;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.Event;
import com.example.turing_eventlottery.model.EventRepository;
import com.example.turing_eventlottery.model.Notification;
import com.example.turing_eventlottery.model.NotificationRepository;
import com.example.turing_eventlottery.model.QRCodeModel;
import com.example.turing_eventlottery.model.User;
import com.example.turing_eventlottery.viewmodel.EventViewModel;
import com.example.turing_eventlottery.utility.ExportCSV;
import com.example.turing_eventlottery.viewmodel.UserViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
    private Event thisEvent;
    private EventRepository eventRepository;
    private EventViewModel eventViewModel;
    private UserViewModel userViewModel;
    private NotificationRepository notificationRepository;

    private TextView eventName, eventDateTime, capacityValue, spotsRemaining, sortText;
    private TextView emptyStateText, listTitle;
    private LinearProgressIndicator capacityProgress;
    private MaterialSwitch geoSwitch;
    private TabLayout statusTabs;
    private RecyclerView entrantsRecyclerView;
    private MaterialButton runLotteryButton, drawSingleButton, viewMapButton, addToWaitlistButton, exportCsvButton;
    private WaitingEntrantsAdapter entrantsAdapter;
    private ActivityResultLauncher<String> createCsvDocumentLauncher;

    private List<Map<String, Object>> waitingList = new ArrayList<>();
    private List<Map<String, Object>> invitedList = new ArrayList<>();
    private List<Map<String, Object>> enrolledList = new ArrayList<>();
    private List<Map<String, Object>> cancelledList = new ArrayList<>();

    private ImageView exportButton, editButton;
    private String pendingCsvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.manage_event);

        eventId = getIntent().getStringExtra("EVENT_ID");
        eventRepository = new EventRepository();
        eventViewModel = new EventViewModel();
        userViewModel = new UserViewModel(this);
        notificationRepository = new NotificationRepository();

        createCsvDocumentLauncher = registerForActivityResult(
                new ActivityResultContracts.CreateDocument("text/csv"),
                uri -> {
                    if (uri == null || pendingCsvContent == null) {
                        pendingCsvContent = null;
                        return;
                    }

                    if (writeCsvToUri(uri, pendingCsvContent)) {
                        showCsvExportSuccessToast();
                    }
                    pendingCsvContent = null;
                }
        );

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
        viewMapButton = findViewById(R.id.viewMapButton);
        exportButton = findViewById(R.id.exportButton);
        editButton = findViewById(R.id.editButton);
        addToWaitlistButton = findViewById(R.id.addToWaitlistButton);
        exportCsvButton = findViewById(R.id.exportCsvButton);
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

        if (viewMapButton != null) {
            viewMapButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, EntrantMapView.class);
                intent.putExtra("EVENT_ID", eventId);
                startActivity(intent);
            });
        }

        if (addToWaitlistButton != null) {
            addToWaitlistButton.setOnClickListener(v -> showAddToWaitlistDialog());
        }
        if (exportCsvButton != null) {
            exportCsvButton.setOnClickListener(v -> exportFinalEnrolledCsv());
        }

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

        // Disable geoSwitch interaction in dashboard, it should be changed via Edit
        geoSwitch.setEnabled(false);
    }

    private void showAddToWaitlistDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_to_waitlist);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        AutoCompleteTextView userSearchInput = dialog.findViewById(R.id.userSearchInput);
        RecyclerView selectedUsersRecyclerView = dialog.findViewById(R.id.selectedUsersRecyclerView);
        TextView selectedTitle = dialog.findViewById(R.id.selectedTitle);
        MaterialButton addSelectedButton = dialog.findViewById(R.id.addSelectedButton);
        MaterialButton cancelButton = dialog.findViewById(R.id.cancelButton);

        List<User> selectedUsers = new ArrayList<>();
        SelectedUsersAdapter selectedAdapter = new SelectedUsersAdapter(selectedUsers, user -> {
            selectedUsers.remove(user);
        });
        refreshSelectedUsersList(selectedUsers, selectedAdapter, selectedTitle);

        selectedUsersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        selectedUsersRecyclerView.setAdapter(selectedAdapter);

        UserAutocompleteAdapter autocompleteAdapter = new UserAutocompleteAdapter(this, userViewModel);
        userSearchInput.setAdapter(autocompleteAdapter);
        userSearchInput.setThreshold(1);

        userSearchInput.setOnItemClickListener((parent, view, position, id) -> {
            User user = (User) parent.getItemAtPosition(position);
            boolean alreadySelected = false;
            for (User u : selectedUsers) {
                if (u.getUserId().equals(user.getUserId())) {
                    alreadySelected = true;
                    break;
                }
            }

            if (!alreadySelected) {
                selectedUsers.add(user);
                refreshSelectedUsersList(selectedUsers, selectedAdapter, selectedTitle);
            }
            userSearchInput.setText("");
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        addSelectedButton.setOnClickListener(v -> {
            if (selectedUsers.isEmpty()) {
                Toast.makeText(this, "Please select at least one user", Toast.LENGTH_SHORT).show();
                return;
            }

            for (User user : selectedUsers) {
                Notification waitlistInvite = new Notification(
                        user.getUserId(),
                        eventId,
                        thisEvent.getName(),
                        thisEvent.getDate(),
                        "You have been invited to join the waitlist for: " + thisEvent.getName(),
                        "Waitlist Invitation"
                );
                notificationRepository.addNotification(waitlistInvite);
            }
            Toast.makeText(this, "Invitations sent to users", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void refreshSelectedUsersList(List<User> selectedUsers, SelectedUsersAdapter adapter, TextView selectedTitle) {
        adapter.notifyDataSetChanged();
        selectedTitle.setVisibility(selectedUsers.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void loadEventDetails() {
        if (eventId == null) return;

        eventRepository.getEventById(eventId, event -> {
            if (event != null) {
                thisEvent = event;
                if (addToWaitlistButton != null) {
                    addToWaitlistButton.setVisibility(thisEvent.isPrivate() ? View.VISIBLE : View.GONE);
                }
                if (exportButton != null) {
                    exportButton.setVisibility(thisEvent.isPrivate() ? View.GONE : View.VISIBLE);
                }
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
                title = "FINAL ENROLLED ENTRANTS";
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

        updateExportCsvButton(position);
    }

    private void updateExportCsvButton(int position) {
        if (exportCsvButton == null) {
            return;
        }

        exportCsvButton.setVisibility(position == 2 ? View.VISIBLE : View.GONE);
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

                notificationRepository.getNotificationsByUserId(userId, notifications -> {
                    if (notifications != null && !notifications.isEmpty()) {
                        for (Notification notification : notifications)
                            if (notification.getEventId().equals(eventId) && notification.getStatus().equals("Invited"))
                                notificationRepository.removeNotification(notification);
                    }
                });

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

                loadAllParticipants();
            } else {
                Toast.makeText(this, "Failed to cancel invitation", Toast.LENGTH_SHORT).show();
            }
        });
    }
// This is a confusing method... the name doesn't make any sense, and doesn't
// reveal that it's actually creating a notification too!
//    @NonNull
//    private NotificationRepository getNotificationRepositoryFromUserId(String userId) {
//        EventRepository eventRepository = new EventRepository();
//        NotificationRepository notificationRepository = new NotificationRepository();
//
//        eventRepository.getEventById(eventId, event -> {
//            if (event == null) return;
//            String eventName = event.getName();
//            String eventDateTime = event.getDate();
//            notificationRepository.addNotification(new Notification(
//                    userId,
//                    eventId,
//                    eventName,
//                    eventDateTime,
//                    "Cancelled"
//            ));
//        });
//        return notificationRepository;
//    }


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

    private void exportFinalEnrolledCsv() {
        if (thisEvent == null) {
            Toast.makeText(this, "Event details are still loading", Toast.LENGTH_SHORT).show();
            return;
        }

        String csvContent = ExportCSV.buildFinalEnrolledEntrantsCsv(enrolledList);
        String fileName = ExportCSV.createFinalEnrolledFileName(thisEvent.getName(), new Date());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveCsvToDownloads(fileName, csvContent);
            return;
        }

        pendingCsvContent = csvContent;
        createCsvDocumentLauncher.launch(fileName);
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private void saveCsvToDownloads(String fileName, String csvContent) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
        values.put(MediaStore.Downloads.MIME_TYPE, "text/csv");
        values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/TuringEvents");

        Uri csvUri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
        if (csvUri == null) {
            Toast.makeText(this, "Failed to export CSV", Toast.LENGTH_SHORT).show();
            return;
        }

        if (writeCsvToUri(csvUri, csvContent)) {
            showCsvExportSuccessToast();
        } else {
            getContentResolver().delete(csvUri, null, null);
        }
    }

    private boolean writeCsvToUri(Uri uri, String csvContent) {
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            if (outputStream == null) {
                throw new IOException("Could not open destination for CSV export");
            }

            try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
                writer.write(csvContent);
                writer.flush();
            }
            return true;
        } catch (Exception e) {
            Log.e("ManageEventView", "Failed to export CSV", e);
            Toast.makeText(this, "Failed to export CSV", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void showCsvExportSuccessToast() {
        String message = enrolledList.isEmpty()
                ? "No enrolled entrants yet. Exported CSV headers only."
                : "Final enrolled entrants CSV exported.";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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

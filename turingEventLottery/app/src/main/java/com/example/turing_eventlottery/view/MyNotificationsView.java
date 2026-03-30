package com.example.turing_eventlottery.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.EventRepository;
import com.example.turing_eventlottery.model.ModelCallback;
import com.example.turing_eventlottery.model.Notification;
import com.example.turing_eventlottery.model.NotificationRepository;
import com.example.turing_eventlottery.model.User;
import com.example.turing_eventlottery.model.UserRepository;
import com.example.turing_eventlottery.viewmodel.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Activity to display the notifications for the current user.
 * <p>
 *     Users can accept or decline invitations. Accepting adds to even participants.
 *     Declining triggers a new lottery draw if there are other users on the waitlist.
 * </p>
 */
// Need to fix MVVM architecture for part 4
public class MyNotificationsView extends AppCompatActivity implements NotificationAdapter.OnNotificationActionListener {

    private NotificationRepository notificationRepository;
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private NotificationAdapter adapter;
    private RecyclerView recyclerView;
    private TextView noNotificationsText;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.my_notifications);

        notificationRepository = new NotificationRepository();
        eventRepository = new EventRepository();
        userRepository = new UserRepository();
        userViewModel = new UserViewModel(this);

        initViews();
        loadNotifications();
        setupNavigation();
    }

    private void initViews() {
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        recyclerView = findViewById(R.id.notificationsRecyclerView);
        noNotificationsText = findViewById(R.id.noNotificationsText);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(new ArrayList<>());
        adapter.setOnNotificationActionListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        FloatingActionButton fabCreate = findViewById(R.id.fabCreate);

        bottomNav.setSelectedItemId(R.id.nav_alerts);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, UserDashboardView.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_events) {
                startActivity(new Intent(this, BrowseEventsView.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_alerts) {
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

    private void loadNotifications() {
        userViewModel.loadUser(new ModelCallback<User>() {
            @Override
            public void onCallback(User user) {
                if (user != null) {
                    View sendNotificationBtn = findViewById(R.id.sendNotificationButton);
                    if (user.isOrganizer()) {
                        sendNotificationBtn.setVisibility(View.VISIBLE);
                        sendNotificationBtn.setOnClickListener(v -> {
                            Intent intent = new Intent(MyNotificationsView.this, SendNotificationView.class);
                            startActivity(intent);
                        });
                    }

                    notificationRepository.getNotificationsByUserId(user.getUserId(), notifications -> {
                        if (notifications != null) {
                            if (notifications.isEmpty()) {
                                noNotificationsText.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                noNotificationsText.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                adapter.setNotifications(notifications);
                            }
                        } else {
                            Toast.makeText(MyNotificationsView.this, "Error loading notifications", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onAccept(Notification notification) {
        if ("Waitlist Invitation".equals(notification.getStatus())) {
            userRepository.getUser(notification.getUserId(), user -> {
                if (user != null) {
                    eventRepository.addUserToWaitList(notification.getEventId(), user, success -> {
                        if (success) {
                            notificationRepository.updateNotificationStatus(notification.getId(), "Accepted", result -> {
                                Log.d("MyNotificationsView", "Joined waitlist!!");
                                Toast.makeText(this, "Joined Waitlist!", Toast.LENGTH_SHORT).show();
                                loadNotifications();
                            });
                        } else {
                            Log.d("MyNotificationsView", "Failed to join waitlist");
                            Toast.makeText(this, "Failed to join waitlist", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } else {
            eventRepository.registerParticipant(notification.getEventId(), notification.getUserId(), success -> {
                if (success) {
                    notificationRepository.updateNotificationStatus(notification.getId(), "Accepted", result -> {
                        Toast.makeText(this, "Invitation Accepted!", Toast.LENGTH_SHORT).show();
                        loadNotifications();
                    });
                } else {
                    Toast.makeText(this, "Failed to accept invitation", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onDecline(Notification notification) {
        if ("Waitlist Invitation".equals(notification.getStatus())) {
            notificationRepository.updateNotificationStatus(notification.getId(), "Declined", result -> {
                Toast.makeText(MyNotificationsView.this, "Invitation Declined", Toast.LENGTH_SHORT).show();
                loadNotifications();
            });
        } else {
            userRepository.getUser(notification.getUserId(), new ModelCallback<User>() {
                @Override
                public void onCallback(User user) {
                    if (user != null) {
                        eventRepository.removeUserFromWaitlist(notification.getEventId(), user, success -> {
                            if (success) {
                                notificationRepository.updateNotificationStatus(notification.getId(), "Declined", result -> {
                                    Toast.makeText(MyNotificationsView.this, "Invitation Declined", Toast.LENGTH_SHORT).show();
                                    triggerNewLotteryDraw(notification.getEventId());
                                    loadNotifications();
                                });
                            } else {
                                Toast.makeText(MyNotificationsView.this, "Failed to decline invitation", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(MyNotificationsView.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void triggerNewLotteryDraw(String eventId) {
        eventRepository.getEventById(eventId, event -> {
            if (event == null) return;
            eventRepository.getWaitlistUsers(eventId, waitingUserIds -> {
                if (waitingUserIds != null && !waitingUserIds.isEmpty()) {
                    Collections.shuffle(waitingUserIds);
                    String winnerId = waitingUserIds.get(0);
                    
                    eventRepository.updateWaitlistStatus(eventId, winnerId, "Invited", success -> {
                        if (success) {
                            Notification newNotification = new Notification(
                                    winnerId,
                                    event.getId(),
                                    event.getName(),
                                    event.getDate(),
                                    "Invited"
                            );
                            notificationRepository.addNotification(newNotification);
                        }
                    });
                }
            });
        });
    }
}

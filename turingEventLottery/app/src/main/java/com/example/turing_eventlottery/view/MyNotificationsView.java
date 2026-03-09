package com.example.turing_eventlottery.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.Notification;
import com.example.turing_eventlottery.model.NotificationRepository;
import com.example.turing_eventlottery.model.User;
import com.example.turing_eventlottery.model.UserCallback;
import com.example.turing_eventlottery.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class MyNotificationsView extends AppCompatActivity {

    private NotificationRepository notificationRepository;
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
        userViewModel = new UserViewModel(this);

        initViews();
        loadNotifications();
    }

    private void initViews() {
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        recyclerView = findViewById(R.id.notificationsRecyclerView);
        noNotificationsText = findViewById(R.id.noNotificationsText);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }

    private void loadNotifications() {
        userViewModel.loadUser(new UserCallback() {
            @Override
            public void onSuccess(User user) {
                if (user != null) {
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


            // TODO: Fix this
            //@Override
            //public void onFailure(Exception e) {
            //    Toast.makeText(MyNotificationsView.this, "Error loading user profile", Toast.LENGTH_SHORT).show();
            //}
        });
    }
}

package com.example.turing_eventlottery.view;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.Comment;
import com.example.turing_eventlottery.model.CommentRepository;
import com.example.turing_eventlottery.model.Event;
import com.example.turing_eventlottery.model.EventCallback;
import com.example.turing_eventlottery.model.User;
import com.example.turing_eventlottery.viewmodel.EventViewModel;
import com.example.turing_eventlottery.viewmodel.UserViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class EventDetailsView extends AppCompatActivity {
    private EventViewModel eventViewModel;
    private UserViewModel userViewModel;
    private CommentRepository commentRepository;

    private ImageView posterView;
    private TextView nameView;
    private TextView locationView;
    private TextView dateTimeView;
    private TextView demandCountView;
    private TextView descriptionView;
    private TextView organizerIdView;
    private View organizerBox;
    private MaterialButton deleteEventButton;
    private MaterialButton waitlistButton;
    
    private LinearLayout commentsList;
    private EditText commentInput;
    private ImageButton postCommentButton;

    private boolean isOnWaitlist;
    private String eventId;
    private boolean fromAdmin;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.event_details);

        posterView = findViewById(R.id.eventPoster);
        nameView = findViewById(R.id.eventName);
        locationView = findViewById(R.id.eventLocation);
        dateTimeView = findViewById(R.id.eventDateTime);
        demandCountView = findViewById(R.id.demandTotal);
        descriptionView = findViewById(R.id.eventDescription);
        organizerIdView = findViewById(R.id.organizerId);
        organizerBox = findViewById(R.id.organizerBox);
        deleteEventButton = findViewById(R.id.deleteEventButton);
        waitlistButton = findViewById(R.id.waitlistButton);
        MaterialButton backButton = findViewById(R.id.backButton);
        
        commentsList = findViewById(R.id.commentsList);
        commentInput = findViewById(R.id.commentInput);
        postCommentButton = findViewById(R.id.postCommentButton);

        eventViewModel = new EventViewModel();
        userViewModel = new UserViewModel(this);
        commentRepository = new CommentRepository();

        eventId = getIntent().getStringExtra("EVENT_ID");
        fromAdmin = getIntent().getBooleanExtra("fromAdmin", false);

        backButton.setOnClickListener(v -> finish());

        if (fromAdmin) {
            organizerBox.setVisibility(View.VISIBLE);
            waitlistButton.setVisibility(View.GONE);
        }

        if (eventId != null) {
            eventViewModel.getEventById(eventId, this::displayEvent);

            eventViewModel.checkRegistrationStatus(eventId, isOpen -> {
                waitlistButton.setEnabled(isOpen);
            });
            
            loadComments();
        }

        userViewModel.loadUser(loadedUser -> {
            this.currentUser = loadedUser;
            if (loadedUser.isAdmin() && fromAdmin) {
                deleteEventButton.setVisibility(View.VISIBLE);
                deleteEventButton.setOnClickListener(v -> showDeleteConfirmation());
            }

            eventViewModel.isUserOnWaitlist(loadedUser, eventId, onWaitlist -> {
                isOnWaitlist = onWaitlist;
                updateWaitlistButton();
            });

            waitlistButton.setOnClickListener(v -> {
                if ("Guest".equals(loadedUser.getUserName())) {
                    Toast.makeText(this,
                            "You must create an account first, go to My Profile",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isOnWaitlist) {
                    eventViewModel.leaveWaitlist(loadedUser, eventId, success -> {
                        if (success) {
                            isOnWaitlist = false;
                            Toast.makeText(this, "You have left the waitlist", Toast.LENGTH_SHORT).show();
                            updateWaitlistButton();
                        } else {
                            Toast.makeText(this, "Failed to leave waitlist, try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    eventViewModel.joinWaitlist(loadedUser, eventId, success -> {
                        if (success) {
                            isOnWaitlist = true;
                            Toast.makeText(this, "You have joined the waitlist", Toast.LENGTH_SHORT).show();
                            updateWaitlistButton();
                        } else {
                            Toast.makeText(this, "Failed to join waitlist, try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        });

        postCommentButton.setOnClickListener(v -> {
            String text = commentInput.getText().toString().trim();
            if (text.isEmpty()) return;
            if (currentUser == null || "Guest".equals(currentUser.getUserName())) {
                Toast.makeText(this, "Please log in to comment", Toast.LENGTH_SHORT).show();
                return;
            }

            commentRepository.addComment(currentUser.getUserId(), eventId, text, success -> {
                if (success) {
                    commentInput.setText("");
                    loadComments();
                } else {
                    Toast.makeText(this, "Failed to post comment", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadComments() {
        commentRepository.getCommentsByEvent(eventId, comments -> {
            commentsList.removeAllViews();
            if (comments != null) {
                for (Comment comment : comments) {
                    addCommentToView(comment);
                }
            }
        });
    }

    private void addCommentToView(Comment comment) {
        View commentView = getLayoutInflater().inflate(android.R.layout.simple_list_item_2, null);
        TextView text1 = commentView.findViewById(android.R.id.text1);
        TextView text2 = commentView.findViewById(android.R.id.text2);

        text1.setText(comment.getText());
        text2.setText("User ID: " + comment.getUserId());
        
        commentsList.addView(commentView);
        
        // Add a simple separator
        View separator = new View(this);
        separator.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        separator.setBackgroundColor(0xFFDDDDDD);
        commentsList.addView(separator);
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    eventViewModel.deleteEvent(eventId, success -> {
                        if (success) {
                            Toast.makeText(this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to delete event", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void displayEvent(Event event) {
        if (event == null) return;
        nameView.setText(event.getName());
        locationView.setText(event.getLocation());
        dateTimeView.setText(eventViewModel.formatEventDate(event.getDate()));
        eventViewModel.getWaitlistCount(event.getId(), count ->
                demandCountView.setText(String.valueOf(count)));
        descriptionView.setText(event.getDescription());

        if (fromAdmin && event.getOrganizerId() != null) {
            organizerIdView.setText(event.getOrganizerId());
        }

        String posterUrl = event.getPosterUrl();
        if (posterUrl != null && !posterUrl.isEmpty()) {
            Glide.with(this).load(posterUrl).into(posterView);
        }

    }

    private void updateWaitlistButton() {
        if (isOnWaitlist) {
            waitlistButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
            waitlistButton.setTextColor(getColor(R.color.red));
            waitlistButton.setText("Leave Waitlist");
        } else {
            waitlistButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.primaryBlue)));
            waitlistButton.setTextColor(getColor(R.color.white));
            waitlistButton.setText("Join Waitlist");
        }
    }
}

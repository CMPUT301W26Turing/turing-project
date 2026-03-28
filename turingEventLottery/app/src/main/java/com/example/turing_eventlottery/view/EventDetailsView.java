package com.example.turing_eventlottery.view;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.Comment;
import com.example.turing_eventlottery.model.CommentRepository;
import com.example.turing_eventlottery.model.Event;
import com.example.turing_eventlottery.model.ModelCallback;
import com.example.turing_eventlottery.model.User;
import com.example.turing_eventlottery.viewmodel.EventViewModel;
import com.example.turing_eventlottery.viewmodel.UserViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.List;

/**
 * View for displaying a specific event's details.
 * <p>
 *     Handles displaying event information (poster, name, location, date/time, description)
 *     Admin actions and user actions.
 * </p>
 */
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
    private Event currentEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.event_details);

        // Bind views
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

        // Show organizer info and hide waitlist if accessed by admin
        if (fromAdmin) {
            organizerBox.setVisibility(View.VISIBLE);
            //waitlistButton.setVisibility(View.GONE); (I marked this out because I can not join my own event)
        }

        if (eventId != null) {
            eventViewModel.getEventById(eventId, event -> {
                this.currentEvent = event;
                displayEvent(event);
                loadComments();
            });

            // Enable waitlist button only if registration is open
            eventViewModel.checkRegistrationStatus(eventId, isOpen -> {
                waitlistButton.setEnabled(isOpen);
            });
        }

        userViewModel.loadUser(loadedUser -> {
            // Show delete button for admins
            this.currentUser = loadedUser;
            if (loadedUser.isAdmin() && fromAdmin) {
                deleteEventButton.setVisibility(View.VISIBLE);
                deleteEventButton.setOnClickListener(v -> showDeleteConfirmation());
            }

            // Check user's event status: "Enrolled", "Invited", "Waiting", or "None"
            eventViewModel.getUserEventStatus(eventId, loadedUser.getUserId(), new ModelCallback<String>() {
                @Override
                public void onCallback(String status) {

                    //CASE 1: Already enrolled
                    if ("Enrolled".equals(status)) {
                        isOnWaitlist = false;
                        waitlistButton.setEnabled(false);
                        waitlistButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.greenIconTint)));
                        waitlistButton.setTextColor(getColor(R.color.white));
                        waitlistButton.setText("Enrolled");
                        return;
                    }


                    //CASE 2: Invited via lottery → Show "Accept" button
                    if ("Invited".equals(status)) {
                        isOnWaitlist = false;
                        waitlistButton.setEnabled(true);
                        waitlistButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.primaryBlue)));
                        waitlistButton.setTextColor(getColor(R.color.white));
                        waitlistButton.setText("Accept");
                    } else {
                        //CASE 3: Check waitlist status for "Waiting" or "None"
                        eventViewModel.isUserOnWaitlist(loadedUser, eventId, new ModelCallback<Boolean>() {
                            @Override
                            public void onCallback(Boolean onWaitlist) {
                                isOnWaitlist = onWaitlist != null && onWaitlist;
                                updateWaitlistButton();
                            }
                        });
                    }

                    //Handle button click
                    waitlistButton.setOnClickListener(v -> {
                        if ("Guest".equals(loadedUser.getUserName())) {
                            Toast.makeText(EventDetailsView.this,
                                    "You must create an account first, go to My Profile",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Re-check status to prevent race conditions
                        eventViewModel.getUserEventStatus(eventId, loadedUser.getUserId(), new ModelCallback<String>() {
                            @Override
                            public void onCallback(String currentStatus) {

                                //Prevent double-enrollment
                                if ("Enrolled".equals(currentStatus)) {
                                    Toast.makeText(EventDetailsView.this,
                                            "You are already enrolled in this event",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                //CASE: Accept invitation
                                if ("Invited".equals(currentStatus)) {
                                    waitlistButton.setEnabled(false); // Prevent double-click

                                    eventViewModel.acceptInvitation(eventId, loadedUser.getUserId(), new ModelCallback<Boolean>() {
                                        @Override
                                        public void onCallback(Boolean success) {
                                            if (success != null && success) {
                                                waitlistButton.setEnabled(false);
                                                waitlistButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.greenIconTint)));
                                                waitlistButton.setTextColor(getColor(R.color.white));
                                                waitlistButton.setText("Enrolled");
                                                Toast.makeText(EventDetailsView.this, "Invitation Accepted!", Toast.LENGTH_SHORT).show();

                                                eventViewModel.getUserEventStatus(eventId, loadedUser.getUserId(), new ModelCallback<String>() {
                                                    @Override
                                                    public void onCallback(String newStatus) {
                                                        Log.d("EventDetails", "New status after accept: " + newStatus);
                                                    }
                                                });
                                            } else {
                                                waitlistButton.setEnabled(true); // Re-enable on failure
                                                Toast.makeText(EventDetailsView.this, "Failed to accept invitation", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    return;
                                }

                                //CASE: Leave waitlist (existing functionality)
                                if (isOnWaitlist) {
                                    eventViewModel.leaveWaitlist(loadedUser, eventId, new ModelCallback<Boolean>() {
                                        @Override
                                        public void onCallback(Boolean success) {
                                            if (success != null && success) {
                                                isOnWaitlist = false;
                                                Toast.makeText(EventDetailsView.this,
                                                        "You have left the waitlist",
                                                        Toast.LENGTH_SHORT).show();
                                                updateWaitlistButton();
                                            } else {
                                                Toast.makeText(EventDetailsView.this,
                                                        "Failed to leave waitlist, try again",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                //CASE: Join waitlist (existing functionality)
                                else {
                                    eventViewModel.joinWaitlist(loadedUser, eventId, new ModelCallback<Boolean>() {
                                        @Override
                                        public void onCallback(Boolean success) {
                                            if (success != null && success) {
                                                isOnWaitlist = true;
                                                Toast.makeText(EventDetailsView.this,
                                                        "You have joined the waitlist",
                                                        Toast.LENGTH_SHORT).show();
                                                updateWaitlistButton();
                                            } else {
                                                Toast.makeText(EventDetailsView.this,
                                                        "Failed to join waitlist, try again",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        });
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

            commentRepository.addComment(currentUser.getUserId(), currentUser.getUserName(), eventId, text, success -> {
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
        View view = getLayoutInflater().inflate(R.layout.item_comment, null);
        TextView nameView = view.findViewById(R.id.commentUserName);
        TextView textView = view.findViewById(R.id.commentText);
        TextView avatarText = view.findViewById(R.id.commentAvatarText);
        ImageView menuButton = view.findViewById(R.id.commentMenu);

        nameView.setText(comment.getUserName());
        textView.setText(comment.getText());

        // Handle Initials
        String initials = "";
        String username = comment.getUserName();
        if (username != null && !username.isEmpty()) {
            String[] parts = username.split(" ");
            if (parts.length > 0 && !parts[0].isEmpty()) {
                initials += parts[0].substring(0, 1).toUpperCase();
                if (parts.length > 1 && !parts[1].isEmpty()) {
                    initials += parts[1].substring(0, 1).toUpperCase();
                }
            }
        }
        avatarText.setText(initials);

        boolean isOwner = currentUser != null && comment.getUserId().equals(currentUser.getUserId());
        boolean isOrganizer = currentUser != null && currentEvent != null && currentUser.getUserId().equals(currentEvent.getOrganizerId());
        boolean isAdmin = currentUser != null && currentUser.isAdmin();

        if (isOwner || isOrganizer || isAdmin) {
            menuButton.setVisibility(View.VISIBLE);
            menuButton.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(this, v);
                popup.getMenu().add("Delete");
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getTitle().equals("Delete")) {
                        showDeleteCommentConfirmation(comment);
                        return true;
                    }
                    return false;
                });
                popup.show();
            });
        }

        commentsList.addView(view);
    }

    private void showDeleteCommentConfirmation(Comment comment) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Comment")
                .setMessage("Are you sure you want to delete this comment?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    commentRepository.deleteComment(comment, success -> {
                        if (success) {
                            Toast.makeText(this, "Comment deleted", Toast.LENGTH_SHORT).show();
                            loadComments();
                        } else {
                            Toast.makeText(this, "Failed to delete comment", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
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

package com.example.turing_eventlottery.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Location;
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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.turing_eventlottery.R;
import com.example.turing_eventlottery.model.Comment;
import com.example.turing_eventlottery.model.CommentRepository;
import com.example.turing_eventlottery.model.Event;
import com.example.turing_eventlottery.model.ModelCallback;
import com.example.turing_eventlottery.model.User;
import com.example.turing_eventlottery.viewmodel.EventViewModel;
import com.example.turing_eventlottery.viewmodel.UserViewModel;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ImageButton sortCommentsButton;
    
    private View replyPreviewArea;
    private TextView replyingToText;
    private ImageButton cancelReplyButton;

    private boolean isOnWaitlist;
    private String eventId;
    private boolean fromAdmin;
    private User currentUser;
    private Event currentEvent;
    
    private String currentParentId = null;
    private String currentSortBy = "timestamp";
    private Query.Direction currentSortDirection = Query.Direction.ASCENDING;

    private ListenerRegistration commentsListener;

    private FusedLocationProviderClient fusedLocationClient;

    private final ActivityResultLauncher<String[]> locationPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
                if (fineLocationGranted != null && fineLocationGranted) {
                    attemptJoinWithLocation();
                } else if (coarseLocationGranted != null && coarseLocationGranted) {
                    attemptJoinWithLocation();
                } else {
                    Toast.makeText(this, "Location permission is required to join this event.", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.event_details);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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
        sortCommentsButton = findViewById(R.id.sortCommentsButton);
        
        replyPreviewArea = findViewById(R.id.replyPreviewArea);
        replyingToText = findViewById(R.id.replyingToText);
        cancelReplyButton = findViewById(R.id.cancelReplyButton);

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
                startListeningForComments();
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

                        // Check if current user is the organizer or co-organizer
                        if (currentEvent != null && (loadedUser.getUserId().equals(currentEvent.getOrganizerId()) || 
                            loadedUser.getUserId().equals(currentEvent.getCoOrganizerId()))) {
                            Toast.makeText(EventDetailsView.this, 
                                "Organizers and Co-Organizers cannot join their own events.", 
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
                                    if (currentEvent != null && currentEvent.isGeolocationRequired()) {
                                        new AlertDialog.Builder(EventDetailsView.this)
                                                .setTitle("Geolocation Required")
                                                .setMessage("This event requires your location to join the waitlist. Your current location will be used to verify you are within the allowed radius.")
                                                .setPositiveButton("Join", (dialog, which) -> checkLocationPermissionAndJoin())
                                                .setNegativeButton("Cancel", null)
                                                .show();
                                    } else {
                                        performJoinWaitlist(null, null);
                                    }
                                }
                            }
                        });
                    });
                }
            });
        });

        cancelReplyButton.setOnClickListener(v -> {
            currentParentId = null;
            replyPreviewArea.setVisibility(View.GONE);
        });

        postCommentButton.setOnClickListener(v -> {
            String text = commentInput.getText().toString().trim();
            if (text.isEmpty()) return;
            if (currentUser == null || "Guest".equals(currentUser.getUserName())) {
                Toast.makeText(this, "Please log in to comment", Toast.LENGTH_SHORT).show();
                return;
            }

            commentRepository.addComment(currentUser.getUserId(), currentUser.getUserName(), eventId, text, currentParentId, success -> {
                if (success) {
                    commentInput.setText("");
                    currentParentId = null;
                    replyPreviewArea.setVisibility(View.GONE);
                } else {
                    Toast.makeText(this, "Failed to post comment", Toast.LENGTH_SHORT).show();
                }
            });
        });

        if (sortCommentsButton != null) {
            sortCommentsButton.setOnClickListener(this::showSortMenu);
        }
    }

    private void showSortMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenu().add(0, 1, 0, "Oldest first");
        popup.getMenu().add(0, 2, 1, "Newest first");
        popup.getMenu().add(0, 3, 2, "Most liked");

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 1:
                    currentSortBy = "timestamp";
                    currentSortDirection = Query.Direction.ASCENDING;
                    break;
                case 2:
                    currentSortBy = "timestamp";
                    currentSortDirection = Query.Direction.DESCENDING;
                    break;
                case 3:
                    currentSortBy = "likes";
                    currentSortDirection = Query.Direction.DESCENDING;
                    break;
            }
            startListeningForComments();
            return true;
        });
        popup.show();
    }

    private void checkLocationPermissionAndJoin() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        } else {
            attemptJoinWithLocation();
        }
    }

    private void attemptJoinWithLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Toast.makeText(this, "Fetching current location...", Toast.LENGTH_SHORT).show();

        CurrentLocationRequest locationRequest = new CurrentLocationRequest.Builder()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();

        CancellationTokenSource cts = new CancellationTokenSource();

        fusedLocationClient.getCurrentLocation(locationRequest, cts.getToken()).addOnSuccessListener(this, location -> {
            if (location != null) {
                if (isWithinRadius(location)) {
                    performJoinWaitlist(location.getLatitude(), location.getLongitude());
                } else {
                    Toast.makeText(this, "You do not meet the location requirement for this event.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Could not get your current location. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isWithinRadius(Location userLocation) {
        if (currentEvent == null) return false;
        float[] results = new float[1];
        Location.distanceBetween(userLocation.getLatitude(), userLocation.getLongitude(),
                currentEvent.getLatitude(), currentEvent.getLongitude(), results);
        float distanceInKm = results[0] / 1000;
        return distanceInKm <= currentEvent.getRadius();
    }

    private void performJoinWaitlist(Double lat, Double lon) {
        eventViewModel.joinWaitlist(currentUser, eventId, lat, lon, new ModelCallback<Boolean>() {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (commentsListener != null) {
            commentsListener.remove();
        }
    }

    private void startListeningForComments() {
        if (commentsListener != null) commentsListener.remove();
        commentsListener = commentRepository.listenForComments(eventId, currentSortBy, currentSortDirection, this::displayComments);
    }

    private void displayComments(List<Comment> comments) {
        commentsList.removeAllViews();
        if (comments != null && !comments.isEmpty()) {
            Map<String, List<Comment>> childrenMap = new HashMap<>();
            List<Comment> rootComments = new ArrayList<>();
            
            for (Comment c : comments) {
                if (c.getParentId() == null) {
                    rootComments.add(c);
                } else {
                    if (!childrenMap.containsKey(c.getParentId())) {
                        childrenMap.put(c.getParentId(), new ArrayList<>());
                    }
                    childrenMap.get(c.getParentId()).add(c);
                }
            }
            
            for (Comment root : rootComments) {
                renderCommentAndChildren(root, childrenMap, 0);
            }
        }
    }

    private void renderCommentAndChildren(Comment comment, Map<String, List<Comment>> childrenMap, int depth) {
        addCommentToView(comment, depth);
        
        List<Comment> children = childrenMap.get(comment.getCommentId());
        if (children != null) {
            for (Comment child : children) {
                renderCommentAndChildren(child, childrenMap, Math.min(depth + 1, 3));
            }
        }
    }

    private void addCommentToView(Comment comment, int depth) {
        View view = getLayoutInflater().inflate(R.layout.item_comment, null);
        
        int indentPx = (int) (depth * 24 * getResources().getDisplayMetrics().density);
        ConstraintLayout container = view.findViewById(R.id.commentContainer);
        container.setPadding(container.getPaddingLeft() + indentPx, container.getPaddingTop(), 
                           container.getPaddingRight(), container.getPaddingBottom());

        TextView nameView = view.findViewById(R.id.commentUserName);
        TextView textView = view.findViewById(R.id.commentText);
        TextView avatarText = view.findViewById(R.id.commentAvatarText);
        ImageView menuButton = view.findViewById(R.id.commentMenu);
        TextView replyButton = view.findViewById(R.id.replyButton);
        TextView timestampView = view.findViewById(R.id.commentTimestamp);
        
        ImageButton likeButton = view.findViewById(R.id.likeButton);
        ImageButton dislikeButton = view.findViewById(R.id.dislikeButton);
        TextView likeCountView = view.findViewById(R.id.likeCount);

        nameView.setText(comment.getUserName());
        textView.setText(comment.getText());
        if (comment.getTimestamp() != null) {
            timestampView.setText(formatTimestamp(comment.getTimestamp()));
        }
        likeCountView.setText(String.valueOf(comment.getLikes()));

        if (currentUser != null) {
            if (comment.getLikedBy().contains(currentUser.getUserId())) {
                likeButton.setImageTintList(ColorStateList.valueOf(getColor(R.color.primaryBlue)));
            }
            if (comment.getDislikedBy().contains(currentUser.getUserId())) {
                dislikeButton.setImageTintList(ColorStateList.valueOf(getColor(R.color.red)));
            }
        }

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

        replyButton.setOnClickListener(v -> {
            currentParentId = comment.getCommentId();
            replyingToText.setText("Replying to " + comment.getUserName());
            replyPreviewArea.setVisibility(View.VISIBLE);
            commentInput.requestFocus();
        });

        likeButton.setOnClickListener(v -> {
            if (currentUser == null || "Guest".equals(currentUser.getUserName())) return;
            commentRepository.toggleLikeComment(currentUser.getUserId(), comment, success -> { });
        });

        dislikeButton.setOnClickListener(v -> {
            if (currentUser == null || "Guest".equals(currentUser.getUserName())) return;
            commentRepository.toggleDislikeComment(currentUser.getUserId(), comment, success -> { });
        });

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

    private String formatTimestamp(Timestamp timestamp) {
        long seconds = Timestamp.now().getSeconds() - timestamp.getSeconds();
        if (seconds < 60) return "Just now";
        if (seconds < 3600) return (seconds / 60) + "m ago";
        if (seconds < 86400) return (seconds / 3600) + "h ago";
        return (seconds / 86400) + "d ago";
    }

    private void showDeleteCommentConfirmation(Comment comment) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Comment")
                .setMessage("Are you sure you want to delete this comment?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    commentRepository.deleteComment(comment, success -> {
                        if (success) {
                            Toast.makeText(this, "Comment deleted", Toast.LENGTH_SHORT).show();
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

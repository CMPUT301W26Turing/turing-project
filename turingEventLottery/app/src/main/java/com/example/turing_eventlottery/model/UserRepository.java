package com.example.turing_eventlottery.model;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository class responsible for handling all user-related
 * database operations through Firebase Firestore.
 */
public class UserRepository {
    private static final String TAG = "UserRepository";
    private static final String COLLECTION_NAME = "users";
    private final CollectionReference usersCollection;

    public UserRepository() {
        usersCollection = FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    /**
     * Retrieves a user from the database.
     * Development Backdoor: Force all loaded users to be administrators for testing.
     */
    public void getUser(String userId, UserCallback callback) {
        usersCollection.document(userId).get()
                .addOnSuccessListener(document -> {
                    User user;
                    if (document.exists()) {
                        user = document.toObject(User.class);
                    } else {
                        // User not in database, create default object
                        user = new User(userId, null, false, false);
                    }

                    // --- Development Backdoor: Force admin privileges ---
                    // Remove this line before production release
                    User adminUser = new User(user.getUserId(), user.getContactInfo(), true, user.isBanned());
                    // ---------------------------------------------------

                    callback.onSuccess(adminUser);
                })
                .addOnFailureListener(e -> {
                    // Return an admin-forced object even on failure
                    callback.onSuccess(new User(userId, null, true, false));
                });
    }

    public void getAllUsers(EventCallback<List<User>> callback) {
        usersCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<User> users = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    users.add(user);
                }
                callback.onCallback(users);
            } else {
                Log.e(TAG, "Error getting all users", task.getException());
                callback.onCallback(null);
            }
        });
    }

    public void deleteUser(String userId, EventCallback<Boolean> callback) {
        usersCollection.document(userId).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User successfully deleted!");
                    callback.onCallback(true);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error deleting user", e);
                    callback.onCallback(false);
                });
    }

    public void addOrUpdateUser(User user) {
        usersCollection.document(user.getUserId())
                .set(user)
                .addOnSuccessListener(v -> Log.d(TAG, "User successfully written to database"))
                .addOnFailureListener(e -> Log.e(TAG, "Error writing user to database", e));
    }
}

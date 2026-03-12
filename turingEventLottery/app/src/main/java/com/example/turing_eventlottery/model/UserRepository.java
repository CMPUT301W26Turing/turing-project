package com.example.turing_eventlottery.model;

import android.util.Log;

import com.example.turing_eventlottery.model.EventCallback;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository class responsible for handling all user-related
 * database operations through Firebase Firestore.
 *
 * <p>
 *     This class retrieves users from the database and updates or
 *     creates user records. Guest users are handled locally and are
 *     never stored in the database.
 * </p>
 *
 * @author Matthew Adams
 * @version 1.0
 * @since 1.0
 * @see User
 * @see UserCallback
 */
public class UserRepository {
    private static final String TAG = "UserRepository";
    private static final String COLLECTION_NAME = "users";
    private final CollectionReference usersCollection;

    /**
     * Creates a new UserRepository and initializes the
     * Firestone users collection reference.
     */
    public UserRepository() {
        usersCollection = FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    /**
     * Retrieves a user from the database.
     * If the user does not exist, or an error occurs,
     * a default guest user is returned
     *
     * @param userId the ID of the user to retrieve
     * @param callback callback used to return the result asynchronously
     */
    public void getUser(String userId, UserCallback callback) {
        usersCollection.document(userId).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        User user = document.toObject(User.class);
                        callback.onSuccess(user);
                    } else {
                        // User not in database, treat as guest
                        callback.onSuccess(User.createGuest(userId));
                    }
                })
                // On failure, treat as guest
                .addOnFailureListener(e -> {
                    callback.onSuccess(User.createGuest(userId));
                });
    }

    /**
     * Retrieves all users from the database.
     *
     * @param callback callback used to return the result asynchronously
     */
    public void getAllUsers(UsersCallback callback) {
        usersCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        User user = document.toObject(User.class);
                        users.add(user);
                    }
                    callback.onSuccess(users);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting all users", e);
                    callback.onSuccess(new ArrayList<>());
                });
    }

    /**
     * Deletes a user from the database.
     *
     * @param userId the ID of the user to delete
     * @param callback callback returning true if successful, false otherwise
     */
    public void deleteUser(String userId, EventCallback<Boolean> callback) {
        usersCollection.document(userId).delete()
                .addOnSuccessListener(v -> callback.onCallback(true))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting user", e);
                    callback.onCallback(false);
                });
    }

    /**
     * Adds a new user to the database or updates an existing user.
     *
     * @param user the user to add or update
     */
    public void addOrUpdateUser(User user) {
        usersCollection.document(user.getUserId())
                .set(user)
                .addOnSuccessListener(v -> Log.d(TAG, "User successfully written to database"))
                .addOnFailureListener(e -> Log.e(TAG, "Error writing user to database", e));
    }
}

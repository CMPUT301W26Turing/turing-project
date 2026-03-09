package com.example.turing_eventlottery.model;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
        usersCollection = FirebaseFirestore.getInstance().collection("users");
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
     * Adds a new user to the database or updates an existing user.
     * Guest users are not stored in the database.
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

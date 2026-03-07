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
    private static final User DEFAULT_GUEST_USER = new User("guest", true, false, false);
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
        if (userId.equals("guest")) {
            callback.onSuccess(DEFAULT_GUEST_USER);
            return;
        }

        usersCollection.document(userId).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        User user = document.toObject(User.class);
                        callback.onSuccess(user);
                    } else {
                        callback.onSuccess(DEFAULT_GUEST_USER);
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onSuccess(DEFAULT_GUEST_USER);
                });
    }

    /**
     * Adds a new user to the database or updates an existing user.
     * Guest users are not stored in the database.
     *
     * @param user the user to add or update
     */
    public void addOrUpdateUser(User user) {
        if (user.isGuest()) return; // never store guest user in the database

        usersCollection.document(user.getUserId())
                .set(user)
                .addOnSuccessListener(v -> Log.d(TAG, "User successfully written to database"))
                .addOnFailureListener(e -> Log.e(TAG, "Error writing user to database", e));
    }
}

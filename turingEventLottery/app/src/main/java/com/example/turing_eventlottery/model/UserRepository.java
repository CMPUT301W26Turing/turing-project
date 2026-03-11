package com.example.turing_eventlottery.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
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
                        callback.onSuccess(new User(userId, null, false, false));
                    }
                })
                // On failure, treat as guest
                .addOnFailureListener(e -> {
                    callback.onSuccess(new User(userId, null, false, false));
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

    /**
     * Logs in user using device ID.
     * Creates new user if not exists, or returns existing user.
     *
     * @param context Android context
     * @param callback callback to return result
     */
    public void loginWithDeviceId(Context context, UserCallback callback) {
        String deviceId = getDeviceId(context);

        // Try to find user with this device ID
        usersCollection.whereEqualTo("deviceId", deviceId)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // User exists - return first match
                        User existingUser = querySnapshot.getDocuments().get(0).toObject(User.class);
                        callback.onSuccess(existingUser);
                    } else {
                        // Create new user with device ID
                        String newUserId = java.util.UUID.randomUUID().toString();
                        User newUser = new User(newUserId, null, false, false);
                        newUser.setDeviceId(deviceId);

                        addOrUpdateUser(newUser);
                        callback.onSuccess(newUser);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Device login error", e);
                    // Fallback: create guest user
                    callback.onSuccess(new User(java.util.UUID.randomUUID().toString(), null, false, false));
                });
    }

    /**
     * Helper: Gets or creates a unique device ID
     */
    private String getDeviceId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("device_prefs", Context.MODE_PRIVATE);
        String deviceId = prefs.getString("device_id", null);

        if (deviceId == null) {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            // Fallback if ANDROID_ID is null
            if (deviceId == null) {
                deviceId = java.util.UUID.randomUUID().toString();
            }
            prefs.edit().putString("device_id", deviceId).apply();
        }
        return deviceId;
    }


}

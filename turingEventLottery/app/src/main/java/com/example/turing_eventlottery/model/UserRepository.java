package com.example.turing_eventlottery.model;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserRepository {
    private static final User DEFAULT_GUEST_USER = new User("guest", true, false, false);
    private static final String TAG = "UserRepository";
    private static final String COLLECTION_NAME = "users";

    private final CollectionReference usersCollection;

    public UserRepository() {
        usersCollection = FirebaseFirestore.getInstance().collection("users");
    }

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
    public void addOrUpdateUser(User user) {
        if (user.isGuest()) return; // never store guest user in the database

        usersCollection.document(user.getUserId())
                .set(user)
                .addOnSuccessListener(v -> Log.d(TAG, "User successfully written to database"))
                .addOnFailureListener(e -> Log.e(TAG, "Error writing user to database", e));
    }
}

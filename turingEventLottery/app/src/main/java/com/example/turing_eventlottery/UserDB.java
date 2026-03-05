package com.example.turing_eventlottery;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
public class UserDB {
    private FirebaseFirestore db;

    public UserDB(Context context) {
        FirebaseApp.initializeApp(context);
        this.db = FirebaseFirestore.getInstance();
    }

    public Task<DocumentSnapshot> getUser(String userId) {
        return db.collection("users").document(userId).get();
    }

    public Task<Void> saveUser(User user) {
        return db.collection("users").document(user.getUserId()).set(user);
    }
}

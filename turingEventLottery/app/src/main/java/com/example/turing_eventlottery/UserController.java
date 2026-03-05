package com.example.turing_eventlottery;

import android.content.Context;
import android.provider.Settings;

import com.google.android.gms.tasks.OnSuccessListener;

public class UserController {
    private UserDB userDB;
    private Context context;
    public UserController(Context context) {
        this.context = context;
        this.userDB = new UserDB(context);
    }

    private String getDeviceId() {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public void getOrCreateUser(String contactInfo, OnSuccessListener<User> onSuccess, onFailureListener onFailer) {
        String deviceId = getDeviceId();

        userDB.getUser(deviceId).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);
                onSuccess.onSuccess(user);
            } else {
                User newUser = new User(deviceId, contactInfo);
                userDB.saveUser(newUser).addOnSuccessListener(aVoid -> onSuccess.onSuccess(newUser))
                        .addOnFailureListener(onFailure);
            }
        });
    }
}
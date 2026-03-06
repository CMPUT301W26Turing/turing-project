package com.example.turing_eventlottery.viewmodel;

import android.content.Context;
import android.provider.Settings;

import com.example.turing_eventlottery.model.User;
import com.example.turing_eventlottery.model.UserRepository;

public class UserViewModel {
    private User user;
    private Context context;

    public UserViewModel(Context context) {
        this.context = context;
        this.user = new User();
    }

    public String getDeviceId() {
        return Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
    }

    public String loadUser() {
        String deviceId = getDeviceId();
        return user.getUserId();
    }

    public void updateContactInfo(String contactInfo) {
        String deviceId = getDeviceId();
        user.setContactInfo(contactInfo);
    }
}

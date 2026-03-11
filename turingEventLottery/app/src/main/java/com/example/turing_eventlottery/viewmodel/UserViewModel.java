package com.example.turing_eventlottery.viewmodel;

import android.content.Context;
import android.provider.Settings;

import com.example.turing_eventlottery.model.EventCallback;
import com.example.turing_eventlottery.model.User;
import com.example.turing_eventlottery.model.UserRepository;
import com.example.turing_eventlottery.model.UserCallback;

import java.util.List;

/**
 * ViewModel responsible for managing user-related data
 * for the application views. This class acts as the intermediary between
 * the View layer and the User model.
 */
public class UserViewModel {
    private User user;
    private Context context;
    private UserRepository userRepository;

    /**
     * Creates a UserViewModel and initializes the user object.
     *
     * @param context the application context
     */
    public UserViewModel(Context context) {
        this.context = context;
        this.user = new User();
        this.userRepository = new UserRepository();
    }

    /**
     * Retrieves the unique Android device ID
     *
     * @return the device ID used to identify the user
     */
    public String getDeviceId() {
        return Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
    }

    /**
     * Loads the current user that is associated
     * with the device.
     */
    public void loadUser(UserCallback callback) {
        String deviceId = getDeviceId();
        userRepository.getUser(deviceId, callback);
    }

    /**
     * Loads a specific user by ID.
     */
    public void loadUserById(String userId, UserCallback callback) {
        userRepository.getUser(userId, callback);
    }

    /**
     * Retrieves all users in the system.
     */
    public void getAllUsers(EventCallback<List<User>> callback) {
        userRepository.getAllUsers(callback);
    }

    /**
     * Deletes a user by ID.
     */
    public void deleteUser(String userId, EventCallback<Boolean> callback) {
        userRepository.deleteUser(userId, callback);
    }

    /**
     * Updates the contact information for the current user.
     *
     * @param contactInfo the new contact information
     */
    public void updateContactInfo(String contactInfo) {
        user.setContactInfo(contactInfo);
        // Note: You might want to persist this change using userRepository.addOrUpdateUser(user)
    }
}

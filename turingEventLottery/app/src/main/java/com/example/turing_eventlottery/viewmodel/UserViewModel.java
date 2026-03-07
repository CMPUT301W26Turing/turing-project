package com.example.turing_eventlottery.viewmodel;

import android.content.Context;
import android.provider.Settings;

import com.example.turing_eventlottery.model.User;
import com.example.turing_eventlottery.model.UserRepository;

/**
 * ViewModel responsible for managin user-related data
 * for the application views. This class acts as the intermediary between
 * the View layer and the User model.
 *
 * <p>
 *     It provides methods to retrieve the device ID
 *     and update user information.
 * </p>
 *
 * @author Matthew Adams
 * @version 1.0
 * @since 1.0
 * @see User
 * @see UserRepository
 */
public class UserViewModel {
    private User user;
    private Context context;

    /**
     * Creates a UserViewModel and initializes the user object.
     *
     * @param context the application context
     */
    public UserViewModel(Context context) {
        this.context = context;
        this.user = new User();
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
     * Updates the contact information from the current user.
     *
     * @return the user ID of the current user
     */
    public String loadUser() {
        String deviceId = getDeviceId();
        return user.getUserId();
    }

    /**
     * Updates the contact information for the current user.
     *
     * @param contactInfo the new contact information
     */
    public void updateContactInfo(String contactInfo) {
        String deviceId = getDeviceId();
        user.setContactInfo(contactInfo);
    }
}

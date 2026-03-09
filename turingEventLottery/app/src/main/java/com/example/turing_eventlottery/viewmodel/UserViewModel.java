package com.example.turing_eventlottery.viewmodel;

import android.content.Context;
import android.provider.Settings;

import com.example.turing_eventlottery.model.User;
import com.example.turing_eventlottery.model.UserRepository;
import com.example.turing_eventlottery.model.UserCallback;

/**
 * ViewModel responsible for managing user-related data
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
     *
     * @return the user ID of the current user
     */
    public void loadUser(UserCallback callback) {
        String deviceId = getDeviceId();
        userRepository.getUser(deviceId, loadedUser -> {
            user = loadedUser;
            callback.onSuccess(loadedUser);
        });
    }

    public void updateUserProfile(String name, String email, String phone) {
        user.setUserName(name);
        user.setUserEmail(email);
        user.setUserPhoneNumber(phone);

        userRepository.addOrUpdateUser(user);
    }
}

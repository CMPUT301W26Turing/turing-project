package com.example.turing_eventlottery.viewmodel;

import android.content.Context;
import android.provider.Settings;

import com.example.turing_eventlottery.model.EventCallback;
import com.example.turing_eventlottery.model.User;
import com.example.turing_eventlottery.model.UserRepository;
import com.example.turing_eventlottery.model.UserCallback;
import com.example.turing_eventlottery.model.UsersCallback;

import java.util.List;

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
 * @version 1.1
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
     * @param callback callback used to return the result asynchronously
     */
    public void loadUser(UserCallback callback) {
        String deviceId = getDeviceId();
        userRepository.getUser(deviceId, loadedUser -> {
            user = loadedUser;
            callback.onSuccess(loadedUser);
        });
    }

    /**
     * Retrieves all users from the database.
     *
     * @param callback callback used to return the result asynchronously
     */
    public void getAllUsers(UsersCallback callback) {
        userRepository.getAllUsers(callback);
    }

    /**
     * Loads a user by their ID (for admin viewing other profiles).
     *
     * @param userId the ID of the user to load
     * @param callback callback used to return the result asynchronously
     */
    public void loadUserById(String userId, UserCallback callback) {
        userRepository.getUser(userId, callback);
    }

    /**
     * Deletes a user from the database.
     *
     * @param userId the ID of the user to delete
     * @param callback callback returning true if successful, false otherwise
     */
    public void deleteUser(String userId, EventCallback<Boolean> callback) {
        userRepository.deleteUser(userId, callback);
    }

    /**
     * Edits the personal information fields of the current user
     *
     * @param name the user's full name
     * @param email the user's email
     * @param phone the user's phone number
     */
    public void updateUserProfile(String name, String email, String phone) {
        user.setUserName(name);
        user.setUserEmail(email);
        user.setUserPhoneNumber(phone);

        userRepository.addOrUpdateUser(user);
    }

    /**
     * Updates the contact information of the current user
     *
     * @param email the user's email
     * @param phone the user's phone number
     */
    public void updateContactInfo(String email, String phone) {
        user.setUserEmail(email);
        user.setUserPhoneNumber(phone);
        userRepository.addOrUpdateUser(user);
    }
}

package com.example.turing_eventlottery.viewmodel;

import android.content.Context;
import android.provider.Settings;

import com.example.turing_eventlottery.model.ModelCallback;
import com.example.turing_eventlottery.model.User;
import com.example.turing_eventlottery.model.UserRepository;

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
 * @since 03-07-2026
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
    public void loadUser(ModelCallback<User> callback) {
        String deviceId = getDeviceId();
        userRepository.getUser(deviceId, loadedUser -> {
            user = loadedUser;
            callback.onCallback(loadedUser);
        });
    }

    /**
     * Retrieves all users from the database.
     *
     * @param callback callback used to return the result asynchronously
     */
    public void getAllUsers(ModelCallback<List<User>> callback) {
        userRepository.getAllUsers(callback);
    }

    /**
     * Loads a user by their ID (for admin viewing other profiles).
     *
     * @param userId the ID of the user to load
     * @param callback callback used to return the result asynchronously
     */
    public void loadUserById(String userId, ModelCallback<User> callback) {
        userRepository.getUser(userId, callback);
    }

    /**
     * Deletes a user from the database.
     *
     * @param userId the ID of the user to delete
     * @param callback callback returning true if successful, false otherwise
     */
    public void deleteUser(String userId, ModelCallback<Boolean> callback) {
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

    /**
     * Retrieves all organizers from the database.
     *
     * @param callback callback used to return the result asynchronously
     */
    public void getAllOrganizers(ModelCallback<List<User>> callback) {
        userRepository.getAllOrganizers(callback);
    }

    /**
     * Sets the current user as an organizer.
     *
     * @param callback callback returning true if successful, false otherwise
     */
    public void setOrganizerStatus(ModelCallback<Boolean> callback) {
        String deviceId = getDeviceId();
        userRepository.setOrganizerStatus(deviceId, callback);
    }

    /**
     * Removes (bans) an organizer account from the system.
     * The organizer will no longer be able to create or manage events.
     *
     * @param organizerId the ID of the organizer to remove
     * @param callback callback returning true if successful, false otherwise
     */
    public void removeOrganizer(String organizerId, ModelCallback<Boolean> callback) {
        userRepository.removeOrganizer(organizerId, callback);
    }
}

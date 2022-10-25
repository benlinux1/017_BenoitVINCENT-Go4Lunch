package com.benlinux.go4lunch.data.userManager;

import android.content.Context;
import android.net.Uri;

import com.benlinux.go4lunch.data.userRepository.UserRepository;
import com.benlinux.go4lunch.ui.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;

public class UserManager {

    private static volatile UserManager instance;
    private final UserRepository userRepository;

    private UserManager() {
        userRepository = UserRepository.getInstance();
    }

    public static UserManager getInstance() {
        UserManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized(UserRepository.class) {
            if (instance == null) {
                instance = new UserManager();
            }
            return instance;
        }
    }


    public FirebaseUser getCurrentUser(){
        return userRepository.getCurrentUser();
    }

    public Boolean isCurrentUserLogged(){
        return (this.getCurrentUser() != null);
    }

    public Task<Void> signOut(Context context){
        return userRepository.signOut(context);
    }

    public Task<Void> deleteUser(Context context){
        return userRepository.deleteUser(context);
    }

    public Task<QuerySnapshot> createUser(){
        return userRepository.createUser();
    }

    public Task<List<User>> getAllUsersData() {
        return Objects.requireNonNull(userRepository.getAllUsersData()).continueWith(task -> task.getResult().toObjects(User.class));
    }

    public Task<User> getUserData(){
        // Get the user from Firestore and cast it to a User model Object
        return Objects.requireNonNull(userRepository.getUserData()).continueWith(task -> task.getResult().toObject(User.class));
    }

    public void updateUsername(String username){
        userRepository.updateUsername(username);
    }

    public void updateUserAvatarUrl(Uri avatarUrl){
        userRepository.uploadImage(avatarUrl, "avatar").addOnSuccessListener(taskSnapshot -> {
            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                userRepository.updateUserAvatar(uri.toString());
            });
        });
    }

    public void updateUserRestaurantOfTheDay(String userId, String restaurantName){
        userRepository.updateUserRestaurantOfTheDay(userId, restaurantName);
    }

    public void updateUserRestaurantIdOfTheDay(String userId, String restaurantId){
        userRepository.updateUserRestaurantIdOfTheDay(userId, restaurantId);
    }

    public void updateIsNotified(Boolean isNotified){
        userRepository.updateIsNotified(isNotified);
    }

    public void addRestaurantToFavorites(String restaurantId){
        userRepository.addRestaurantToFavorites(restaurantId);
    }
    public void removeRestaurantFromFavorites(String restaurantId){
        userRepository.removeRestaurantFromFavorites(restaurantId);
    }

    public Task<Void> deleteUserFromFirestore(Context context){
        // Delete the user account from the Auth
        return userRepository.deleteUserFromFirestore(context);
    }

}
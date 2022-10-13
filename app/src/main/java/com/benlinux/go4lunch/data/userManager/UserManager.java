package com.benlinux.go4lunch.data.userManager;

import android.content.Context;
import android.net.Uri;

import com.benlinux.go4lunch.data.userRepository.UserRepository;
import com.benlinux.go4lunch.ui.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

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

    public void createUser(){
        userRepository.createUser();
    }


    public Task<User> getUserData(){
        // Get the user from Firestore and cast it to a User model Object
        return Objects.requireNonNull(userRepository.getUserData()).continueWith(task -> task.getResult().toObject(User.class)) ;
    }



    public Task<Void> updateUsername(String username){
        return userRepository.updateUsername(username);
    }


    public void updateUserAvatarUrl(Uri avatarUrl){
        userRepository.uploadImage(avatarUrl, "avatar").addOnSuccessListener(taskSnapshot -> {
            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                userRepository.updateUserAvatar(uri.toString());
            });
        });
    }

    public Task<Void> updateUserEmail(String email){
        return userRepository.updateUserEmail(email);
    }

    public Task<Void> updateUserRestaurantOfTheDay(String restaurantName){
        return userRepository.updateUserRestaurantOfTheDay(restaurantName);
    }

    public void updateIsNotified(Boolean isNotified){
        userRepository.updateIsNotified(isNotified);
    }

    public void addRestaurantToFavorites(String restaurantId){
        userRepository.addRestaurantToFavorites(restaurantId);
    }
    public Task<Void> removeRestaurantFromFavorites(String restaurantId){
        return userRepository.removeRestaurantFromFavorites(restaurantId);
    }

    public Task<Void> deleteUserFromFirestore(Context context){
        // Delete the user account from the Auth
        return userRepository.deleteUserFromFirestore(context);
    }

}
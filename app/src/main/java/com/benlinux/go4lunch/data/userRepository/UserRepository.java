package com.benlinux.go4lunch.data.userRepository;

import android.content.Context;

import androidx.annotation.Nullable;

import com.benlinux.go4lunch.ui.models.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public final class UserRepository {

    // FIRESTORE DATA
    private static final String COLLECTION_NAME = "users";
    private static final String USERNAME_FIELD = "name";
    private static final String EMAIL_FIELD = "email";
    private static final String AVATAR_FIELD = "avatar";
    private static final String RESTAURANT_FIELD = "restaurantOfTheDay";
    private static final String IS_NOTIFIED_FIELD = "isNotified";

    private static volatile UserRepository instance;

    private UserRepository() { }

    public static UserRepository getInstance() {
        UserRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized(UserRepository.class) {
            if (instance == null) {
                instance = new UserRepository();
            }
            return instance;
        }
    }


    @Nullable
    public FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    private String getCurrentUserUID() {
        FirebaseUser user = getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    public Task<Void> signOut(Context context){
        return AuthUI.getInstance().signOut(context);
    }

    public Task<Void> deleteUser(Context context){
        return AuthUI.getInstance().delete(context);
    }


    // Get the Collection Reference in Firestore Database
    private CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // Create User in Firestore
    public void createUser() {
        FirebaseUser user = getCurrentUser();
        if(user != null){
            String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
            String username = user.getDisplayName();
            String uid = user.getUid();
            String email = user.getEmail();

            User userToCreate = new User(uid, username, email, urlPicture, null, true);

            Task<DocumentSnapshot> userData = getUserData();
            // If the user already exist in Firestore, we get his data (isMentor)
            userData.addOnSuccessListener(documentSnapshot -> {
                this.getUsersCollection().document(uid).set(userToCreate);
            });
        }
    }

    // Get User Data from Firestore
    public Task<DocumentSnapshot> getUserData(){
        String uid = this.getCurrentUserUID();
        if(uid != null){
            return this.getUsersCollection().document(uid).get();
        } else {
            return null;
        }
    }



    // Update User Username
    public Task<Void> updateUsername(String username) {
        String uid = this.getCurrentUserUID();
        if(uid != null) {
            return this.getUsersCollection().document(uid).update(USERNAME_FIELD, username);
        } else {
            return null;
        }
    }

    // Update User Email
    public Task<Void> updateUserEmail(String email) {
        String uid = this.getCurrentUserUID();
        if(uid != null) {
            return this.getUsersCollection().document(uid).update(EMAIL_FIELD, email);
        } else {
            return null;
        }
    }

    // Update User Avatar
    public Task<Void> updateUserAvatar(String avatarUrl) {
        String uid = this.getCurrentUserUID();
        if(uid != null) {
            return this.getUsersCollection().document(uid).update(AVATAR_FIELD, avatarUrl);
        } else {
            return null;
        }
    }

    // Update User Restaurant of the Day
    public Task<Void> updateUserRestaurantOfTheDay(String restaurantName) {
        String uid = this.getCurrentUserUID();
        if(uid != null) {
            return this.getUsersCollection().document(uid).update(RESTAURANT_FIELD, restaurantName);
        } else {
            return null;
        }
    }

    // Update User isNotified
    public void updateIsNotified(Boolean isNotified) {
        String uid = this.getCurrentUserUID();
        if(uid != null) {
            this.getUsersCollection().document(uid).update(IS_NOTIFIED_FIELD, isNotified);
        }
    }

    // Delete the User from Firestore
    public void deleteUserFromFirestore() {
        FirebaseUser user = getCurrentUser();
        assert user != null;
        String uid = user.getUid();
        this.getUsersCollection().document(uid).delete();
    }
}
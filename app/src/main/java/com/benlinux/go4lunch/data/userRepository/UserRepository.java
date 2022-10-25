package com.benlinux.go4lunch.data.userRepository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import com.benlinux.go4lunch.ui.models.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class UserRepository {

    // FIRESTORE DATA
    private static final String COLLECTION_NAME = "users";
    private static final String USERNAME_FIELD = "name";
    private static final String EMAIL_FIELD = "email";
    private static final String AVATAR_FIELD = "avatar";
    private static final String RESTAURANT_FIELD = "restaurantName";
    private static final String BOOKINGID_FIELD = "restaurantId";
    private static final String NOTIFIED_FIELD = "notified";
    private static final String FAVORITES_FIELD = "favoriteRestaurants";

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
    public Task<QuerySnapshot> createUser() {
        FirebaseUser user = getCurrentUser();
        if(user != null){

            List<? extends UserInfo> providerData = user.getProviderData();

            String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
            String username = user.getDisplayName();
            String uid = user.getUid();
            String email = user.getEmail();
            if (email == null) {
                email = providerData.get(1).getEmail();
            }

            List<String> favorites = Collections.emptyList();

            User userToCreate = new User(uid, username, email, urlPicture, "", "", true, favorites);

            // Check if user exists in database
            return getAllUsersData().addOnSuccessListener(querySnapshot -> {
                // Get all users
                List<User> users = querySnapshot.toObjects(User.class);
                boolean userExists = false;
                // Check if user id exists in database
                for (int i=0; i<users.size(); i++) {
                    if (users.get(i).getId().equals(uid)) {
                        // If exists, set boolean to true
                        userExists = true;
                        break;
                    }
                }
                // If user exists, don't do anything
                if (userExists) {
                    Log.d("USER CREATION INFO", "user already exists");
                // If user doesn't exist, create it in FireStore
                } else {
                    this.getUsersCollection().document(uid).set(userToCreate);
                }
            });
        } else {
            return null;
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


    // Get all users from Firestore
    public Task<QuerySnapshot> getAllUsersData() {
        return this.getUsersCollection().orderBy(RESTAURANT_FIELD, Query.Direction.DESCENDING).get();
    }


    // Update Username in FireStore
    public Task<Void> updateUsername(String username) {
        String uid = this.getCurrentUserUID();
        if(uid != null) {
            return this.getUsersCollection().document(uid).update(USERNAME_FIELD, username);
        } else {
            return null;
        }
    }

    // Update User Email in FireStore
    public Task<Void> updateUserEmail(String email) {
        String uid = this.getCurrentUserUID();
        if(uid != null) {
            return this.getUsersCollection().document(uid).update(EMAIL_FIELD, email);
        } else {
            return null;
        }
    }

    // Update User Avatar in FireStore
    public void updateUserAvatar(String avatarUrl) {
        String uid = this.getCurrentUserUID();
        if(uid != null) {
            this.getUsersCollection().document(uid).update(AVATAR_FIELD, avatarUrl);
        }
    }

    // Update User Restaurant of the Day in FireStore
    public void updateUserRestaurantOfTheDay(String userId, String restaurantName) {
        this.getUsersCollection().document(userId).update(RESTAURANT_FIELD, restaurantName);

    }

    // Update User Restaurant Id of the Day in FireStore
    public void updateUserRestaurantIdOfTheDay(String userId, String restaurantId) {
        this.getUsersCollection().document(userId).update(BOOKINGID_FIELD, restaurantId);

    }

    // Update User isNotified in FireStore
    public void updateIsNotified(Boolean isNotified) {
        String uid = this.getCurrentUserUID();
        if(uid != null) {
            this.getUsersCollection().document(uid).update(NOTIFIED_FIELD, isNotified);
        }
    }

    // Add restaurant to favorites in FireStore
    public void addRestaurantToFavorites(String restaurantId) {
        String uid = this.getCurrentUserUID();
        if (uid != null) {
            this.getUsersCollection().document(uid).update(FAVORITES_FIELD, FieldValue.arrayUnion(restaurantId));
        }
    }

    // Remove restaurant from favorites in FireStore
    public Task<Void> removeRestaurantFromFavorites(String restaurantId) {
        String uid = this.getCurrentUserUID();
        if(uid != null) {
            return this.getUsersCollection().document(uid).update(FAVORITES_FIELD, FieldValue.arrayRemove(restaurantId));
        } else {
            return null;
        }
    }

    // Upload image from device to firebase storage
    public UploadTask uploadImage(Uri imageUri, String pictures){
        String uuid = UUID.randomUUID().toString(); // GENERATE UNIQUE STRING
        StorageReference mImageRef = FirebaseStorage.getInstance().getReference(pictures + "/" + uuid);
        return mImageRef.putFile(imageUri);
    }

    // Delete the User from Firestore
    // if result ok, delete from firebase & logout
    public Task<Void> deleteUserFromFirestore(Context context) {
        String uid = this.getCurrentUserUID();
        return this.getUsersCollection().document(uid).delete();
    }

}
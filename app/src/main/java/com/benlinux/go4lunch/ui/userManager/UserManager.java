package com.benlinux.go4lunch.ui.userManager;

import android.content.Context;

import com.benlinux.go4lunch.ui.userRepository.UserRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

public class UserManager {

    private static volatile UserManager instance;
    private UserRepository userRepository;

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

}
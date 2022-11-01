package com.benlinux.go4lunch.Repositories;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.benlinux.go4lunch.activities.MainActivity;
import com.benlinux.go4lunch.data.userManager.UserManager;
import com.benlinux.go4lunch.data.userRepository.UserRepository;
import com.benlinux.go4lunch.ui.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class UserRepositoryTest {

    List<User> fakeUserList = new ArrayList<>();
    final ArrayList<String> favoritesRestaurants1 = new ArrayList<>();

    final User user1 = new User( "1", "user 1", "user1@test.com", "https://www.avatar.com/testavatar1", "restaurant name 1",
            "restaurant address 1", "1", true, favoritesRestaurants1);

    final User user2 = new User( "2", "user 2", "user2@test.com", null, "restaurant name 2",
            "restaurant address 2", "2", true, new ArrayList<>());

    final User user3 = new User( "3", "user 3", "user3@test.com", "https://www.avatar.com/testavatar3", "restaurant name 3",
            "restaurant address 3", "3", false, null);


    UserRepository userRepository = Mockito.mock(UserRepository.class);
    UserManager userManager = Mockito.mock(UserManager.class);
    MainActivity mainActivity = Mockito.mock(MainActivity.class);


    @Test
    // Check that get all users data method returns all users data
    public void userManagerGetAllUsersData() {
        when(userManager.getAllUsersData()).thenReturn(getAllUsersData);
        assertEquals(userManager.getAllUsersData(), getAllUsersData);
        assertEquals(userManager.getAllUsersData().getResult(), fakeUserList);
    }

    @Test
    // Check that get user data method returns user 1 data
    public void userManagerGetUserData() {
        when(userManager.getUserData()).thenReturn(getUserData);
        assertEquals(userManager.getUserData(), getUserData);
        assertEquals(getUserData.getResult(), user1);
    }

    @Test
    // Check that user manager delete user from repository
    public void userManagerDeleteUser() {
        assertEquals(userManager.deleteUser(mainActivity.getApplicationContext()), userRepository.deleteUser(mainActivity.getApplicationContext()));
    }

    @Test
    // Check that user manager sign out user from repository
    public void userManagerSignOut() {
        assertEquals(userManager.signOut(mainActivity.getApplicationContext()), userRepository.signOut(mainActivity.getApplicationContext()));
    }

    @Test
    // Check that user manager sign out user from repository
    public void userManagerGetCurrentUser() {
        assertEquals(userManager.getCurrentUser(), userRepository.getCurrentUser());
    }

    @Test
    // Check that user manager create user in repository
    public void userManagerCreateUser() {
        assertEquals(userManager.createUser(), userRepository.createUser());
    }



    //////////// SIMULATION METHODS ///////////////

    // Simulate user repository
    public Task<List<User>> getAllUsersData = new Task<List<User>>() {

        @NonNull
        @Override
        public Task<List<User>> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
            return getAllUsersData;
        }

        @NonNull
        @Override
        public Task<List<User>> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
            return getAllUsersData;
        }

        @NonNull
        @Override
        public Task<List<User>> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
            return getAllUsersData;
        }

        @NonNull
        @Override
        public Task<List<User>> addOnSuccessListener(@NonNull OnSuccessListener<? super List<User>> onSuccessListener) {
            return getAllUsersData;
        }

        @NonNull
        @Override
        public Task<List<User>> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super List<User>> onSuccessListener) {
            return getAllUsersData;
        }

        @NonNull
        @Override
        public Task<List<User>> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super List<User>> onSuccessListener) {
            return getAllUsersData;
        }

        @Nullable
        @Override
        public Exception getException() {
            return null;
        }

        @Override
        public List<User> getResult() {
            fakeUserList.add(user1);
            fakeUserList.add(user2);
            fakeUserList.add(user3);
            return fakeUserList;
        }

        @Override
        public <X extends Throwable> List<User> getResult(@NonNull Class<X> aClass) {
            return null;
        }

        @Override
        public boolean isCanceled() {
            return getAllUsersData.isCanceled();
        }

        @Override
        public boolean isComplete() {
            return getAllUsersData.isComplete();
        }

        @Override
        public boolean isSuccessful() {
            return getAllUsersData.isSuccessful();
        }
    };


    // Simulate current user
    public Task<User> getUserData = new Task<User>() {
        @NonNull
        @Override
        public Task<User> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
            return getUserData;
        }

        @NonNull
        @Override
        public Task<User> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
            return getUserData;
        }

        @NonNull
        @Override
        public Task<User> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
            return getUserData;
        }

        @NonNull
        @Override
        public Task<User> addOnSuccessListener(@NonNull OnSuccessListener<? super User> onSuccessListener) {
            return getUserData;
        }

        @NonNull
        @Override
        public Task<User> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super User> onSuccessListener) {
            return getUserData;
        }

        @NonNull
        @Override
        public Task<User> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super User> onSuccessListener) {
            return getUserData;
        }

        @Nullable
        @Override
        public Exception getException() {
            return null;
        }

        @Override
        public User getResult() {
            return user1;
        }

        @Override
        public <X extends Throwable> User getResult(@NonNull Class<X> aClass) throws X {
            return null;
        }

        @Override
        public boolean isCanceled() {
            return getUserData.isCanceled();
        }

        @Override
        public boolean isComplete() {
            return getUserData.isComplete();
        }

        @Override
        public boolean isSuccessful() {
            return getUserData.isSuccessful();
        }
    };


}

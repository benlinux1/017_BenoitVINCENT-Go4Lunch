package com.benlinux.go4lunch;

import static org.junit.Assert.assertEquals;

import com.benlinux.go4lunch.activities.MainActivity;
import com.benlinux.go4lunch.data.userManager.UserManager;
import com.benlinux.go4lunch.data.userRepository.UserRepository;
import com.benlinux.go4lunch.ui.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Objects;

public class MainActivityTest {

    //Create a mock object of the class MainActivity
    //MainActivity mainActivity = Mockito.mock(MainActivity.class);
    //UserManager userManager = Mockito.mock(UserManager.class);
    //UserRepository userRepository = Mockito.mock(UserRepository.class);

    final User currentUser = new User( "1", "user 1", "user1@test.com", "https://www.avatar.com/testavatar1", "restaurant name 1",
            "restaurant address 1", "1", true, null);

    private User getUserData(){
        // Get the user from Firestore and cast it to a User model Object
        return currentUser;
    }



    @Test
    public void DrawerNavGetsUserName() {


    }

    @Test
    public void DrawerNavGetsUserEmail() {

    }

    @Test
    public void DrawerNavGetsUserAvatar() {

    }

    @Test
    public void SearchBarGetsAutoCompleteText() {

    }
}

package com.benlinux.go4lunch;

import static org.junit.Assert.assertEquals;

import com.benlinux.go4lunch.activities.MainActivity;
import com.benlinux.go4lunch.ui.models.User;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

public class MainActivityTest {

    //Create a mock object of the class MainActivity
    MainActivity mainActivity = mock(MainActivity.class);

    final User currentUser = new User( "1", "user 1", "user1@test.com", "https://www.avatar.com/testavatar1", "restaurant name 1",
            "restaurant address 1", "1", true, null);

    public User getUserData(){
        // Get the user from Firestore and cast it to a User model Object
        return currentUser;
    }


    @Test
    public void DrawerNavGetsUserName() throws Exception {
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

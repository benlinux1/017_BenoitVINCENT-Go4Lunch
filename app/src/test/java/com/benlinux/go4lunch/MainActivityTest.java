package com.benlinux.go4lunch;

import com.benlinux.go4lunch.ui.models.User;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;


public class MainActivityTest {

    //Create a mock object of the class MainActivity


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

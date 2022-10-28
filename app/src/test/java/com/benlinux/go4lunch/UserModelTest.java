package com.benlinux.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.benlinux.go4lunch.ui.models.Restaurant;
import com.benlinux.go4lunch.ui.models.User;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import java.util.ArrayList;


public class UserModelTest {

    @Test
    public void userModelTest() {

        final ArrayList<String> favoritesRestaurants1 = new ArrayList<>();
        final String stringExample = "restaurant example 1";
        favoritesRestaurants1.add(stringExample);


        final User user1 = new User( "1", "user 1", "user1@test.com", "", "restaurant name 1",
                "restaurant address 1", "1", true, favoritesRestaurants1);

        final User user2 = new User( "2", "user 2", "user2@test.com", "", "restaurant name 2",
                "restaurant address 2", "2", true, new ArrayList<>());

        final User user3 = new User( "3", "user 3", "user3@test.com", "", "restaurant name 3",
                "restaurant address 3", "3", true, null);



        // Test for user id
        assertEquals("1", user1.getId());
        assertEquals("2", user2.getId());
        assertEquals("3", user3.getId());


        // Test for user name
        assertEquals("user 1", user1.getName());
        assertEquals("user 2", user2.getName());
        assertEquals("user 3", user3.getName());


        // Test for user email
        assertEquals("user1@test.com", user1.getEmail());
        assertEquals("user2@test.com", user2.getEmail());
        assertEquals("user3@test.com", user3.getEmail());




    }
}

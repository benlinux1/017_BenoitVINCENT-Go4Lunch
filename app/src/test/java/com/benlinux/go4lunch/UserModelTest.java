package com.benlinux.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.benlinux.go4lunch.ui.models.Restaurant;
import com.benlinux.go4lunch.ui.models.User;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import java.util.ArrayList;


public class UserModelTest {

    final ArrayList<String> favoritesRestaurants1 = new ArrayList<>();


    final User user1 = new User( "1", "user 1", "user1@test.com", "https://www.avatar.com/testavatar1", "restaurant name 1",
            "restaurant address 1", "1", true, favoritesRestaurants1);

    final User user2 = new User( "2", "user 2", "user2@test.com", null, "restaurant name 2",
            "restaurant address 2", "2", true, new ArrayList<>());

    final User user3 = new User( "3", "user 3", "user3@test.com", "https://www.avatar.com/testavatar3", "restaurant name 3",
            "restaurant address 3", "3", false, null);


    @Test
    public void userGetId() {
        // Test for user id
        assertEquals("1", user1.getId());
        assertEquals("2", user2.getId());
        assertEquals("3", user3.getId());

    }

    @Test
    public void userGetName() {
        // Test for user name
        assertEquals("user 1", user1.getName());
        assertEquals("user 2", user2.getName());
        assertEquals("user 3", user3.getName());
    }


    @Test
    public void userGetEmail() {
        // Test for user email
        assertEquals("user1@test.com", user1.getEmail());
        assertEquals("user2@test.com", user2.getEmail());
        assertEquals("user3@test.com", user3.getEmail());
    }


    @Test
    public void userGetAvatar() {
        // Test for user avatar
        assertEquals("https://www.avatar.com/testavatar1", user1.getAvatar());
        assertNull(user2.getAvatar());
        assertEquals("https://www.avatar.com/testavatar3", user3.getAvatar());
    }


    @Test
    public void userGetRestaurantName() {
        // Test for user restaurant of day
        assertEquals("restaurant name 1", user1.getRestaurantName());
        assertEquals("restaurant name 2", user2.getRestaurantName());
        assertEquals("restaurant name 3", user3.getRestaurantName());
    }


    @Test
    public void userGetRestaurantAddress() {
        // Test for user restaurant's address of day
        assertEquals("restaurant address 1", user1.getRestaurantAddress());
        assertEquals("restaurant address 2", user2.getRestaurantAddress());
        assertEquals("restaurant address 3", user3.getRestaurantAddress());
    }

    @Test
    public void userGetRestaurantId() {

        // Test for user restaurant id of day
        assertEquals("1", user1.getRestaurantId());
        assertEquals("2", user2.getRestaurantId());
        assertEquals("3", user3.getRestaurantId());
    }


    @Test
    public void userIsNotified() {
        // Test for user notifications
        assertEquals(Boolean.TRUE, user1.isNotified());
        assertEquals(Boolean.TRUE, user2.isNotified());
        assertEquals(Boolean.FALSE, user3.isNotified());
    }

    @Test
    public void userGetFavoritesList() {
        final String restaurant1 = "restaurant1";
        favoritesRestaurants1.add(restaurant1);

        // Test for user's favorites list
        assertEquals(Boolean.TRUE, user1.getFavoriteRestaurants().contains(restaurant1));
        assertEquals(new ArrayList<>(), user2.getFavoriteRestaurants());
        assertNull(user3.getFavoriteRestaurants());

    }

}

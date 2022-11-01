package com.benlinux.go4lunch.adapters;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import com.benlinux.go4lunch.activities.RestaurantDetailsActivity;
import com.benlinux.go4lunch.ui.adapters.GuestAdapter;
import com.benlinux.go4lunch.ui.models.User;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class GuestAdapterTest {

    @Test
    public void guestAdapterGetGuestData() {

        // Create empty guests list
        List<String> mGuest = new ArrayList<>();

        // Mock activity to get context
        RestaurantDetailsActivity guestActivity = mock(RestaurantDetailsActivity.class);

        // Define adapter
        GuestAdapter guestAdapter;

        // Set favorite restaurants list
        final ArrayList<String> favoritesRestaurants1 = new ArrayList<>();
        final String restaurant1 = "restaurant1";
        favoritesRestaurants1.add(restaurant1);

        // Set 3 workmates
        final User user1 = new User( "1", "user 1", "user1@test.com", "https://www.avatar.com/testavatar1", "restaurant name 1",
                "restaurant address 1", "1", true, favoritesRestaurants1);

        final User user2 = new User( "2", "user 2", "user2@test.com", null, "restaurant name 2",
                "restaurant address 2", "2", true, new ArrayList<>());

        final User user3 = new User( "3", "user 3", "user3@test.com", "https://www.avatar.com/testavatar3", "restaurant name 3",
                "restaurant address 3", "3", false, null);

        // Add this 3 workmates id to guest list
        mGuest.add(user1.getId());
        mGuest.add(user2.getId());
        mGuest.add(user3.getId());

        // Check bookings list size
        assertEquals(mGuest.size(), 3);

        // Define booking adapter
        guestAdapter = new GuestAdapter(mGuest, guestActivity.getApplicationContext(), true);

        // Check guest adapter get 3 items
        assertEquals(guestAdapter.getItemCount(), 3);

        // Check each item in adapter
        assertEquals(guestAdapter.getItem(0), user1.getId());
        assertEquals(guestAdapter.getItem(1), user2.getId());
        assertEquals(guestAdapter.getItem(2), user3.getId());
    }
}

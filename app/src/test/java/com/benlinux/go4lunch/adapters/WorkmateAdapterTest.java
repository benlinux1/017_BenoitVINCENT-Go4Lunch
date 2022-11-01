package com.benlinux.go4lunch.adapters;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import com.benlinux.go4lunch.activities.MainActivity;
import com.benlinux.go4lunch.ui.adapters.WorkmateAdapter;
import com.benlinux.go4lunch.ui.models.User;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class WorkmateAdapterTest {

    @Test
    public void workmateAdapterGetWorkmateData() {

        // Create empty bookings list
        List<User> mWorkmates = new ArrayList<>();

        // Mock activity to get context
        MainActivity workmatesActivity = mock(MainActivity.class);

        // Define adapter
        WorkmateAdapter workmateAdapter;

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

        // Add this 3 workmates to workmates list
        mWorkmates.add(user1);
        mWorkmates.add(user2);
        mWorkmates.add(user3);

        // Check bookings list size
        assertEquals(mWorkmates.size(), 3);

        // Define booking adapter
        workmateAdapter = new WorkmateAdapter(mWorkmates, workmatesActivity.getApplicationContext());

        // Check booking adapter get 3 items
        assertEquals(workmateAdapter.getItemCount(), 3);

        // Check workmate item in adapter
        assertEquals(workmateAdapter.getItem(0), user1);
        assertEquals(workmateAdapter.getItem(1), user2);
        assertEquals(workmateAdapter.getItem(2), user3);

        // Check data for each workmate
        assertEquals(workmateAdapter.getItem(0).getRestaurantName(), user1.getRestaurantName());
        assertEquals(workmateAdapter.getItem(0).getRestaurantId(), user1.getRestaurantId());
        assertEquals(workmateAdapter.getItem(0).getFavoriteRestaurants(), user1.getFavoriteRestaurants());
        assertEquals(workmateAdapter.getItem(1).getAvatar(), user2.getAvatar());
        assertEquals(workmateAdapter.getItem(1).getRestaurantAddress(), user2.getRestaurantAddress());
        assertEquals(workmateAdapter.getItem(2).getEmail(), user3.getEmail());
        assertEquals(workmateAdapter.getItem(2).getName(), user3.getName());
        assertEquals(workmateAdapter.getItem(2).getId(), user3.getId());

    }
}

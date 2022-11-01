package com.benlinux.go4lunch.adapters;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import com.benlinux.go4lunch.activities.MainActivity;
import com.benlinux.go4lunch.ui.adapters.ListAdapter;
import com.benlinux.go4lunch.ui.models.Booking;
import com.benlinux.go4lunch.ui.models.Restaurant;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ListAdapterTest {

    @Test
    public void listAdapterGetRestaurantData() {

        // Create empty restaurants list
        List<Restaurant> mRestaurants = new ArrayList<>();
        // Mock activity to get context
        MainActivity listActivity = mock(MainActivity.class);
        // Define adapter
        ListAdapter restaurantAdapter;

        // Set 1 booking
        final List<Booking> bookingsList1 = new ArrayList<>();
        Booking booking1 = new Booking("1", "1", "restaurant 1", "restaurant 1 address", null, "1", "28/10/2022");

        // Set 4 restaurants
        final Restaurant restaurant1 = new Restaurant("1", "restaurant 1", "address restaurant 1", 3.0,
                "Open from 11.00 to 15.00", "360m", new LatLng(50.5, 30.5), bookingsList1);

        final Restaurant restaurant2 = new Restaurant("2", "restaurant 2", "address restaurant 2", 2.5,
                "Open from 10.00 to 16.00", "420m", new LatLng(50.6, 30.6), null);

        final Restaurant restaurant3 = new Restaurant("3", "restaurant 3", "address restaurant 3", 2.0,
                "Open from 10.30 to 15.30", "1600m", new LatLng(50.7, 30.7), null);

        final Restaurant restaurant4 = new Restaurant("4", null, null, null, null, null, null, null);

        // Add this 3 bookings to bookings list
        mRestaurants.add(restaurant1);
        mRestaurants.add(restaurant2);
        mRestaurants.add(restaurant3);
        mRestaurants.add(restaurant4);

        // Check bookings list size
        assertEquals(mRestaurants.size(), 4);

        // Define booking adapter
        restaurantAdapter = new ListAdapter(mRestaurants, listActivity.getApplicationContext());

        // Check booking adapter get 3 items
        assertEquals(restaurantAdapter.getItemCount(), 4);

        // Check items in adapter
        assertEquals(restaurantAdapter.getItem(0), restaurant1);
        assertEquals(restaurantAdapter.getItem(1), restaurant2);
        assertEquals(restaurantAdapter.getItem(2), restaurant3);
        assertEquals(restaurantAdapter.getItem(3), restaurant4);


        // Check data for each item
        assertEquals(restaurantAdapter.getItem(0).getId(), restaurant1.getId());
        assertEquals(restaurantAdapter.getItem(0).getName(), restaurant1.getName());
        assertEquals(restaurantAdapter.getItem(0).getAddress(), restaurant1.getAddress());
        assertEquals(restaurantAdapter.getItem(0).getDistance(), restaurant1.getDistance());
        assertEquals(restaurantAdapter.getItem(1).getHours(), restaurant2.getHours());
        assertEquals(restaurantAdapter.getItem(1).getLatLng(), restaurant2.getLatLng());
        assertEquals(restaurantAdapter.getItem(1).getOpen(), restaurant2.getOpen());
        assertEquals(restaurantAdapter.getItem(2).getPictureUrl(), restaurant3.getPictureUrl());
        assertEquals(restaurantAdapter.getItem(2).getRating(), restaurant3.getRating());
        assertEquals(restaurantAdapter.getItem(2).getBookings(), restaurant3.getBookings());
        assertEquals(restaurantAdapter.getItem(2).getStyle(), restaurant3.getStyle());

    }

}


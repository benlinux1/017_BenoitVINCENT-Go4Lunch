package com.benlinux.go4lunch;

import org.junit.Test;

import static org.junit.Assert.*;

import com.benlinux.go4lunch.ui.models.Booking;
import com.benlinux.go4lunch.ui.models.Restaurant;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class RestaurantModelTest {

    @Test
    public void restaurantModelTest() {

        final List<Booking> bookingsList1 = new ArrayList<>();
        Booking booking1 = new Booking("1","1", "restaurant 1", "restaurant 1 address", null, "1", "28/10/2022");
        bookingsList1.add(booking1);

        final Restaurant restaurant1 = new Restaurant( "1", "restaurant 1", "address restaurant 1", 3.0,
                "Open from 11.00 to 15.00", "360m", new LatLng(50.5, 30.5), bookingsList1);

        final Restaurant restaurant2 = new Restaurant( "2", "restaurant 2", "address restaurant 2", 2.5,
                "Open from 10.00 to 16.00", "420m", new LatLng(50.6, 30.6), null);

        final Restaurant restaurant3 = new Restaurant( "3", "restaurant 3", "address restaurant 3", 2.0,
                "Open from 10.30 to 15.30", "1600m", new LatLng(50.7, 30.7), null);

        final Restaurant restaurant4 = new Restaurant("4", null, null,null,null,null,null,null);


        // Test for restaurants id
        assertEquals("1", restaurant1.getId());
        assertEquals("2", restaurant2.getId());
        assertEquals("3", restaurant3.getId());


        // Test for restaurants name
        assertEquals("restaurant 1", restaurant1.getName());
        assertEquals("restaurant 2", restaurant2.getName());
        assertEquals("restaurant 3", restaurant3.getName());
        assertNull(restaurant4.getName());


        // Test for restaurants address
        assertEquals("address restaurant 1", restaurant1.getAddress());
        assertEquals("address restaurant 2", restaurant2.getAddress());
        assertEquals("address restaurant 3", restaurant3.getAddress());


        // Test for restaurants rating
        assertEquals(3.0, restaurant1.getRating(), 0);
        assertEquals(2.5, restaurant2.getRating(), 0);
        assertEquals(2.0, restaurant3.getRating(), 0);


        // Test for restaurants opening hours
        assertEquals("Open from 11.00 to 15.00", restaurant1.getHours());
        assertEquals("Open from 10.00 to 16.00", restaurant2.getHours());
        assertEquals("Open from 10.30 to 15.30", restaurant3.getHours());


        // Test for restaurants distance
        assertEquals("360m", restaurant1.getDistance());
        assertEquals("420m", restaurant2.getDistance());
        assertEquals("1600m", restaurant3.getDistance());


        // Test for restaurants latLng
        final LatLng latLng1 = new LatLng(50.5, 30.5);
        final LatLng latLng2 = new LatLng(50.6, 30.6);
        final LatLng latLng3 = new LatLng(50.7, 30.7);

        assertEquals(latLng1, restaurant1.getLatLng());
        assertEquals(latLng2, restaurant2.getLatLng());
        assertEquals(latLng3, restaurant3.getLatLng());


        // Test for restaurants bookings
        assertEquals(bookingsList1, restaurant1.getBookings());
        assertNull(restaurant2.getBookings());
        assertNull(restaurant3.getBookings());


    }



}



package com.benlinux.go4lunch;

import static org.junit.Assert.assertEquals;

import com.benlinux.go4lunch.ui.models.Booking;

import org.junit.Test;

/**
 * Tests for Booking Model & getters
 */
public class BookingModelTest {

    final Booking booking1 = new Booking("1", "restaurantId1", "restaurant 1",
            "address 1", "picture 1", "4", "29/10/2022");

    final Booking booking2 = new Booking("2", "restaurantId2", "restaurant 2",
            "address 2", "picture 2", "5", "30/10/2022");

    final Booking booking3 = new Booking("3", "restaurantId3", "restaurant 3",
            "address 3", "picture 3", "6", "31/10/2022");

    @Test
    public void bookingGetId() {
        // Test for booking id
        assertEquals("1", booking1.getBookingId());
        assertEquals("2", booking2.getBookingId());
        assertEquals("3", booking3.getBookingId());
    }

    @Test
    public void bookingGetRestaurantId() {
        // Test for booking's restaurant id
        assertEquals("restaurantId1", booking1.getRestaurantId());
        assertEquals("restaurantId2", booking2.getRestaurantId());
        assertEquals("restaurantId3", booking3.getRestaurantId());
    }


    @Test
    public void bookingGetRestaurantName() {
        // Test for booking's restaurant id
        assertEquals("restaurant 1", booking1.getRestaurantName());
        assertEquals("restaurant 2", booking2.getRestaurantName());
        assertEquals("restaurant 3", booking3.getRestaurantName());
    }

    @Test
    public void bookingGetRestaurantFullAddress() {
        // Test for booking's restaurant address
        assertEquals("address 1", booking1.getFullAddress());
        assertEquals("address 2", booking2.getFullAddress());
        assertEquals("address 3", booking3.getFullAddress());
    }

    @Test
    public void bookingGetRestaurantPicture() {
        // Test for booking's restaurant picture
        assertEquals("picture 1", booking1.getRestaurantPicture());
        assertEquals("picture 2", booking2.getRestaurantPicture());
        assertEquals("picture 3", booking3.getRestaurantPicture());
    }

    @Test
    public void bookingGetUserId() {
        // Test for booking's user id
        assertEquals("4", booking1.getUserId());
        assertEquals("5", booking2.getUserId());
        assertEquals("6", booking3.getUserId());
    }

    @Test
    public void bookingGetBookingDate() {
        // Test for booking date
        assertEquals("29/10/2022", booking1.getBookingDate());
        assertEquals("30/10/2022", booking2.getBookingDate());
        assertEquals("31/10/2022", booking3.getBookingDate());
    }
}

package com.benlinux.go4lunch.adapters;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import com.benlinux.go4lunch.activities.UserLunchActivity;
import com.benlinux.go4lunch.ui.adapters.BookingAdapter;
import com.benlinux.go4lunch.ui.models.Booking;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class BookingAdapterTest {

    @Test
    public void bookingAdapterGetBookingData() {
        // Create empty bookings list
        List<Booking> mBookings = new ArrayList<>();
        // Mock activity to get context
        UserLunchActivity bookingsActivity = mock(UserLunchActivity.class);
        // Define adapter
        BookingAdapter bookingAdapter;

        // Set 3 bookings
        final Booking booking1 = new Booking("1", "restaurantId1", "restaurant 1",
                "address 1", "picture 1", "4", "29/10/2022");

        final Booking booking2 = new Booking("2", "restaurantId2", "restaurant 2",
                "address 2", "picture 2", "5", "30/10/2022");

        final Booking booking3 = new Booking("3", "restaurantId3", "restaurant 3",
                "address 3", "picture 3", "6", "31/10/2022");

        // Add this 3 bookings to bookings list
        mBookings.add(booking1);
        mBookings.add(booking2);
        mBookings.add(booking3);

        // Check bookings list size
        assertEquals(mBookings.size(), 3);

        // Define booking adapter
        bookingAdapter = new BookingAdapter(mBookings, bookingsActivity.getApplicationContext());

        // Check booking adapter get 3 items
        assertEquals(bookingAdapter.getItemCount(), 3);

        // Check items in adapter
        assertEquals(bookingAdapter.getItem(0), booking1);
        assertEquals(bookingAdapter.getItem(1), booking2);
        assertEquals(bookingAdapter.getItem(2), booking3);

        // Check data for each item
        assertEquals(bookingAdapter.getItem(0).getRestaurantName(), booking1.getRestaurantName());
        assertEquals(bookingAdapter.getItem(0).getRestaurantId(), booking1.getRestaurantId());
        assertEquals(bookingAdapter.getItem(1).getBookingId(), booking2.getBookingId());
        assertEquals(bookingAdapter.getItem(1).getBookingDate(), booking2.getBookingDate());
        assertEquals(bookingAdapter.getItem(2).getFullAddress(), booking3.getFullAddress());
        assertEquals(bookingAdapter.getItem(2).getUserId(), booking3.getUserId());

    }

}

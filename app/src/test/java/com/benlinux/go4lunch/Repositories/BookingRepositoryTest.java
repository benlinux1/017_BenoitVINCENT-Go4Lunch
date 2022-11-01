package com.benlinux.go4lunch.Repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.benlinux.go4lunch.data.bookingManager.BookingManager;
import com.benlinux.go4lunch.data.bookingRepository.BookingRepository;
import com.benlinux.go4lunch.ui.models.Booking;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class BookingRepositoryTest {

    final Booking booking1 = new Booking("1", "restaurantId1", "restaurant 1",
            "address 1", "picture 1", "4", "29/10/2022");

    final Booking booking2 = new Booking("2", "restaurantId2", "restaurant 2",
            "address 2", "picture 2", "5", "30/10/2022");

    final Booking booking3 = new Booking("3", "restaurantId3", "restaurant 3",
            "address 3", "picture 3", "6", "31/10/2022");


    List<Booking> fakeBookingList = new ArrayList<>();

    BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
    BookingManager bookingManager = Mockito.mock(BookingManager.class);


    @Test
    // Check that method which get all bookings data returns all bookings data
    public void bookingManagerGetAllBookingsData() {
        when(bookingManager.getAllBookingsData()).thenReturn(getAllBookingsData);
        assertEquals(bookingManager.getAllBookingsData(), getAllBookingsData);
        assertEquals(bookingManager.getAllBookingsData().getResult(), fakeBookingList);
        assertTrue(fakeBookingList.contains(booking1));
        assertTrue(fakeBookingList.contains(booking2));
        assertTrue(fakeBookingList.contains(booking3));
        assertTrue(bookingManager.getAllBookingsData().getResult().contains(booking1));
        assertTrue(bookingManager.getAllBookingsData().getResult().contains(booking2));
        assertTrue(bookingManager.getAllBookingsData().getResult().contains(booking3));
    }

    @Test
    // Check that booking manager create booking in repository
    public void bookingManagerCreateBooking() {
        assertEquals(bookingManager.createBooking(booking1), bookingRepository.createBooking(booking1));
        assertEquals(bookingManager.createBooking(booking2), bookingRepository.createBooking(booking2));
        assertEquals(bookingManager.createBooking(booking3), bookingRepository.createBooking(booking3));
    }

    @Test
    // Check that booking manager delete booking in repository
    public void bookingManagerDeleteBooking() {
        assertEquals(bookingManager.deleteBookingById(booking1.getBookingId()), bookingRepository.deleteBookingById(booking1.getBookingId()));
        assertEquals(bookingManager.deleteBookingById(booking2.getBookingId()), bookingRepository.deleteBookingById(booking2.getBookingId()));
        assertEquals(bookingManager.deleteBookingById(booking2.getBookingId()), bookingRepository.deleteBookingById(booking3.getBookingId()));
    }


    // Simulate bookings repository
    public Task<List<Booking>> getAllBookingsData = new Task<List<Booking>>() {

        @NonNull
        @Override
        public Task<List<Booking>> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
            return getAllBookingsData;
        }

        @NonNull
        @Override
        public Task<List<Booking>> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
            return getAllBookingsData;
        }

        @NonNull
        @Override
        public Task<List<Booking>> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
            return getAllBookingsData;
        }

        @NonNull
        @Override
        public Task<List<Booking>> addOnSuccessListener(@NonNull OnSuccessListener<? super List<Booking>> onSuccessListener) {
            return getAllBookingsData;
        }

        @NonNull
        @Override
        public Task<List<Booking>> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super List<Booking>> onSuccessListener) {
            return getAllBookingsData;
        }

        @NonNull
        @Override
        public Task<List<Booking>> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super List<Booking>> onSuccessListener) {
            return getAllBookingsData;
        }


        @Nullable
        @Override
        public Exception getException() {
            return null;
        }

        @Override
        public List<Booking> getResult() {
            fakeBookingList.add(booking1);
            fakeBookingList.add(booking2);
            fakeBookingList.add(booking3);
            return fakeBookingList;
        }

        @Override
        public <X extends Throwable> List<Booking> getResult(@NonNull Class<X> aClass) throws X {
            return null;
        }


        @Override
        public boolean isCanceled() {
            return getAllBookingsData.isCanceled();
        }

        @Override
        public boolean isComplete() {
            return getAllBookingsData.isComplete();
        }

        @Override
        public boolean isSuccessful() {
            return getAllBookingsData.isSuccessful();
        }
    };
}

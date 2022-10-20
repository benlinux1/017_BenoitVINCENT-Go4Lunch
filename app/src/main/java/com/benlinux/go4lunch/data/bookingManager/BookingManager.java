package com.benlinux.go4lunch.data.bookingManager;

import com.benlinux.go4lunch.data.bookingRepository.BookingRepository;
import com.benlinux.go4lunch.data.userRepository.UserRepository;
import com.benlinux.go4lunch.ui.models.Booking;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;

public class BookingManager {

    private static volatile com.benlinux.go4lunch.data.bookingManager.BookingManager instance;
    private final BookingRepository bookingRepository;

    private BookingManager() {
        bookingRepository = BookingRepository.getInstance();
    }

    public static com.benlinux.go4lunch.data.bookingManager.BookingManager getInstance() {
        com.benlinux.go4lunch.data.bookingManager.BookingManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized(UserRepository.class) {
            if (instance == null) {
                instance = new com.benlinux.go4lunch.data.bookingManager.BookingManager();
            }
            return instance;
        }
    }

    public Task<QuerySnapshot> createBooking(Booking booking){
        return this.bookingRepository.createBooking(booking);
    }


    public Task<List<Booking>> getAllBookingsData() {
        return Objects.requireNonNull(bookingRepository.getAllBookingsData()).continueWith(task -> task.getResult().toObjects(Booking.class)) ;
    }


    public Task<Void> deleteBookingById(String bookingId){
        return this.bookingRepository.deleteBookingById(bookingId);
    }


}
package com.benlinux.go4lunch.data.bookingRepository;

import android.util.Log;

import com.benlinux.go4lunch.ui.models.Booking;

import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


public class BookingRepository {

    // FIRESTORE DATA
    private static final String COLLECTION_NAME = "bookings";
    private static final String DATE_FIELD = "bookingDate";

    private static volatile BookingRepository instance;

    private BookingRepository() { }

    public static BookingRepository getInstance() {
        BookingRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized(BookingRepository.class) {
            if (instance == null) {
                instance = new BookingRepository();
            }
            return instance;
        }
    }


    // Get the Collection Reference in Firestore Database
    private CollectionReference getBookingsCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // Create User in Firestore
    public Task<QuerySnapshot> createBooking(Booking booking) {

        // Define booking data
        String bookingId = booking.getBookingId();
        String restaurantId = booking.getRestaurantId();
        String restaurantPicture = booking.getRestaurantPicture();
        String fullAddress = booking.getFullAddress();
        String restaurantName = booking.getRestaurantName();
        String userId = booking.getUserId();
        String date = booking.getBookingDate();

        // Define booking object
        Booking bookingToCreate = new Booking(bookingId, restaurantId, restaurantName, fullAddress, restaurantPicture, userId, date);

        Task<QuerySnapshot> bookingsData = getAllBookingsData();
        // Check if booking exists in database
        return bookingsData.addOnSuccessListener(queryDocumentSnapshots -> {
            // if booking exists in database, continue
            if (queryDocumentSnapshots.getDocuments().contains(date) && queryDocumentSnapshots.getDocuments().contains(restaurantId) && queryDocumentSnapshots.getDocuments().contains(userId)) {
                Log.d("BOOKING CREATION INFO", "booking already exists");
                // if booking doesn't exists in database, create it
            } else {
                // Create booking in database with custom id (user to delete it later)
                this.getBookingsCollection().document(bookingId).set(bookingToCreate);
            }
        });
    }


    // Get all bookings from Firestore
    public Task<QuerySnapshot> getAllBookingsData() {
        return this.getBookingsCollection().orderBy(DATE_FIELD, Query.Direction.ASCENDING).get();
    }


    // Delete the Booking from Firestore, according to its id
    public Task<Void> deleteBookingById(String bookingId) {
        return this.getBookingsCollection().document(bookingId).delete();
    }

}

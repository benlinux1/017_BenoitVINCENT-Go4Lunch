package com.benlinux.go4lunch.data.bookingRepository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import com.benlinux.go4lunch.data.userRepository.UserRepository;
import com.benlinux.go4lunch.ui.models.Booking;
import com.benlinux.go4lunch.ui.models.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class BookingRepository {

    // FIRESTORE DATA
    private static final String COLLECTION_NAME = "bookings";
    private static final String RESTAURANT_ID_FIELD = "restaurantId";
    private static final String RESTAURANT_NAME_FIELD = "restaurantName";
    private static final String USER_ID_FIELD = "userId";
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
    public void createBooking(Booking booking) {

        String restaurantId = booking.getRestaurantId();
        String restaurantName = booking.getRestaurantName();
        String userId = booking.getUserId();
        String date = booking.getBookingDate();

        Booking bookingToCreate = new Booking(restaurantId, restaurantName, userId, date);

        Task<QuerySnapshot> bookingsData = getAllBookingsData();
        // TODO : Check if booking exists in database
        bookingsData.addOnSuccessListener(queryDocumentSnapshots -> {
            // if booking exists in database, continue
            if (queryDocumentSnapshots.getDocuments().contains(date) && queryDocumentSnapshots.getDocuments().contains(restaurantId) && queryDocumentSnapshots.getDocuments().contains(userId)) {
                Log.d("BOOKING CREATION INFO", "booking already exists");
                // if user doesn't exists in database, create it
            } else {
                this.getBookingsCollection().document().set(bookingToCreate);
            }
        });

    }


    // Get all users from Firestore
    public Task<QuerySnapshot> getAllBookingsData() {
        return this.getBookingsCollection().get();
    }

}

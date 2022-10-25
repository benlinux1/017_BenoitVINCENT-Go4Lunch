package com.benlinux.go4lunch.ui.adapters;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.benlinux.go4lunch.BuildConfig;
import com.benlinux.go4lunch.R;
import com.benlinux.go4lunch.activities.RestaurantDetailsActivity;
import com.benlinux.go4lunch.activities.UserLunchActivity;
import com.benlinux.go4lunch.data.bookingManager.BookingManager;
import com.benlinux.go4lunch.ui.models.Booking;

import com.bumptech.glide.Glide;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;


public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    private final List<Booking> mBookings;
    private final Context localContext;

    private final BookingManager bookingManager = BookingManager.getInstance();


    /**
     * Instantiates a new ListAdapter.
     * @param bookings the list of restaurants the adapter deals with to set
     */
    public BookingAdapter(List<Booking> bookings, Context context) {
        mBookings = bookings;
        localContext = context;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_list, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final BookingAdapter.ViewHolder holder, int position) {
        // bind restaurant according to position in the list
        holder.bind(mBookings.get(position));

        Booking booking = mBookings.get(position);
        String bookingId = holder.bookingId.getText().toString();
        String restaurantId = holder.restaurantId.getText().toString();

        // Launch Restaurant Details according to the Restaurant Id
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View restaurantItem) {
                // On workmate item click, display restaurant details
                Intent restaurantDetailsActivityIntent = new Intent(restaurantItem.getContext(), RestaurantDetailsActivity.class);
                restaurantDetailsActivityIntent.putExtra("PLACE_ID", restaurantId);
                restaurantItem.getContext().startActivity(restaurantDetailsActivityIntent);
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCustomDialogBox(booking, bookingId);
            }
        });
    }


    public Booking getItem(int i) {
        return mBookings.get(i);
    }


    @Override
    public int getItemCount() {
        return mBookings.size();
    }

    /**
     * <p>ViewHolder for restaurants items in the restaurants list</p>
     * @author BenLinux1
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * The square picture of the workmate
         */
        private final ImageView restaurantPicture;

        /**
         * The TextView displaying the name of the restaurant
         */
        private final TextView restaurantName;

        /**
         * The TextView displaying the address of the restaurant
         */
        private final TextView restaurantAddress;

        /**
         * The TextView displaying the date of the booking
         */
        private final TextView bookingDate;

        /**
         * The TextView displaying the id of the restaurant
         */
        private final TextView restaurantId;

        /**
         * The TextView displaying the id of the booking
         */
        private final TextView bookingId;

        /**
         * The TextView displaying the id of the restaurant
         */
        private final ImageButton deleteButton;



        /**
         * Instantiates a new Restaurant ViewHolder.
         * @param itemView the view of the restaurant item
         */
        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            restaurantPicture = itemView.findViewById(R.id.item_booking_avatar);
            restaurantName = itemView.findViewById(R.id.item_booking_restaurant_name);
            restaurantAddress = itemView.findViewById(R.id.item_booking_address);
            bookingDate = itemView.findViewById(R.id.item_booking_date);
            restaurantId = itemView.findViewById(R.id.item_booking_restaurant_id);
            bookingId = itemView.findViewById(R.id.item_booking_id);
            deleteButton = itemView.findViewById(R.id.item_booking_delete_button);
        }


        /**
         * Binds data to the item view.
         * @param booking the restaurant to bind in the item view
         */
        void bind(Booking booking) {

            // Set name & booking of the day
            restaurantName.setText(booking.getRestaurantName());
            // Set restaurant address
            restaurantAddress.setText(booking.getFullAddress());
            // Set date
            bookingDate.setText(booking.getBookingDate());
            // Set restaurant Id
            restaurantId.setText(booking.getRestaurantId());
            // Set booking id
            bookingId.setText(booking.getBookingId());
            // Set avatar
            getRestaurantPicture(restaurantId.getText().toString() ,restaurantPicture);

        }

    }

    // Retrieve place details
    private void getRestaurantPicture(String restaurantId, ImageView imageView) {

        // Specify the fields to return from request
        final List<Place.Field> placeFields = Arrays.asList(
            Place.Field.ID,
            Place.Field.PHOTO_METADATAS
        );

        // Construct a request object, passing the place ID and fields array.
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(restaurantId, placeFields);

        // Initialize Places API
        Places.initialize(localContext, BuildConfig.PLACE_API_KEY);

        // Initialize Places Client
        PlacesClient placesClient = Places.createClient(localContext);

        // Fetch place to get restaurants details
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            final List<PhotoMetadata> metadata = place.getPhotoMetadatas();

            // If no photo, set No photo picture
            if (metadata == null || metadata.isEmpty()) {
                Log.w(TAG, "No photo metadata.");
                Glide.with(imageView.getContext())
                        .load(R.mipmap.no_photo)
                        .centerCrop()
                        .into(imageView);
            } else {
            // Set restaurant's main picture
                final PhotoMetadata photoMetadata = metadata.get(0);
                // Create a FetchPhotoRequest.
                final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                        .build();
                placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                    // On success, set photo in ImageView
                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    Glide.with(imageView.getContext())
                            .load(bitmap)
                            .circleCrop()
                            .into(imageView);
                }).addOnFailureListener((exception) -> {
                    // On fail, log error
                    if (exception instanceof ApiException) {
                        final ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + exception.getMessage());
                        final int statusCode = apiException.getStatusCode();
                        Log.e(TAG, "Place not found: " + statusCode);
                    }
                });
            }
        });
    }

    public void createCustomDialogBox(Booking booking, String bookingId) {
        // Build an alert dialogBox
        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(localContext);
        builder.setCancelable(true);
        builder.setMessage(R.string.dialog_title);

        builder.setPositiveButton(R.string.dialog_yes, (dialog, which) -> {
            deleteBooking(booking, bookingId);
        });

        builder.setNegativeButton(R.string.dialog_no, (dialog, which) -> dialog.cancel());
        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();

        // Show the Alert Dialog box
        alertDialog.show();
    }

    private void deleteBooking(Booking booking, String bookingId) {
        bookingManager.deleteBookingById(bookingId).addOnSuccessListener(new OnSuccessListener<Void>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(Void unused) {
                mBookings.remove(booking);
                Toast.makeText(localContext, R.string.booking_delete_success, Toast.LENGTH_LONG).show();
                notifyDataSetChanged();
                UserLunchActivity.showTextIfNoBooking();
            }
        });
    }
}
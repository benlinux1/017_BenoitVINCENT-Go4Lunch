package com.benlinux.go4lunch.ui.adapters;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.benlinux.go4lunch.BuildConfig;
import com.benlinux.go4lunch.R;
import com.benlinux.go4lunch.activities.RestaurantDetailsActivity;
import com.benlinux.go4lunch.modules.FormatRatingModule;
import com.benlinux.go4lunch.ui.models.Booking;
import com.benlinux.go4lunch.ui.models.Restaurant;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private List<Restaurant> mRestaurants;
    public LatLng actualLocation;
    private final Context localContext;

    /**
     * Instantiates a new ListAdapter.
     * @param restaurants the list of restaurants the adapter deals with to set
     */
    public ListAdapter(List<Restaurant> restaurants, Context context) {
        mRestaurants = restaurants;
        localContext = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_restaurant_list, parent, false);
        return new ListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListAdapter.ViewHolder holder, int position) {
        // bind restaurant according to position in the list
        holder.bind(mRestaurants.get(position));

        // Launch Restaurant Details according to the Restaurant Id
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View restaurantItem) {
               Intent restaurantDetailsActivityIntent = new Intent(restaurantItem.getContext(), RestaurantDetailsActivity.class);
               restaurantDetailsActivityIntent.putExtra("PLACE_ID", holder.id.getText());
               restaurantDetailsActivityIntent.putExtra("USER_LOCATION", getUserLocation());
               restaurantItem.getContext().startActivity(restaurantDetailsActivityIntent);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void initList(List<Restaurant> mRestaurants) {
        this.mRestaurants = mRestaurants;
        notifyItemRangeChanged(- 1, mRestaurants.size());
    }


    public LatLng getUserLocation() {
        return this.actualLocation;
    }

    public Restaurant getItem(int i) {
        return mRestaurants.get(i);
    }

    @Override
    public int getItemCount() {
        return mRestaurants.size();
    }

    /**
     * <p>ViewHolder for restaurants items in the restaurants list</p>
     * @author BenLinux1
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * The square picture of the restaurant
         */
        private final ImageView picture;

        /**
         * The TextView displaying the id of the restaurant
         */
        private final TextView id;

        /**
         * The TextView displaying the name of the restaurant
         */
        private final TextView name;

        /**
         * The TextView displaying the style and the address of the restaurant
         */
        private final TextView styleAndAddress;

        /**
         * The TextView displaying the opening hours of the restaurant
         */
        private final TextView hours;

        /**
         * The TextView displaying the distance between the user and the restaurant
         */
        private final TextView distance;

        /**
         * The TextView displaying the number of workmates who booked a lunch in the restaurant
         */
        private final TextView numberOfBookings;

        /**
         * The Rating bar displaying the rating average of the restaurant
         */
        private final RatingBar ratingBar;


        /**
         * Instantiates a new Restaurant ViewHolder.
         * @param itemView the view of the restaurant item
         */
        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            picture = itemView.findViewById(R.id.item_restaurant_picture);
            id = itemView.findViewById(R.id.item_restaurant_id);
            name = itemView.findViewById(R.id.item_restaurant_name);
            styleAndAddress = itemView.findViewById(R.id.item_restaurant_style_and_address);
            hours = itemView.findViewById(R.id.item_restaurant_hours);
            distance = itemView.findViewById(R.id.item_restaurant_distance);
            numberOfBookings = itemView.findViewById(R.id.item_restaurant_guests);
            ratingBar = itemView.findViewById(R.id.item_restaurant_rating);
        }


        /**
         * Binds data to the item view.
         * @param restaurant the restaurant to bind in the item view
         */
        void bind(Restaurant restaurant) {

            // Set name
            name.setText(restaurant.getName());
            // Set place_id (from Places API)
            id.setText(restaurant.getId());
            // Set formatted rating
            Double rating = restaurant.getRating();
            if (rating != null) {
                ratingBar.setRating(FormatRatingModule.formatRating(rating).floatValue());
            } else {
                ratingBar.setVisibility(View.GONE);
            }

            // Set address to text view
            styleAndAddress.setText(restaurant.getAddress());
            // Set distance into text view
            distance.setText(restaurant.getDistance());
            // Get and set formatted opening hours and picture into respective views
            getAndSetOpeningHoursAndPicture(restaurant.getId(), hours, picture);

            // Get and set number of users who booked in the restaurant
            if (restaurant.getBookings().size() == 0 || restaurant.getBookings() == null) {
                numberOfBookings.setVisibility(View.GONE);
            } else {
                String bookingsNumber = String.valueOf(restaurant.getBookings().size());
                StringBuilder sb = new StringBuilder();
                sb.append("(").append(bookingsNumber).append(")");
                numberOfBookings.setText(sb.toString());
                numberOfBookings.setVisibility(View.VISIBLE);
            }
        }
    }


    // Retrieve place details to set opening hours and picture
    private void getAndSetOpeningHoursAndPicture(String placeId, TextView hoursDestination, ImageView pictureDestination) {

        // Specify the fields to return from request
        final List<Place.Field> placeFields = Arrays.asList(
                Place.Field.ID,
                Place.Field.OPENING_HOURS,
                Place.Field.PHOTO_METADATAS
        );

        // Construct a request object, passing the place ID and fields array.
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        // Initialize Places API
        Places.initialize(localContext, BuildConfig.PLACE_API_KEY);

        // Initialize Places Client
        PlacesClient placesClient = Places.createClient(localContext);

        // Fetch place to get restaurants details
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();

            // Set restaurant's opening hours into textview
            setOpeningHours(place, hoursDestination);

            // Set restaurant's picture into imageView
            setPicture(place, placesClient, pictureDestination);

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                Log.e(TAG, "Place not found: " + exception.getMessage());
            }
        });
    }

    // Format opening hours for best user experience
    private void setOpeningHours(Place place, TextView hoursDestination) {

        StringBuilder openingHours = new StringBuilder();
        final Calendar currentDate = Calendar.getInstance(Locale.getDefault());
        int today = currentDate.get(Calendar.DAY_OF_WEEK)-1;

        // Get Opening hours
        if (place.getOpeningHours() != null) {
            try{
                List<Period> periodList = place.getOpeningHours().getPeriods();
                int openHour = periodList.get(today).getOpen().getTime().getHours();
                int closeHour = Objects.requireNonNull(periodList.get(today).getClose()).getTime().getHours();
                String openMinutes = String.valueOf(Objects.requireNonNull(periodList.get(today).getOpen()).getTime().getMinutes());
                String closeMinutes = String.valueOf(Objects.requireNonNull(periodList.get(today).getClose()).getTime().getMinutes());

                // Format opening hours of the day according to user language
                openingHours.append(localContext.getString(R.string.open_from)).append(openHour)
                    .append(localContext.getString(R.string.us_hours_separator)).append(formatMinutes(openMinutes)).append(localContext.getString(R.string.am))
                    .append(localContext.getString(R.string.open_to)).append(closeHour).append(localContext.getString(R.string.us_hours_separator))
                    .append(formatMinutes(closeMinutes)).append(localContext.getString(R.string.pm)
                );

            }
            catch(Exception e){
                Log.d("exception:", e.getMessage());
                openingHours.append(localContext.getString(R.string.closed_today));
            }

        } else {
            openingHours.append(localContext.getString(R.string.no_opening_hours));
        }
        // Set formatted opening hours into destination TextView
        hoursDestination.setText(openingHours.toString());
    }

    // Set place picture with placeClient to imageView
    public void setPicture(Place place, PlacesClient placesClient, ImageView imageView) {
        final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
        // Set No Picture if result is null
        if (metadata == null || metadata.isEmpty()) {
            Log.w(TAG, "No photo metadata.");
            Glide.with(imageView.getContext())
                    .load(R.mipmap.no_photo)
                    .centerCrop()
                    .into(imageView);
        } else {
            final PhotoMetadata photoMetadata = metadata.get(0);

            // Create a FetchPhotoRequest.
            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .build();
            // On success, set picture into imageView
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                imageView.setImageBitmap(bitmap);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    final ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + exception.getMessage());
                    final int statusCode = apiException.getStatusCode();
                    Log.e(TAG, "Status code : " + statusCode);
                }
            });
        }
    }

    // Format 0 minutes to 00 for best user XP
    private String formatMinutes(String string) {
        StringBuilder formattedString = new StringBuilder();
        if (string.equals("0") || string == null) {
            formattedString.append("00");
            return formattedString.toString();
        }
        return string;
    }
}
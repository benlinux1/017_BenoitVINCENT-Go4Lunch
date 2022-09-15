package com.benlinux.go4lunch.ui.list;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
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
import com.benlinux.go4lunch.modules.FormatAddressModule;
import com.benlinux.go4lunch.ui.map.MapFragment;
import com.benlinux.go4lunch.ui.models.Restaurant;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private JSONArray mRestaurants;
    public LatLng actualLocation;
    private Application app;
    private Context localContext;


    /**
     * Instantiates a new ListAdapter.
     *
     * @param restaurants the list of restaurants the adapter deals with to set
     */
    public ListAdapter(JSONArray restaurants, Context context) {
        mRestaurants = restaurants;
        localContext = context;

    }


    /**
     * Updates the list of restaurants the adapter deals with.
     *
     * @param restaurants the list of tasks the adapter deals with to set
     */
    void updateRestaurants(@NonNull final JSONArray restaurants) {
        this.mRestaurants = restaurants;
        notifyDataSetChanged();
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
        try {
            holder.bind(mRestaurants.getJSONObject(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    public void initList(JSONArray mRestaurants) {
        this.mRestaurants = mRestaurants;
        notifyDataSetChanged();
    }

    public void setUserLocation(LatLng location) {
        this.actualLocation = location;
    }

    public LatLng getUserLocation() {
        return this.actualLocation;
    }


    @Override
    public int getItemCount() {
        return mRestaurants.length();
    }

    /**
     * <p>ViewHolder for restaurants items in the restaurants list</p>
     *
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
         * The drawable that represents users who booked
         */
        private final ImageView userPicture;

        /**
         * The TextView displaying the number of workmates who booked a lunch in the restaurant
         */
        private final TextView numberOfBookings;

        /**
         * The TextView displaying the number of workmates who booked a lunch in the restaurant
         */
        private final RatingBar ratingBar;


        /**
         * Instantiates a new Restaurant ViewHolder.
         *
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
            userPicture = itemView.findViewById(R.id.item_restaurant_user);
            numberOfBookings = itemView.findViewById(R.id.item_restaurant_guests);
            ratingBar = itemView.findViewById(R.id.item_restaurant_rating);
        }


        /**
         * Binds data to the item view.
         *
         * @param restaurant the restaurant to bind in the item view
         */
        void bind(JSONObject restaurant) {
            // String styleAndAddressString = restaurant.getStyle() + R.string.restaurant_style_and_address_separator + restaurant.getAddress();
            //name.setText(name.getText());
            try {
                name.setText(restaurant.getString("name"));
                id.setText(restaurant.getString("place_id"));
                id.setVisibility(View.GONE);
                Double rating = restaurant.getDouble("rating");
                ratingBar.setRating(formatRating(rating).floatValue());

                JSONObject getLocation = restaurant.getJSONObject("geometry")
                        .getJSONObject("location");

                // Get restaurant's latitude & longitude
                String lat = getLocation.getString("lat");
                String lng = getLocation.getString("lng");

                // Define restaurant LatLng for marker
                LatLng restaurantLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

                // Format and set address to text view
                String formattedAddress = getFormattedAddressFromLatLng(restaurantLocation);
                styleAndAddress.setText(formattedAddress);

                // Calculate and set distance into text view
                distance.setText(calculateAndFormatDistance(actualLocation, restaurantLocation));

                getRestaurantInfo(restaurant.getString("place_id"), hours);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            // styleAndAddress.getEditableText();
            // distance.setText(distance.getText());
            /**
            Glide.with(itemView.getContext())
                    .load(restaurant.getPictureUrl())
                    .into(picture);
             */
            // hours.getText();
            // numberOfBookings.getText();
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

    // Calculate distance between two points, and format to string in meters
    @SuppressLint("DefaultLocale")
    private String calculateAndFormatDistance(LatLng startPoint, LatLng endPoint) {
        double distance = SphericalUtil.computeDistanceBetween(startPoint, endPoint);
        return String.format("%d m", Math.round(distance));
    }

    private Double formatRating(Double rating) {
        Double formattedRating = null;
        if (rating > 0 && rating <= 0.8) {
            formattedRating = 0.5;
        } else if (rating > 0.8 && rating <= 1.4) {
            formattedRating = 1.0;
        } else if (rating > 1.4 && rating <= 2.3) {
            formattedRating = 1.5;
        } else if (rating > 2.3 && rating <= 3.2) {
            formattedRating = 2.0;
        } else if (rating > 3.2 && rating <= 4.0) {
            formattedRating = 2.5;
        } else if (rating > 4.0 && rating <= 5.0) {
            formattedRating = 3.0;
        }
        return formattedRating;
    }

    public String getFormattedAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(localContext, Locale.getDefault());
        String formattedAddress = "";
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            Address returnedAddress = addresses.get(0);
            StringBuilder strReturnedAddress = new StringBuilder("");

            for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
            }
            String mStreetNumber = returnedAddress.getSubThoroughfare();
            String mStreet = returnedAddress.getThoroughfare();
            strReturnedAddress.append(mStreetNumber).append(" ").append(mStreet);
            formattedAddress = strReturnedAddress.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return formattedAddress;
    }

    private void setOpeningHours(Place place, TextView hours) {

        StringBuilder openingHours = new StringBuilder();
        // Set Opening hours
        if (place.getOpeningHours() != null) {
            final Calendar currentDate = Calendar.getInstance(Locale.getDefault());
            int today = currentDate.get(Calendar.DAY_OF_WEEK);
            List<Period> periodList = place.getOpeningHours().getPeriods();
            int openHour = Objects.requireNonNull(periodList.get(today - 1).getOpen()).getTime().getHours();
            int closeHour = Objects.requireNonNull(periodList.get(today - 1).getClose()).getTime().getHours();
            String openMinutes = String.valueOf(Objects.requireNonNull(periodList.get(today - 1).getOpen()).getTime().getMinutes());
            String closeMinutes = String.valueOf(Objects.requireNonNull(periodList.get(today - 1).getClose()).getTime().getMinutes());

            // Format opening hours of the day according to user language
            if (Locale.getDefault().getLanguage().equals("fr")) {
                openingHours.append("Ouvert aujourd'hui de ").append(openHour).append("h").append(formatMinutes(openMinutes))
                        .append(" jusqu'à ").append(closeHour).append("h").append(formatMinutes(closeMinutes));
            } else {
                openingHours.append("Open today from ").append(openHour).append(":").append(formatMinutes(openMinutes)).append(" am")
                        .append(" to ").append(closeHour).append(":").append(formatMinutes(closeMinutes)).append(" pm");
            }
        } else {
            if (Locale.getDefault().getLanguage().equals("fr")) {
                openingHours.append("Horaires non communiqués");
            } else {
                openingHours.append("Opening hours not registered yet");
            }
        }
        hours.setText(openingHours.toString());
    }

    // Retrieve place details
    private void getRestaurantInfo(String placeId, TextView textView) {

        TextView destination = textView;

        // Specify the fields to return from request
        final List<Place.Field> placeFields = Arrays.asList(
                Place.Field.ID,
                Place.Field.OPENING_HOURS
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

            // if result is successful, set restaurants details in textview
            setOpeningHours(place, destination);

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + exception.getMessage());
            }
        });
    }

}
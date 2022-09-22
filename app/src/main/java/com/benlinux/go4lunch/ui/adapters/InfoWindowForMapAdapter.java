package com.benlinux.go4lunch.ui.adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.benlinux.go4lunch.BuildConfig;
import com.benlinux.go4lunch.R;
import com.benlinux.go4lunch.activities.RestaurantDetailsActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class InfoWindowForMapAdapter implements GoogleMap.InfoWindowAdapter {
    Context context;
    LayoutInflater inflater;
    Float ratingFloat;

    public InfoWindowForMapAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoContents(Marker marker) {
    return null;

    }

    @Override
    public View getInfoWindow(Marker marker) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.info_window_map, null);

        // Get reference to the TextView to set place-id
        TextView restaurantId = (TextView) view.findViewById(R.id.place_id);
        // Getting the restaurant's position from marker
        LatLng latLng = marker.getPosition();
        // Get reference to the TextView to set restaurant's name
        TextView restaurantName = (TextView) view.findViewById(R.id.title);
        // Get reference to the TextView to set street & street number
        TextView restaurantStreet = (TextView) view.findViewById(R.id.street);
        // Get reference to the TextView to set postal code & city
        TextView restaurantCity = (TextView) view.findViewById(R.id.postalCodeAndCity);
        // Get reference to the Rating bar to set rating value
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating);
        // Get reference to the info window button
        Button seeDetailsButton = (Button) view.findViewById(R.id.seeDetailsButton);

        // Getting restaurant's data to set rating & place_id in invisible textView
        if (marker.getTag() != null) {
            getRestaurantInfo(marker.getTag().toString());
            restaurantId.setText(marker.getTag().toString());
        } else {
            seeDetailsButton.setVisibility(View.GONE);
            ratingBar.setVisibility(View.GONE);
        }

        // Set restaurant's name
        restaurantName.setText(Objects.requireNonNull(marker.getTitle()).toUpperCase(Locale.ROOT));
        // Set restaurant's formatted address
        setAddressFromLatLng(latLng, restaurantStreet, restaurantCity);
        // Set restaurant's rating
        setRestaurantRating(ratingBar);

        // Returning the view containing InfoWindow contents
        return view;

    }

    // Retrieve place details
    private void getRestaurantInfo(String placeId) {

        // Specify the fields to return from request
        final List<Place.Field> placeFields = Arrays.asList(
                Place.Field.ID,
                Place.Field.RATING
        );

        // Construct a request object, passing the place ID and fields array.
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        // Initialize Places API
        Places.initialize(context, BuildConfig.PLACE_API_KEY);

        // Initialize Places Client
        PlacesClient placesClient = Places.createClient(context);

        // Fetch place to get restaurants details
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();

            // if result is successful, set restaurants details in textview
            if (place.getRating() != null) {
                this.ratingFloat = place.getRating().floatValue();
            }

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + exception.getMessage());
            }
        });
    }

    // Set restaurant's rating in text view, according to rating result
    private void setRestaurantRating(RatingBar ratingBar) {
        if (this.ratingFloat != null) {
            ratingBar.setRating(ratingFloat);
        } else {
            ratingBar.setVisibility(View.GONE);
        }
    }

    // Return address according to Latitude & longitude params
    public void setAddressFromLatLng(LatLng latLng, TextView restaurantStreet, TextView restaurantCity) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this.context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            Address returnedAddress = addresses.get(0);
            StringBuilder strReturnedAddress = new StringBuilder("");

            for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
            }
            // Get Street info from Geocoder
            String mStreetNumber = returnedAddress.getSubThoroughfare();
            String mStreet = returnedAddress.getThoroughfare();
            // Set street number & street info into dedicated textView
            if (mStreetNumber != null) {
                restaurantStreet.setText(mStreetNumber + " " + mStreet);
            } else {
                restaurantStreet.setText(mStreet);
            }

            // Get postal code & city from Geocoder
            String mPostalCode = returnedAddress.getPostalCode();
            String mCity = returnedAddress.getLocality();
            // Set postal code & city info into dedicated textView
            restaurantCity.setText(mPostalCode + " " + mCity);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

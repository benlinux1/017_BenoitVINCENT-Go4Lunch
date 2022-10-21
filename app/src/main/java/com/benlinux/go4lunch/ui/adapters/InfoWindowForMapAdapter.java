package com.benlinux.go4lunch.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.benlinux.go4lunch.R;

import com.benlinux.go4lunch.modules.FormatRatingModule;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class InfoWindowForMapAdapter implements GoogleMap.InfoWindowAdapter {
    Context context;

    public InfoWindowForMapAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoContents(@NonNull Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.info_window_map, null);

        // Get reference to the TextView to set place-id
        TextView restaurantId = view.findViewById(R.id.place_id);
        // Getting the restaurant's position from marker
        LatLng latLng = marker.getPosition();
        // Get reference to the TextView to set restaurant's name
        TextView restaurantName = view.findViewById(R.id.title);
        // Get reference to the TextView to set street & street number
        TextView restaurantStreet = view.findViewById(R.id.street);
        // Get reference to the TextView to set postal code & city
        TextView restaurantCity = view.findViewById(R.id.postalCodeAndCity);
        // Get reference to the Rating bar to set rating value
        RatingBar ratingBar = view.findViewById(R.id.rating);
        // Get reference to the info window button
        Button seeDetailsButton = view.findViewById(R.id.seeDetailsButton);

        // Getting & setting restaurant's rating
        if (marker.getSnippet() != null) {
            try {
                String ratingString = marker.getSnippet();
                Double ratingDouble = Double.parseDouble(ratingString);
                float ratingFloat = FormatRatingModule.formatRating(ratingDouble).floatValue();
                ratingBar.setRating(ratingFloat);
            } catch (Exception e) {
                Log.e("Error format rating", e.getMessage());
            }
        } else {
            ratingBar.setVisibility(View.GONE);
        }

        // Disable details button if place doesn't get id
        if (marker.getTag() == null) {
            seeDetailsButton.setVisibility(View.GONE);
        } else {
            restaurantId.setText(marker.getTag().toString());
        }

        // Set restaurant's name
        restaurantName.setText(Objects.requireNonNull(marker.getTitle()).toUpperCase(Locale.ROOT));
        // Set restaurant's formatted address
        setAddressFromLatLng(latLng, restaurantStreet, restaurantCity);

        // Returning the view containing InfoWindow contents
        return view;

    }


    // Return address according to Latitude & longitude params
    @SuppressLint("SetTextI18n")
    public void setAddressFromLatLng(LatLng latLng, TextView restaurantStreet, TextView restaurantCity) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this.context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            Address returnedAddress = addresses.get(0);
            StringBuilder strReturnedAddress = new StringBuilder();

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

package com.benlinux.go4lunch.activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.benlinux.go4lunch.BuildConfig;
import com.benlinux.go4lunch.R;
import com.benlinux.go4lunch.databinding.ActivityRestaurantDetailsBinding;
import com.benlinux.go4lunch.modules.FormatAddressModule;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.SphericalUtil;


import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RestaurantDetailsActivity extends AppCompatActivity {

    private ActivityRestaurantDetailsBinding binding;
    private TextView restaurantName;
    private TextView restaurantAddress;
    private String restaurantPhoneNumber;
    private Uri restaurantWebSite;
    private RatingBar restaurantRating;
    private TextView restaurantHours;
    private TextView restaurantDistance;
    private ImageView restaurantPicture;

    private ImageButton phoneButton;
    // TODO : private ImageButton likeButton;
    private ImageButton webSiteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);
        getUserLocationFromIntent();
        getRestaurantIdFromIntent();
        setViews();
        getRestaurantInfo();
        setListeners();
    }

    // Retrieve Place Id from previous activity
    private String getRestaurantIdFromIntent() {
        Intent intent = getIntent();
        return intent.getStringExtra("PLACE_ID");
    }

    // Retrieve User location from previous activity
    private LatLng getUserLocationFromIntent() {
        Intent intent = getIntent();
        return intent.getParcelableExtra("USER_LOCATION");
    }

    // Define views
    private void setViews() {
        restaurantName = binding.restaurantDetailsName;
        restaurantAddress = binding.restaurantDetailsAddress;
        restaurantDistance = binding.restaurantDetailsDistance;
        restaurantRating = binding.restaurantDetailsRating;
        restaurantHours = binding.restaurantDetailsOpening;
        restaurantPicture = binding.restaurantDetailsPhoto;
        phoneButton = binding.restaurantDetailsCallButton;
        // TODO : likeButton = binding.restaurantDetailsLikeButton;
        webSiteButton = binding.restaurantDetailsWebsiteButton;
    }

    // Set listeners on buttons
    private void setListeners() {
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactByPhone(restaurantPhoneNumber);
            }
        });
        webSiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (restaurantWebSite != null) {
                    openWebSite(restaurantWebSite.toString());
                } else {
                    showSnackBar(binding.getRoot().getRootView(), getString(R.string.no_website_notification));
                }
            }
        });
    }


    // Retrieve place details
    private void getRestaurantInfo() {
        // Define the Place ID.
        final String placeId = getRestaurantIdFromIntent();

        // Specify the fields to return from request
        final List<Place.Field> placeFields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG,
                Place.Field.RATING,
                Place.Field.OPENING_HOURS,
                Place.Field.PHONE_NUMBER,
                Place.Field.PHOTO_METADATAS,
                Place.Field.WEBSITE_URI
        );

        // Construct a request object, passing the place ID and fields array.
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        // Initialize Places API
        Places.initialize(getApplicationContext(), BuildConfig.PLACE_API_KEY);

        // Initialize Places Client
        PlacesClient placesClient = Places.createClient(getApplicationContext());

        // Fetch place to get restaurants details
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();

            // if result is successful, set restaurants details in textview
            setRestaurantsInfo(place);

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                Log.e(TAG, "Place not found: " + exception.getMessage());
            }
        });
    }

    // Set restaurant's details in text views, according to place result
    private void setRestaurantsInfo(Place place) {
        // Format restaurant Address
        FormatAddressModule addressFormatter = new FormatAddressModule(getApplication());
        String address = FormatAddressModule.getFormattedAddressFromLatLng(place.getLatLng());
        // Set Name
        if (place.getName() != null) {
            restaurantName.setText(place.getName());
        }
        // Set Position
        if (place.getLatLng() != null) {
            restaurantAddress.setText(address);
        }
        // Set Rating
        if (place.getRating() != null) {
            restaurantRating.setRating(place.getRating().floatValue());
        }
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
            StringBuilder openingHours = new StringBuilder();
            if (Locale.getDefault().getLanguage().equals("fr")) {
                openingHours.append("Ouvert aujourd'hui de ").append(openHour).append("h").append(formatMinutes(openMinutes))
                        .append(" jusqu'à ").append(closeHour).append("h").append(formatMinutes(closeMinutes)).append("  -");
            } else {
                openingHours.append("Open today from ").append(openHour).append(":").append(formatMinutes(openMinutes)).append(" am")
                        .append(" to ").append(closeHour).append(":").append(formatMinutes(closeMinutes)).append(" pm").append("  -");
            }
            restaurantHours.setText(openingHours.toString());
        }
        // Phone number
        restaurantPhoneNumber = place.getPhoneNumber();
        // Website Url
        restaurantWebSite = place.getWebsiteUri();

        // Get distance between user & restaurant
        LatLng restaurantPosition = place.getLatLng();
        LatLng userPosition = getUserLocationFromIntent();
        String distance = calculateAndFormatDistance(userPosition, restaurantPosition);

        // Set restaurant distance from user
        restaurantDistance.setText(distance);

        // Set restaurant's main picture
        if (place.getPhotoMetadatas() != null) {
            PlacesClient placesClient = Places.createClient(getApplicationContext());
            setPicture(place, placesClient, restaurantPicture);
        } else {
            Glide.with(restaurantPicture.getContext())
                    .load(R.mipmap.no_photo)
                    .centerCrop()
                    .into(restaurantPicture);
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
    private String calculateAndFormatDistance(LatLng startPoint, LatLng endPoint) {
        double distance = SphericalUtil.computeDistanceBetween(startPoint, endPoint);
        return String.format(getString(R.string.distance_in_meters_end), Math.round(distance));
    }

    // Set place picture with placeClient to imageView
    public void setPicture(Place place, PlacesClient placesClient, ImageView imageView) {
        final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
        // If no photo, set No photo picture
        if (metadata == null || metadata.isEmpty()) {
            Log.w(TAG, "No photo metadata.");
            Glide.with(restaurantPicture.getContext())
                    .load(R.mipmap.no_photo)
                    .centerCrop()
                    .into(imageView);
        } else {
            // Get photo
            final PhotoMetadata photoMetadata = metadata.get(0);

            // Create a FetchPhotoRequest.
            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .build();
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                // On success, set photo in ImageView
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                imageView.setImageBitmap(bitmap);
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

    }

    // Contact restaurant by phone
    public void contactByPhone(String phoneNumber) {
        if (!phoneNumber.equals("null")) {
            Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
            phoneIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(phoneIntent);
        } else {
            showSnackBar(binding.getRoot().getRootView(), getString(R.string.no_phoneNumber_notification));
        }
    }

    // Visit restaurant's website
    public void openWebSite(String webSiteUrl) {
        if (!webSiteUrl.equals("null") && webSiteUrl.startsWith("http")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webSiteUrl));
            startActivity(intent);
        } else {
            showSnackBar(binding.getRoot().getRootView(), getString(R.string.no_website_notification));
        }
    }

    // Show snackBar on activity
    private void showSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }
}
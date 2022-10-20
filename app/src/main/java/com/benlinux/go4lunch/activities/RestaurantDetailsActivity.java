package com.benlinux.go4lunch.activities;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.benlinux.go4lunch.BuildConfig;
import com.benlinux.go4lunch.R;
import com.benlinux.go4lunch.data.bookingManager.BookingManager;
import com.benlinux.go4lunch.data.userManager.UserManager;
import com.benlinux.go4lunch.databinding.ActivityRestaurantDetailsBinding;
import com.benlinux.go4lunch.modules.FormatAddressModule;
import com.benlinux.go4lunch.ui.adapters.GuestAdapter;
import com.benlinux.go4lunch.ui.adapters.WorkmateAdapter;
import com.benlinux.go4lunch.ui.models.Booking;
import com.benlinux.go4lunch.ui.models.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.divider.MaterialDividerItemDecoration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.SphericalUtil;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private AppCompatCheckBox likeButton;
    private TextView likeText;
    private ImageButton webSiteButton;

    private String restaurantId;
    private String restaurantPictureUrl;
    private FloatingActionButton bookingButton;

    private RecyclerView mRecyclerView;
    private GuestAdapter adapter;
    private List<String> mGuests;

    // FOR DATA
    private final UserManager userManager = UserManager.getInstance();
    private final BookingManager bookingManager = BookingManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);
        restaurantId = getRestaurantIdFromIntent();
        setToolbar();
        setViews();
        getRestaurantInfo();
        setListeners();
        setLikeButton();
        setListenerOnLikeButton();
        setBookingButtonListener();
        checkIfUserBookedForToday();
    }

    private void setToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Restaurant Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Close details page and turn back to main activity if back button is clicked
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            MainActivity.navigate(this);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Retrieve Place Id from previous activity
    private String getRestaurantIdFromIntent() {
        Intent intent = getIntent();
        return intent.getStringExtra("PLACE_ID");
    }

    // Set booking button drawable according to existing bookings
    private void checkIfUserBookedForToday() {
        bookingManager.getAllBookingsData().addOnCompleteListener(new OnCompleteListener<List<Booking>>() {
            @Override
            public void onComplete(@NonNull Task<List<Booking>> task) {
                Calendar currentDate = Calendar.getInstance(Locale.FRANCE);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String today = dateFormat.format(currentDate.getTime());
                String userId = userManager.getCurrentUser().getUid();
                boolean userBookedToday = false;
                mGuests = new ArrayList<>();

                for (Booking existingBooking : task.getResult()) {
                    // If current user booked for today
                    if (Objects.equals(existingBooking.getUserId(), userId) &&
                            Objects.equals(existingBooking.getBookingDate(), today)) {
                        userBookedToday = true;
                        break;
                    }
                }
                if (userBookedToday) {
                    bookingButton.setImageResource(R.drawable.ic_check_circle);
                } else {
                    bookingButton.setImageResource(R.drawable.ic_edit_calendar);
                }
                for (Booking existingBooking : task.getResult()) {
                    // Add guests to list for this restaurant at date of today
                    if (Objects.equals(existingBooking.getRestaurantId(), restaurantId) &&
                            Objects.equals(existingBooking.getBookingDate(), today)) {
                        mGuests.add(existingBooking.getUserId());
                    }
                }
                setGuestsList(mGuests);
                configRecyclerView();
            }
        });
    }

    private void setGuestsList(List<String> guests) {
        this.mGuests = guests;
    }

    private List<String> getGuestsList() {
        return this.mGuests;
    }

    /**
     * Init the recyclerView that contains workmates who booked in this restaurant
     */
    private void configRecyclerView() {
        adapter = new GuestAdapter(getGuestsList(), this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
        adapter.notifyItemRangeInserted(-1, mGuests.size());

        // If current user is alone to eat in this restaurant
        String userId = userManager.getCurrentUser().getUid();
        if (mGuests.contains(userId) && mGuests.size() == 1) {
            binding.textNoWorkmates.setText(getString(R.string.nobody_booked_today_except_you));
            mRecyclerView.setVisibility(View.GONE);
        }
        // If workmates list is empty, show notification text instead of recyclerview
        if (mGuests.size() == 0) {
            binding.textNoWorkmates.setText(getString(R.string.nobody_booked_today));
        }
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
        likeButton = binding.restaurantDetailsLikeButton;
        webSiteButton = binding.restaurantDetailsWebsiteButton;
        likeText = binding.restaurantDetailsLikeText;
        bookingButton = binding.fabRestaurantDetailsBooking;
        mRecyclerView = binding.workmatesDetails;
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


    // Set FAB button for booking feature, with date picker
    private void setBookingButtonListener() {
        bookingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchDateDialog();
            }
        });
    }

    // Show Snack Bar with a message
    private void showSnackBar(String message){
        View container = findViewById(R.id.restaurant_details_main_container);
        Snackbar.make(container, message, Snackbar.LENGTH_SHORT).show();
    }

    private void launchDateDialog() {
        final Calendar currentDate = Calendar.getInstance(Locale.FRANCE);
        String userId = userManager.getCurrentUser().getUid();

        // Date Picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(RestaurantDetailsActivity.this, new DatePickerDialog.OnDateSetListener() {
            private Boolean bookingExists = false;
            private final Calendar date = Calendar.getInstance(Locale.FRANCE);

            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);

                // Format calendar date
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String formattedDate = dateFormat.format(date.getTime());

                // Define today formatted date
                String today = dateFormat.format(currentDate.getTime());

                // Define booking id
                String bookingId = Long.toString(System.currentTimeMillis());

                // Create booking object
                Booking booking = new Booking(bookingId, restaurantId, restaurantName.getText().toString(),
                        restaurantAddress.getText().toString(), restaurantPicture.getTransitionName(),  userId, formattedDate);

                // Check if booking exists in database
                bookingManager.getAllBookingsData().addOnCompleteListener(new OnCompleteListener<List<Booking>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Booking>> task) {
                        String bookedRestaurant = "";
                        for (Booking existingBooking : task.getResult()) {
                            // If booking already exists, boolean set to true
                            if (Objects.equals(existingBooking.getUserId(), userId) &&
                                    Objects.equals(existingBooking.getBookingDate(), formattedDate)) {
                                bookingExists = true;
                                bookedRestaurant = existingBooking.getRestaurantName();
                                break;
                            }
                        }
                        // If booking already exists, show toast to user
                        if (bookingExists) {
                            showSnackBar(getString(R.string.booking_error) + " " + formattedDate + " at " + bookedRestaurant);
                        } else {
                            // If not, create booking in database
                            bookingManager.createBooking(booking).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    checkIfUserBookedForToday();
                                }
                            });
                        }
                    }
                });
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));

        // Disable dates before today in date picker
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

        // Customize title view
        TextView customTitle = new TextView(getApplicationContext());
        customTitle.setText(R.string.booking_date_dialog_title);
        customTitle.setTextColor(getResources().getColor(R.color.white));
        customTitle.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        customTitle.setGravity(Gravity.CENTER);
        customTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
        customTitle.setPadding(8, 16, 8, 16);
        // Set custom title
        datePickerDialog.setCustomTitle(customTitle);
        // Show date dialog
        datePickerDialog.show();
    }


    // Retrieve place details
    private void getRestaurantInfo() {

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
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(restaurantId, placeFields);

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
            Double ratingValue = place.getRating();
            restaurantRating.setRating(formatRating(ratingValue).floatValue());
        } else {
            restaurantRating.setVisibility(View.GONE);
        }
        // Set Opening hours
        if (place.getOpeningHours() != null) {
            try {
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
            } catch (Exception e) {
                Log.e("Error hours formatting", e.getMessage());
            }
            

        } else {
            // If no opening hours, delete distance marginStart
            restaurantHours.setVisibility(View.GONE);
            setMargin(restaurantDistance, 24,0,40,0);
        }
        // Phone number
        restaurantPhoneNumber = place.getPhoneNumber();
        // Website Url
        restaurantWebSite = place.getWebsiteUri();

        // Get distance between user & restaurant
        LatLng restaurantPosition = place.getLatLng();

        String distance = calculateAndFormatDistance(MainActivity.userLocation, restaurantPosition);

        // Set restaurant distance from user
        restaurantDistance.setText(distance);

        // Set restaurant's main picture
        if (place.getPhotoMetadatas() != null) {
            PlacesClient placesClient = Places.createClient(getApplicationContext());
            setPicture(place, placesClient, restaurantPicture);
        } else {
            Glide.with(restaurantPicture.getContext())
                .load(R.mipmap.no_photo)
                .centerInside()
                .into(restaurantPicture);
            restaurantPicture.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
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
            showSnackBar(binding.getRoot().getRootView(), getString(R.string.no_secured_website));
        }
    }

    private void setLikeButton() {
        Task<User> getData = userManager.getUserData();
        getData.addOnSuccessListener(user -> {
            if (user.getFavoriteRestaurants().contains(restaurantId)) {
                likeButton.setChecked(true);
                likeText.setText(getString(R.string.unlike));
            }
        });
    }

    private void setListenerOnLikeButton() {
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (likeButton.isChecked()) {
                    userManager.addRestaurantToFavorites(restaurantId);
                    likeText.setText(getString(R.string.unlike));
                } else {
                    userManager.removeRestaurantFromFavorites(restaurantId);
                    likeText.setText(getString(R.string.like));
                }
            }
        });
    }

    // Show snackBar on activity
    private void showSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    public static void setMargin(View view, int left, int right, int top, int bottom) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)
                view.getLayoutParams();
        params.setMargins(left, top, right, bottom);
        view.setLayoutParams(params);
    }

    // Format number of rating stars (between 0.5 and 3) as asked from client
    private Double formatRating(Double rating) {
        Double formattedRating = null;
        if (rating >= 0 && rating <= 0.8) {
            formattedRating = 0.5;
        } else if (rating > 0.8 && rating <= 1.6) {
            formattedRating = 1.0;
        } else if (rating > 1.6 && rating <= 2.5) {
            formattedRating = 1.5;
        } else if (rating > 2.5 && rating <= 3.4) {
            formattedRating = 2.0;
        } else if (rating > 3.4 && rating <= 4.3) {
            formattedRating = 2.5;
        } else if (rating > 4.3 && rating <= 5.0) {
            formattedRating = 3.0;
        }
        return formattedRating;
    }
}
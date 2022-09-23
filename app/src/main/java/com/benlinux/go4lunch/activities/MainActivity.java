package com.benlinux.go4lunch.activities;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.benlinux.go4lunch.BuildConfig;
import com.benlinux.go4lunch.R;
import com.benlinux.go4lunch.ui.adapters.PlaceAutoCompleteAdapter;
import com.benlinux.go4lunch.ui.map.MapFragment;
import com.benlinux.go4lunch.ui.userManager.UserManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView drawerNavView;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private TextView userName;
    private TextView userEmail;
    private ImageView userAvatar;

    public static LatLng userLocation;


    // FOR DATA
    private final UserManager userManager = UserManager.getInstance();

    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;// declare this globally

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        this.configureToolBar();
        this.configureNavigation();
        this.configureDrawerLayout();
        this.setDrawerViews();
        this.updateUIWithUserData();
    }


    private LatLng getUserLocation() {
        return userLocation;
    }

    public static void setUserLocation(LatLng location) {
        userLocation = location;
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.search_action);

        // Initialize Places API
        Places.initialize(getApplicationContext(), BuildConfig.PLACE_API_KEY);

        // Specify the fields to return from request
        final List<Place.Field> placeFields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG,
                Place.Field.TYPES
        );

        // Call autocomplete search bar when user clicks on search icon
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                        placeFields).setTypeFilter(TypeFilter.ESTABLISHMENT).build(MainActivity.this);
                startActivityForResult(intent, 100);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    // Start details activity if selected place is a restaurant
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {

            Place place = Autocomplete.getPlaceFromIntent(data);

            if (Objects.requireNonNull(place.getTypes()).toString().contains("RESTAURANT") || place.getTypes().toString().contains("FOOD")) {
                Intent detailsActivityIntent = new Intent(MainActivity.this, RestaurantDetailsActivity.class);
                detailsActivityIntent.putExtra("PLACE_ID", place.getId());
                detailsActivityIntent.putExtra("USER_LOCATION", getUserLocation());
                startActivity(detailsActivityIntent);
            } else {
                Toast.makeText(getApplicationContext(), "This place is not a restaurant", Toast.LENGTH_LONG).show();
            }

        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {

            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void setDrawerViews() {
        drawerNavView = findViewById(R.id.activity_main_nav_view);
        View headerContainer = drawerNavView.getHeaderView(0);
        userName = headerContainer.findViewById(R.id.user_name);
        userEmail = headerContainer.findViewById(R.id.user_email);
        userAvatar = headerContainer.findViewById(R.id.user_avatar);
    }


    @Override
    public void onBackPressed() {
        // Handle back click to close menu
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    public boolean onSupportNavigateUp() {
        // Replace navigation up button with nav drawer button when on start destination
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle Navigation Item Click
        int id = item.getItemId();

        switch (id){
            case R.id.activity_main_drawer_lunch:
                Intent lunchActivityIntent = new Intent(this, UserLunchActivity.class);
                startActivity(lunchActivityIntent);
                break;
            case R.id.activity_main_drawer_settings:
                Intent settingsActivityIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsActivityIntent);
                break;
            case R.id.activity_main_drawer_logout:
                    logout();
                break;
            default:
                break;
        }

        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    // Set custom Toolbar
    private void configureToolBar(){
        //FOR DESIGN
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setElevation(R.dimen.default_elevation_size);
    }


    // Configure Drawer Layout with toggle
    private void configureDrawerLayout(){
        this.drawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    // Configure Drawer & Bottom Navigation
    private void configureNavigation() {

        // Set navigation views
        BottomNavigationView bottomNavView = findViewById(R.id.bottom_nav_view);
        drawerNavView = findViewById(R.id.activity_main_nav_view);
        drawerLayout = findViewById(R.id.activity_main_drawer_layout);

        // Set navigation controller
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);


        // Build and configure App bar
        appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph()) //Pass the ids of fragments from nav_graph which you dont want to show back button in toolbar
                        .setOpenableLayout(drawerLayout)
                        .build();

        //Setup toolbar with back button and drawer icon according to appBarConfiguration
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Setup Navigation for drawer & bottom bar
        NavigationUI.setupWithNavController(bottomNavView, navController);
        NavigationUI.setupWithNavController(drawerNavView, navController);

        // Listener for selected item
        drawerNavView.setNavigationItemSelectedListener(this);
    }


    // Replace
    private void updateUIWithUserData(){
        if(userManager.isCurrentUserLogged()) {
            FirebaseUser user = userManager.getCurrentUser();

            if (user.getPhotoUrl() != null) {
                setProfilePicture(user.getPhotoUrl());
            } else {
                setNoPhoto(userAvatar);
            }
            setTextUserData(user);
        }
    }

    private void setProfilePicture(Uri profilePictureUrl){
        View headerContainer = drawerNavView.getHeaderView(0);
        ImageView userAvatar = headerContainer.findViewById(R.id.user_avatar);
        Glide.with(this)
                .load(profilePictureUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(userAvatar);
    }

    private void setNoPhoto(ImageView destination) {
        Glide.with(this)
            .load(R.mipmap.no_photo)
            .apply(RequestOptions.circleCropTransform())
            .into(destination);
    }

    private void setTextUserData(FirebaseUser user){
        //Get email & username from User
        String email = TextUtils.isEmpty(user.getEmail()) ? getString(R.string.info_no_email_found) : user.getEmail();
        String username = TextUtils.isEmpty(user.getDisplayName()) ? getString(R.string.info_no_username_found) : user.getDisplayName();

        //Update views with data
        userName.setText(username);
        userEmail.setText(email);
    }

    // logout from firebase
    private void logout() {
        // On success, close activity & go to login
        userManager.signOut(this).addOnSuccessListener(aVoid -> {
            finish();
            Intent loginActivityIntent = new Intent(this, LoginActivity.class);
            ActivityCompat.startActivity(this, loginActivityIntent, null);
            Toast.makeText(getApplicationContext(), getString(R.string.disconnection_succeed), Toast.LENGTH_SHORT).show();
        })
        // On failure, show error toast
        .addOnFailureListener(aVoid -> Toast.makeText(getApplicationContext(), getString(R.string.disconnection_failed), Toast.LENGTH_SHORT).show());
    }


    private LatLng getLatLngFromAddress(String address){

        Geocoder geocoder=new Geocoder(MainActivity.this);
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocationName(address, 1);
            if(addressList!=null){
                Address singleaddress=addressList.get(0);
                LatLng latLng=new LatLng(singleaddress.getLatitude(),singleaddress.getLongitude());
                return latLng;
            }
            else{
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    private Address getAddressFromLatLng(LatLng latLng){
        Geocoder geocoder=new Geocoder(MainActivity.this);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5);
            if(addresses!=null){
                Address address=addresses.get(0);
                return address;
            }
            else{
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Used to navigate to Main activity
     * @param activity is original activity
     */
    public static void navigate(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        ActivityCompat.startActivity(activity, intent, null);
    }
}
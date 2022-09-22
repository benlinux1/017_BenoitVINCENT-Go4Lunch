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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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


    // FOR DATA
    private final UserManager userManager = UserManager.getInstance();

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



    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.search_action);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Find your restaurant here");

        SearchView.SearchAutoComplete autoCompleteTextView = (SearchView.SearchAutoComplete) searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        autoCompleteTextView.setAdapter(new PlaceAutoCompleteAdapter(MainActivity.this, android.R.layout.simple_list_item_1));

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Address : ",autoCompleteTextView.getText().toString());
                LatLng latLng = getLatLngFromAddress(autoCompleteTextView.getText().toString());

                if (latLng != null) {
                    Log.d("Lat Lng : ", " " + latLng.latitude + " " + latLng.longitude);
                    Address address = getAddressFromLatLng(latLng);

                    if (address != null) {
                        Log.d("Address : ", "" + address.toString());
                        Log.d("Address Line : ",""+address.getAddressLine(0));
                        Log.d("Phone : ",""+address.getPhone());
                        Log.d("Pin Code : ",""+address.getPostalCode());
                        Log.d("Feature : ",""+address.getFeatureName());
                        Log.d("More : ",""+address.getLocality());
                    }
                    else {
                        Log.d("Address","Address Not Found");
                    }
                }
                else {
                    Log.d("Lat Lng","Lat Lng Not Found");
                }

            }
        });

        return super.onCreateOptionsMenu(menu);
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
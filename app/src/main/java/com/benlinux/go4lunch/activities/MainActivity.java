package com.benlinux.go4lunch.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.benlinux.go4lunch.R;
import com.benlinux.go4lunch.ui.userManager.UserManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
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


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //FOR DESIGN
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView drawerNavView;
    private BottomNavigationView bottomNavView;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;


    // FOR DATA
    private final UserManager userManager = UserManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.configureToolBar();
        this.configureNavigation();
        this.configureDrawerLayout();
        this.updateUIWithUserData();
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


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle Navigation Item Click
        int id = item.getItemId();

        switch (id){
            case R.id.activity_main_drawer_lunch:
                break;
            case R.id.activity_main_drawer_settings:
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
        this.toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
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
        bottomNavView = findViewById(R.id.bottom_nav_view);
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


    private void updateUIWithUserData(){
        if(userManager.isCurrentUserLogged()) {
            FirebaseUser user = userManager.getCurrentUser();

            if (user.getPhotoUrl() != null) {
                setProfilePicture(user.getPhotoUrl());
            }
            setTextUserData(user);
        }
    }

    private void setProfilePicture(Uri profilePictureUrl){
        drawerNavView = findViewById(R.id.activity_main_nav_view);
        View headerContainer = drawerNavView.getHeaderView(0);
        ImageView userAvatar = headerContainer.findViewById(R.id.user_avatar);
        Glide.with(this)
                .load(profilePictureUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(userAvatar);
    }

    private void setTextUserData(FirebaseUser user){

        drawerNavView = findViewById(R.id.activity_main_nav_view);
        View headerContainer = drawerNavView.getHeaderView(0);
        TextView userNameTextView = headerContainer.findViewById(R.id.user_name);
        TextView userEmailTextView = headerContainer.findViewById(R.id.user_email);

        //Get email & username from User
        String email = TextUtils.isEmpty(user.getEmail()) ? getString(R.string.info_no_email_found) : user.getEmail();
        String username = TextUtils.isEmpty(user.getDisplayName()) ? getString(R.string.info_no_username_found) : user.getDisplayName();

        //Update views with data
        userNameTextView.setText(username);
        userEmailTextView.setText(email);
    }

    // logout from firebase
    private void logout() {
        userManager.signOut(this).addOnSuccessListener(aVoid -> {
                Intent loginActivityIntent = new Intent(this, LoginActivity.class);
                ActivityCompat.startActivity(this, loginActivityIntent, null);
                finish();
        });
    }
}
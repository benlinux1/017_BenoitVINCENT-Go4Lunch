package com.benlinux.go4lunch.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import androidx.fragment.app.Fragment;

import com.benlinux.go4lunch.BuildConfig;
import com.benlinux.go4lunch.R;
import com.benlinux.go4lunch.activities.MainActivity;
import com.benlinux.go4lunch.activities.RestaurantDetailsActivity;
import com.benlinux.go4lunch.databinding.FragmentMapViewBinding;
import com.benlinux.go4lunch.ui.adapters.InfoWindowForMapAdapter;
import com.benlinux.go4lunch.modules.FetchPlacesData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


@RequiresApi(api = Build.VERSION_CODES.N) // Required for getOrDefault method in location permissions
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private FragmentMapViewBinding binding;
    private GoogleMap mGoogleMap;
    private SupportMapFragment mapFragment;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient client;
    private static final int DEFAULT_ZOOM = 14;
    private boolean locationPermissionGranted;
    public LatLng actualLocation;
    private Double actualLatitude;
    private Double actualLongitude;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize user location
        client = LocationServices.getFusedLocationProviderClient(requireActivity());
        // Request user location permission
        getLocationPermission();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMapViewBinding.inflate(inflater, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set async google map
        if (mapFragment !=null) {
            mapFragment.getMapAsync(this);
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    @Override
    // Google map For OnMapReady callback implementation
    public void onMapReady(GoogleMap googleMap) {
        // When map is loaded
        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Check permission & get user location if needed
        if (locationPermissionGranted) {
            if (getUserLocation() == null) {
                getCurrentLocation();
            } else {
                setCamera(googleMap);
                findRestaurants();
            }
        }

        // update UI with or without blue point location and map centering button
        updateLocationUI();

        // Set map listeners
        setListenerOnMapClick(googleMap);
        setListenerOnMyLocationIcon(googleMap);
        setListenerOnMyLocationButton(googleMap);

        // Set custom info window layout
        googleMap.setInfoWindowAdapter(new InfoWindowForMapAdapter(getContext()));

        // Set click listener on Info window
        setInfoWindowClickListener(googleMap);
    }

    private void setListenerOnMapClick(GoogleMap googleMap) {
        // Set listener for clicks on Map
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            // When clicked on map
            public void onMapClick(@NonNull LatLng latLng) {
                // Remove all marker
                googleMap.clear();
                // save new location
                setUserPosition(new LatLng(latLng.latitude, latLng.longitude));
                // Initialize marker options
                MarkerOptions markerOptions = new MarkerOptions();
                // Set title of marker
                markerOptions.title("Search location");
                // Set position of marker
                markerOptions.position(latLng);
                // Animating to zoom the marker
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));
                // Add marker on map
                googleMap.addMarker(markerOptions);
                // Actualize restaurants on map
                findRestaurants();
            }
        });
    }

    private void setListenerOnMyLocationIcon(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        googleMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
            @Override
            public void onMyLocationClick(@NonNull Location location) {
                googleMap.clear();
                LatLng userPosition = new LatLng(location.getLatitude(), location.getLongitude());
                // Actualize user fictive position on click
                setUserPosition(userPosition);
                setMarkerForUserLocation(userPosition, "actual");
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(actualLatitude, actualLongitude), DEFAULT_ZOOM));
                findRestaurants();
            }
        });
    }


    private void setListenerOnMyLocationButton(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                googleMap.clear();
                getCurrentLocation();
                return true;
            }
        });
    }


    // Prompt user for location permission (Coarse & fine)
    private void getLocationPermission() {
        ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                .RequestMultiplePermissions(), result -> {
                    Boolean fineLocationGranted = result.getOrDefault(
                        Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted = result.getOrDefault(
                        Manifest.permission.ACCESS_COARSE_LOCATION, false);
                    if (fineLocationGranted != null && fineLocationGranted) {
                        locationPermissionGranted = true;
                    } else if (coarseLocationGranted != null && coarseLocationGranted) {
                        locationPermissionGranted = true;
                    } else {
                        // When location service is not enabled, open location settings
                        Toast.makeText(getContext(), "Please enable position & restart app to use map features", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                }
            );

        /*
        * Before permission request, check whether the app has already the permissions
        * and whether the app needs to show a permission rationale dialog
        */
        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    // Set listener on restaurant's info window to launch restaurant's details page
    @SuppressLint("PotentialBehaviorOverride")
    private void setInfoWindowClickListener(GoogleMap mGoogleMap) {
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {

                Intent restaurantDetailsIntent = new Intent(getContext(), RestaurantDetailsActivity.class);
                // Retrieve place_id in tag
                String placeId= String.valueOf(marker.getTag());
                // Retrieve user location
                LatLng userLocation = actualLocation;
                // Send place_id & user location in order to get it in details activity
                restaurantDetailsIntent.putExtra("PLACE_ID", placeId);
                restaurantDetailsIntent.putExtra("USER_LOCATION", userLocation);

                if (!placeId.equals("null")) {
                    startActivity(restaurantDetailsIntent);
                }
            }
        });
    }

    /**
     * Add or remove user location with blue point marker on Google Map
     * and map centering button according to permissions
     */
    @SuppressLint("MissingPermission")
    private void updateLocationUI() {
        if (mGoogleMap == null) {
            Toast.makeText(getContext(), "No Map", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            if (locationPermissionGranted) {
                // When location is granted, mark current location with blue point
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                // When location is not granted, don't mark map with blue point
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        // Initialize location manager
        LocationManager locationManager = (LocationManager) requireActivity()
                .getSystemService(Context.LOCATION_SERVICE);
        // Check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            try {
                // When Location is enabled, get last location
                client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        // Initialize location
                        Location location = task.getResult();

                        // Check condition
                        if (location != null) {

                            // save actual location to actualLocation variable
                            setUserPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                            // set markers
                            setMarkerForUserLocation(new LatLng(location.getLatitude(), location.getLongitude()),
                                    "actual");

                            setCamera(mGoogleMap);
                            findRestaurants();

                        } else {

                            // When location result is null, initialize location request
                            LocationRequest locationRequest = new LocationRequest()
                                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                    .setInterval(10000)
                                    .setFastestInterval(1000)
                                    .setNumUpdates(1);

                            // Initialize Location callback
                            LocationCallback locationCallback = new LocationCallback() {
                                @Override
                                public void onLocationResult(LocationResult locationResult) {
                                    // Initialize last location
                                    Location lastLocation = locationResult.getLastLocation();
                                    // save last location in actualLocation variable
                                    setUserPosition(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
                                    // set markers
                                    setMarkerForUserLocation(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()),
                                            "last");

                                    setCamera(mGoogleMap);
                                    findRestaurants();
                                }
                            };
                            // Request location updates
                            client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                        }
                    }
                });
            } catch (Exception ex)  {
                Log.e("Exception: %s", ex.getMessage());
            }

        } else {
            // Open location settings
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    private void setMarkerForUserLocation(LatLng userLocation, String locationType) {
        // Set map callback
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                // When map is loaded
                mGoogleMap = googleMap;

                // Add marker & move camera to user location
                if (locationType.equals("actual")) {
                    googleMap.addMarker(new MarkerOptions().position(userLocation).title("Actual location"));
                } else if (locationType.equals("last")) {
                    googleMap.addMarker(new MarkerOptions().position(userLocation).title("Last known location"));
                }
            }
        });
    }

    private void setCamera(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(getUserLatitude(), getUserLongitude()), DEFAULT_ZOOM));
    }

    public void findRestaurants() {
        // Build Place request with URL
        String apiKey = BuildConfig.PLACE_API_KEY;

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + "location=" + getUserLatitude() + "," + getUserLongitude() +
                "&radius=2000" +
                "&type=restaurant" +
                "&key=" + apiKey;
                Object[] restaurantData = new Object[2];
                restaurantData[0] = mGoogleMap;
                restaurantData[1] = url;

        FetchPlacesData fetchPlacesData = new FetchPlacesData(getContext(), "map");
        fetchPlacesData.execute(restaurantData);
    }

    public void setUserPosition(LatLng location) {
        actualLocation = location;
        actualLatitude = location.latitude;
        actualLongitude = location.longitude;
        MainActivity.setUserLocation(location);
    }

    private Double getUserLatitude() {
        return MainActivity.userLocation.latitude;
    }
    private Double getUserLongitude() {
        return MainActivity.userLocation.longitude;
    }
    private LatLng getUserLocation() {
        return MainActivity.userLocation;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (locationPermissionGranted && getUserLocation() == null) {
            getCurrentLocation();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }





}
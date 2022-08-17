package com.benlinux.go4lunch.ui.map;

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

import com.benlinux.go4lunch.R;
import com.benlinux.go4lunch.databinding.FragmentMapViewBinding;

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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


@RequiresApi(api = Build.VERSION_CODES.N) // Required for getOrDefault method in location permissions
public class MapFragment extends Fragment implements OnMapReadyCallback{

    private FragmentMapViewBinding binding;

    private static GoogleMap mGoogleMap;

    SupportMapFragment mapFragment;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient client;

    private static final int DEFAULT_ZOOM = 15;

    private boolean locationPermissionGranted;





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
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

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
                                getCurrentLocation();
                                updateLocationUI();
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                locationPermissionGranted = true;
                                getCurrentLocation();
                                updateLocationUI();
                            } else {
                                // When location service is not enabled, open location settings and put app in background
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            }
                        }
                );

        // Before permission request, check whether the app has already the permissions
        // and whether the app needs to show a permission rationale dialog
        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }


    @Override
    // Google map For OnMapReady callback implementation
    public void onMapReady(GoogleMap googleMap) {

        // When map is loaded
        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        updateLocationUI();
    }



    @SuppressLint("MissingPermission")
    // Add user location with blue point marker on Google Map
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
                            mapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(@NonNull GoogleMap googleMap) {
                                    // When map is loaded
                                    mGoogleMap = googleMap;

                                    // Add marker & move camera to user location
                                    mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),
                                            location.getLongitude())).title("Actual location"));
                                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                            new LatLng(location.getLatitude(),
                                                    location.getLongitude()), DEFAULT_ZOOM));

                                    // Set listener for clicks on Map
                                    googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                        @Override
                                        public void onMapClick(@NonNull LatLng latLng) {
                                            // When clicked on map
                                            // Initialize marker options
                                            MarkerOptions markerOptions = new MarkerOptions();
                                            // Set position of marker
                                            markerOptions.position(latLng);
                                            // Set title of marker
                                            markerOptions.title(latLng.latitude+" : "+latLng.longitude);
                                            // Remove all marker
                                            googleMap.clear();
                                            // Animating to zoom the marker
                                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
                                            // Add marker on map
                                            googleMap.addMarker(markerOptions);
                                        }
                                    });
                                }
                            });
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
                                    // Add marker & move camera to user last known location
                                    mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lastLocation.getLatitude(),
                                            lastLocation.getLongitude())).title("Last known location"));
                                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                            new LatLng(lastLocation.getLatitude(),
                                                    lastLocation.getLongitude()), DEFAULT_ZOOM));
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
            // When location service is not enabled, open location settings in foreground
            // so app is put in background
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
package com.benlinux.go4lunch.ui.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import androidx.fragment.app.Fragment;

import com.benlinux.go4lunch.BuildConfig;
import com.benlinux.go4lunch.R;
import com.benlinux.go4lunch.databinding.FragmentMapViewBinding;

import com.benlinux.go4lunch.ui.models.FetchPlacesData;
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
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private FragmentMapViewBinding binding;

    private GoogleMap mGoogleMap;

    SupportMapFragment mapFragment;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient client;

    private static final int DEFAULT_ZOOM = 15;

    private boolean locationPermissionGranted;

    private LatLng actualLocation;

    private Double actualLatitude;
    private Double actualLongitude;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize user location
        client = LocationServices.getFusedLocationProviderClient(requireActivity());

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Request user location permission
        getLocationPermission();

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
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                locationPermissionGranted = true;
                            } else {
                                // When location service is not enabled, open location settings and put app in background
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



    @Override
    // Google map For OnMapReady callback implementation
    public void onMapReady(GoogleMap googleMap) {
        // When map is loaded
        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (locationPermissionGranted) {
            // Get user location
            getCurrentLocation();
        } else {
            // Ask permission
            getLocationPermission();
        }

        // update UI with or without blue point location and map centering button
        updateLocationUI();

        // If user is located
        if (actualLocation != null)  {
            findRestaurants();
        }

    }




    /*
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

    public void setUserPosition(LatLng location) {
        actualLocation = location;
        actualLatitude = location.latitude;
        actualLongitude = location.longitude;
    }

    private Double getUserLatitude() {
        return actualLatitude;
    }

    private Double getUserLongitude() {
        return actualLongitude;
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

                            // Set map callback
                            mapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(@NonNull GoogleMap googleMap) {
                                    // When map is loaded
                                    mGoogleMap = googleMap;

                                    // Add marker & move camera to user location
                                    mGoogleMap.addMarker(new MarkerOptions().position(actualLocation).title("Actual location"));
                                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(actualLatitude, actualLongitude), DEFAULT_ZOOM));

                                    // find & mark restaurants
                                    if (actualLocation != null)  {
                                        findRestaurants();
                                    }

                                    // Set listener for clicks on Map
                                    googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                        @Override
                                        public void onMapClick(@NonNull LatLng latLng) {
                                            // When clicked on map
                                            // save new location
                                            setUserPosition(new LatLng(latLng.latitude, latLng.longitude));
                                            // Initialize marker options
                                            MarkerOptions markerOptions = new MarkerOptions();
                                            // Set position of marker
                                            markerOptions.position(latLng);
                                            // Remove all marker
                                            googleMap.clear();
                                            // Animating to zoom the marker
                                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
                                            // Add marker on map
                                            googleMap.addMarker(markerOptions);
                                            // find & mark restaurants with new location
                                            findRestaurants();
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

                                    // save last location in actualLocation variable
                                    setUserPosition(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));

                                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                                        @Override
                                        public void onMapReady(@NonNull GoogleMap googleMap) {
                                            // When map is loaded
                                            mGoogleMap = googleMap;

                                            // Add marker & move camera to user location
                                            mGoogleMap.addMarker(new MarkerOptions().position(actualLocation).title("Last location"));
                                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(actualLocation.latitude, actualLocation.longitude), DEFAULT_ZOOM));

                                            // find & mark restaurants
                                            if (actualLocation != null)  {
                                                findRestaurants();
                                            }

                                            // Set listener for clicks on Map
                                            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                                @Override
                                                public void onMapClick(@NonNull LatLng latLng) {
                                                    // When clicked on map
                                                    // save new location in actualLocation variable
                                                    setUserPosition(new LatLng(latLng.latitude, latLng.longitude));
                                                    // Initialize marker options
                                                    MarkerOptions markerOptions = new MarkerOptions();
                                                    // Set position of marker
                                                    markerOptions.position(latLng);
                                                    // Remove all marker
                                                    googleMap.clear();
                                                    // Animating to zoom the marker
                                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
                                                    // Add marker on map
                                                    googleMap.addMarker(markerOptions);
                                                    // find & mark restaurants with new location
                                                    if (actualLocation != null)  {
                                                        findRestaurants();
                                                    }
                                                }
                                            });
                                        }
                                    });
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
            /**
             * When location service is not enabled, open location settings in foreground
             * so that app is put in background
             */
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }


    public void findRestaurants() {
        // Build Place request with URL
        String apiKey = BuildConfig.PLACE_API_KEY;
        StringBuilder stringBuilder = new StringBuilder
                ("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                stringBuilder.append("location=" + getUserLatitude() + "," + getUserLongitude());
                stringBuilder.append("&radius=2000");
                stringBuilder.append("&type=restaurant");
                stringBuilder.append("&key=" + apiKey);

                String url = stringBuilder.toString();
                Object[] restaurantData = new Object[2];
                restaurantData[0] = mGoogleMap;
                restaurantData[1] = url;

        FetchPlacesData fetchPlacesData = new FetchPlacesData(getContext());
        fetchPlacesData.execute(restaurantData);
    }

/**
    public class PlaceTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            String data = null;
            try {
                // Initialize data
                data = PlaceDownloadUrl.downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }
*/




/**
        private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {
            @Override
            protected List<HashMap<String, String>> doInBackground(String... strings) {
                // Create JSON parser class
                JsonParser jsonParser = new JsonParser();
                // Initialize hash map list
                List<HashMap<String, String>> mapList = null;
                JSONObject object = null;
                try {
                    // Initialize Json object
                    object = new JSONObject(strings[0]);
                    // Parse Json object
                    mapList = jsonParser.parseResult(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Return map list
                return mapList;
            }
            @Override
            protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
                // clear map
                mGoogleMap.clear();
                // Use for loop
                for (int i=0; i<hashMaps.size(); i++) {
                    // Initialize has map
                    HashMap<String, String> hashMapList = hashMaps.get(i);
                    // Get latitude
                    double lat = Double.parseDouble(Objects.requireNonNull(hashMapList.get("lat")));
                    // Get longitude
                    double lng = Double.parseDouble(Objects.requireNonNull(hashMapList.get("lng")));
                    // Get name
                    String name = hashMapList.get("name");
                    // Concat latitude & longitude
                    LatLng latLng = new LatLng(lat, lng);
                    // Initialize marker options
                    MarkerOptions options = new MarkerOptions();
                    // Set position
                    options.position(latLng);
                    // Set title
                    options.title(name);
                    // add marker on map
                    mGoogleMap.addMarker(options);
                }
            }
        }
    }
 */

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
package com.benlinux.go4lunch.ui.list;

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
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.benlinux.go4lunch.BuildConfig;
import com.benlinux.go4lunch.R;
import com.benlinux.go4lunch.databinding.FragmentListViewBinding;
import com.benlinux.go4lunch.ui.map.MapFragment;
import com.benlinux.go4lunch.ui.models.FetchPlacesData;
import com.benlinux.go4lunch.ui.models.Restaurant;
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

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Objects;

public class ListFragment extends Fragment {

    private FragmentListViewBinding binding;
    private ListAdapter adapter;
    private JSONArray mRestaurants;
    private FusedLocationProviderClient client;
    public  LatLng actualLocation;
    private Double actualLatitude;
    private Double actualLongitude;
    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize user location
        client = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentListViewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        mRestaurants = new JSONArray();
        mRecyclerView = binding.listRestaurants;
        configRecyclerView();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getCurrentLocation();
    }

    /**
     * Init the recyclerView
     */
    private void configRecyclerView() {

        adapter = new ListAdapter(mRestaurants, getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(adapter);
        adapter.notifyItemRangeInserted(- 1, mRestaurants.length());
    }

    public void findRestaurants() {
        // Build Place request with URL
        String apiKey = BuildConfig.PLACE_API_KEY;

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + "location=" + getUserLatitude() + "," + getUserLongitude() +
                "&radius=2000" +
                "&type=restaurant" +
                "&key=" + apiKey +
                "&rankBy=distance";

        Object[] restaurantData = new Object[4];
        restaurantData[0] = this.mRestaurants;
        restaurantData[1] = url;
        restaurantData[2] = this.adapter;
        restaurantData[3] = this.actualLocation;

        FetchPlacesData fetchPlacesData = new FetchPlacesData(getContext(), "list");
        fetchPlacesData.execute(restaurantData);
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
                client.getLastLocation().addOnCompleteListener(task -> {
                    // Initialize location
                    Location location = task.getResult();
                    // Check condition
                    if (location != null) {

                        // save actual location to actualLocation variable
                        setUserPosition(new LatLng(location.getLatitude(), location.getLongitude()));

                        // When user location is found, find restaurants
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

                                // When user location is found, find restaurants
                                findRestaurants();

                            }
                        };
                        // Request location updates
                        client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                });
            } catch (Exception ex) {
                Log.e("Exception: %s", ex.getMessage());
            }
        }
    }

    public void setUserPosition(LatLng location) {
        this.actualLocation = location;
        this.actualLatitude = location.latitude;
        this.actualLongitude = location.longitude;
    }

    private Double getUserLatitude() {
        return this.actualLatitude;
    }

    private Double getUserLongitude() {
        return this.actualLongitude;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
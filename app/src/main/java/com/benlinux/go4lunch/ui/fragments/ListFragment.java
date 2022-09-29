package com.benlinux.go4lunch.ui.fragments;

import android.os.Build;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.benlinux.go4lunch.BuildConfig;
import com.benlinux.go4lunch.activities.MainActivity;
import com.benlinux.go4lunch.databinding.FragmentListViewBinding;
import com.benlinux.go4lunch.ui.adapters.ListAdapter;
import com.benlinux.go4lunch.modules.FetchPlacesData;
import com.benlinux.go4lunch.ui.models.Restaurant;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    private FragmentListViewBinding binding;
    private ListAdapter adapter;
    private List<Restaurant> mRestaurants;
    public  LatLng actualLocation;
    private Double actualLatitude;
    private Double actualLongitude;
    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentListViewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Get user position set on map in main fragment
        setUserPosition(MainActivity.userLocation);

        mRecyclerView = binding.listRestaurants;
        mRestaurants = new ArrayList<>();
        configRecyclerView();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findRestaurants();

    }

    /**
     * Init the recyclerView that contains nearby restaurants
     */
    private void configRecyclerView() {
        adapter = new ListAdapter(mRestaurants, getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(adapter);
        adapter.notifyItemRangeInserted(- 1, mRestaurants.size());
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
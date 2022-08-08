package com.benlinux.go4lunch.ui.map;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.benlinux.go4lunch.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapViewModel extends ViewModel {

    private final MutableLiveData<LatLng> mLocation;


    public MapViewModel() {
        mLocation = new MutableLiveData<LatLng>();
        mLocation.setValue(new LatLng(-33.852, 151.211));
    }

    public LiveData<LatLng> getPosition() {
        return mLocation;
    }


}
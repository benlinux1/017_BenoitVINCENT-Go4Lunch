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

    private MutableLiveData<LatLng> mLocation;


    public MapViewModel() {
        mLocation = new MutableLiveData<>();
        mLocation.setValue(new LatLng(49.17824211438383, -0.36613963544368744));
    }

    public LiveData<LatLng> getPosition() {
        return mLocation;
    }

    public void setUserPosition(MutableLiveData<LatLng> position) {
        this.mLocation = position;
    }


}
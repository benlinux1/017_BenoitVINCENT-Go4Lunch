package com.benlinux.go4lunch.ui.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONArray;

public class ListViewModel extends ViewModel {

    private JSONArray mRestaurants;

    public ListViewModel() {
        mRestaurants = getRestaurants();
        if (mRestaurants == null) {
            mRestaurants = new JSONArray();
        }
    }

    public JSONArray getRestaurants() {
        return mRestaurants;
    }

    public void setRestaurants(JSONArray restaurants) {
        mRestaurants = restaurants;
    }
}
package com.benlinux.go4lunch.ui.models;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.benlinux.go4lunch.R;
import com.benlinux.go4lunch.activities.MainActivity;
import com.benlinux.go4lunch.ui.list.ListFragment;
import com.benlinux.go4lunch.ui.list.ListViewModel;
import com.benlinux.go4lunch.ui.map.MapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class FetchPlacesData extends AsyncTask<Object, String, String> {

    String googleNearByPlacesData;
    GoogleMap googleMap;
    String url;
    Context localContext;

    JSONArray restaurantsResults;


    public FetchPlacesData(Context context) {
        super();
        localContext = context;
    }

    @Override
    protected void onPostExecute(String s) {

        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray =  jsonObject.getJSONArray("results");

            restaurantsResults = new JSONArray();
            restaurantsResults.put(jsonArray);
            new ListViewModel().setRestaurants(restaurantsResults);

            int restaurantMarker;

            // Loop to get restaurants details from each result of the Place request
            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject restaurantInfo = jsonArray.getJSONObject(i);
                JSONObject getLocation = restaurantInfo.getJSONObject("geometry")
                        .getJSONObject("location");

                // Get restaurant's latitude & longitude
                String lat = getLocation.getString("lat");
                String lng = getLocation.getString("lng");

                // Get restaurant's name
                JSONObject getInfo = jsonArray.getJSONObject(i);
                String name = getInfo.getString("name");

                // Define restaurant marker icon
                restaurantMarker = R.drawable.ic_marker_48;

                // Define restaurant LatLng for marker
                LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

                // Get place id (used to retrieve place info in details activity)
                String placeId = getInfo.getString("place_id");

                // Define marker options (restaurant's name, address, position, icon)
                MarkerOptions markerOptions = new MarkerOptions()
                    .title(name)
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(restaurantMarker));

                // Set place id in tag (used to retrieve place info in details activity)
                Objects.requireNonNull(googleMap.addMarker(markerOptions)).setTag(placeId);
                // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    @Override
    protected String doInBackground (Object... objects) {

        try {
            googleMap = (GoogleMap) objects[0];
            url = (String) objects[1];
            PlaceDownloadUrl downloadUrl = new PlaceDownloadUrl();
            googleNearByPlacesData = downloadUrl.downloadUrl(url);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return googleNearByPlacesData;
    }

}

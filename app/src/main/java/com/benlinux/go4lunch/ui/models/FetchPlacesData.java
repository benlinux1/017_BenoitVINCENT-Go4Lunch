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

import com.benlinux.go4lunch.R;
import com.benlinux.go4lunch.activities.MainActivity;
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
import java.util.List;
import java.util.Locale;

public class FetchPlacesData extends AsyncTask<Object, String, String> {

    String googleNearByPlacesData;
    GoogleMap googleMap;
    String url;
    Context localContext;

    public FetchPlacesData(Context context) {
        super();
        localContext = context;
    }

    @Override
    protected void onPostExecute(String s) {

        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray =  jsonObject.getJSONArray("results");

            int restaurantMarker;



            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                JSONObject getLocation = jsonObject1.getJSONObject("geometry")
                        .getJSONObject("location");

                String lat = getLocation.getString("lat");
                String lng = getLocation.getString("lng");

                JSONObject getInfo = jsonArray.getJSONObject(i);
                String name = getInfo.getString("name");

                restaurantMarker = R.drawable.ic_marker_48;

                LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

                String address = getAddressFromLatLng(latLng);

                MarkerOptions markerOptions = new MarkerOptions()
                    .title(name)
                    .snippet(address)
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(restaurantMarker));
                googleMap.addMarker(markerOptions);
                // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Return address according to Latitude & longitude params
    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this.localContext, Locale.getDefault());
        String strAdd = "";
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            Address returnedAddress = addresses.get(0);
            StringBuilder strReturnedAddress = new StringBuilder("");


            for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
            }
            // String mFullAddress = returnedAddress.getAddressLine(0);
            String mStreetNumber = returnedAddress.getSubThoroughfare();
            String mStreet = returnedAddress.getThoroughfare();
            String mPostalCode = returnedAddress.getPostalCode();
            String mCity = returnedAddress.getLocality();
            strReturnedAddress.append(mStreetNumber).append(" ").append(mStreet).append(" - ").append(mPostalCode).append(" ").append(mCity);
            strAdd = strReturnedAddress.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return strAdd;
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

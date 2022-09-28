package com.benlinux.go4lunch.modules;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.benlinux.go4lunch.R;
import com.benlinux.go4lunch.ui.adapters.ListAdapter;
import com.benlinux.go4lunch.ui.models.Restaurant;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class FetchPlacesData extends AsyncTask<Object, String, String> {

    private String googleNearByPlacesData;
    private GoogleMap googleMap;
    @SuppressLint("StaticFieldLeak")
    private final Context localContext;
    private final String dataType;

    private List<Restaurant> restaurantsList;
    private ListAdapter adapter;
    private LatLng userLocation;


    public FetchPlacesData(Context context, String data) {
        super();
        localContext = context;
        dataType = data;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onPostExecute(String s) {

        try {
            // Get results from NearBy API
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            // If request come from map fragment, set elements on google map
            if (dataType.equals("map") ) {

                int restaurantMarker;

                // Loop to get restaurants details from each result of the Place request
                for (int i = 0; i < jsonArray.length(); i++) {
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

            // else, request come from list fragment, so set elements into recyclerview adapter
            } else {

                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject restaurantInfo = jsonArray.getJSONObject(i);

                        String placeId = restaurantInfo.getString("place_id");
                        String name = restaurantInfo.getString("name");
                        Double rating = restaurantInfo.getDouble("rating");

                        // Get restaurant's full Location
                        JSONObject getLocation = restaurantInfo.getJSONObject("geometry")
                                .getJSONObject("location");

                        // Get restaurant's latitude & longitude
                        String lat = getLocation.getString("lat");
                        String lng = getLocation.getString("lng");

                        // Define restaurant's LatLng
                        LatLng restaurantLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

                        // Formatted Address
                        String formattedAddress = getFormattedAddressFromLatLng(restaurantLocation);

                        // Formatted distance
                        String distance = calculateAndFormatDistance(userLocation, restaurantLocation);

                        // Create new restaurant for each result of Nearby Places API
                        Restaurant restaurant = new Restaurant(placeId, name, formattedAddress, rating, null, distance, restaurantLocation);

                        // Add each restaurant in the list
                        restaurantsList.add(restaurant);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Pass user's location data to calculate distance from restaurant
                    adapter.setUserLocation(userLocation);
                    // Set restaurants list to adapter
                    adapter.initList(restaurantsList);
                }
            }
        } catch (JSONException e ) {
            e.printStackTrace();
        }
    }


    @Override
    protected String doInBackground (Object... objects) {
        // if request come from mapFragment
        String url;
        if (dataType.equals("map") ) {
            try {
                googleMap = (GoogleMap) objects[0];
                url = (String) objects[1];
                PlaceDownloadUrl downloadUrl = new PlaceDownloadUrl();
                googleNearByPlacesData = downloadUrl.downloadUrl(url);



            } catch (IOException e) {
                e.printStackTrace();
            }
        // else, if request come from listFragment
        } else {
            try {
                restaurantsList = (List<Restaurant>) objects[0];
                url = (String) objects[1];
                adapter = (ListAdapter) objects[2];
                // get user location from request parameters to calculate distance
                userLocation = (LatLng) objects[3];
                PlaceDownloadUrl downloadUrl = new PlaceDownloadUrl();
                googleNearByPlacesData = downloadUrl.downloadUrl(url);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return googleNearByPlacesData;
    }

    // Format LatLng data to readable address
    private String getFormattedAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(localContext, Locale.getDefault());
        String formattedAddress;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            Address returnedAddress = addresses.get(0);
            StringBuilder strReturnedAddress = new StringBuilder();

            for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
            }
            String mStreetNumber = returnedAddress.getSubThoroughfare();
            String mStreet = returnedAddress.getThoroughfare();
            strReturnedAddress.append(mStreetNumber).append(" ").append(mStreet);
            formattedAddress = strReturnedAddress.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return formattedAddress;
    }

    // Calculate distance between two points, and format to string in meters
    @SuppressLint("DefaultLocale")
    private String calculateAndFormatDistance(LatLng startPoint, LatLng endPoint) {
        double distance = SphericalUtil.computeDistanceBetween(startPoint, endPoint);
        return String.format("%d m", Math.round(distance));
    }

}

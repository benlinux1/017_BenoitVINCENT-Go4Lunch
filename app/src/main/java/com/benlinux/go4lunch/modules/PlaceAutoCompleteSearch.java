package com.benlinux.go4lunch.modules;

import android.util.Log;

import com.benlinux.go4lunch.BuildConfig;
import com.benlinux.go4lunch.ui.models.Booking;
import com.benlinux.go4lunch.ui.models.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class PlaceAutoCompleteSearch {

    public List<Restaurant> autoComplete(String input) {
        List<Restaurant> restaurantsList = new ArrayList();
        HttpURLConnection connection = null;
        StringBuilder jsonResult = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/autocomplete/json?");
            sb.append("input=").append(input);
            sb.append("&type=restaurant");
            sb.append("&key=").append(BuildConfig.PLACE_API_KEY);
            URL url = new URL(sb.toString());
            connection = (HttpURLConnection)url.openConnection();
            InputStreamReader inputStreamReader=new InputStreamReader(connection.getInputStream());

            int read;

            char[] buff = new char[1024];
            while ((read = inputStreamReader.read(buff)) != -1){
                jsonResult.append(buff,0, read);
            }

            Log.d("JSon",jsonResult.toString());
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if(connection != null){
                connection.disconnect();
            }
        }

        try {
            JSONObject jsonObject=new JSONObject(jsonResult.toString());
            JSONArray prediction=jsonObject.getJSONArray("predictions");

            for (int i = 0; i < prediction.length(); i++) {

                String description = prediction.getJSONObject(i).getString("description");
                String id = prediction.getJSONObject(i).getString("place_id");
                Restaurant restaurant = new Restaurant(id, description, null, null, null, null, null, new ArrayList<>());

                restaurantsList.add(restaurant);
            }

        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return restaurantsList;
    }
}
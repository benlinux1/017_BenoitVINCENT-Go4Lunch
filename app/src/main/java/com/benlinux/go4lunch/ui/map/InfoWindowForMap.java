package com.benlinux.go4lunch.ui.map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.benlinux.go4lunch.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.Locale;

public class InfoWindowForMap implements GoogleMap.InfoWindowAdapter {
    Context context;
    LayoutInflater inflater;

    public InfoWindowForMap(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.info_window_map, null);

        // Getting the position from the marker
        LatLng latLng = marker.getPosition();

        // Getting reference to the TextView to set restaurant's name
        TextView restaurantName = (TextView) view.findViewById(R.id.title);
        // Getting reference to the TextView to set restaurant's full address
        TextView restaurantAddress = (TextView) view.findViewById(R.id.snippet);
        // Getting reference to the TextView to set place-id
        TextView restaurantId = (TextView) view.findViewById(R.id.place_id);
        // Getting reference to the TextView to set street & street number
        TextView restaurantStreet = (TextView) view.findViewById(R.id.street);
        // Getting reference to the TextView to set postal code & city
        TextView restaurantCity = (TextView) view.findViewById(R.id.postalCodeAndCity);

        // Setting the restaurant's name
        restaurantName.setText(marker.getTitle().toUpperCase(Locale.ROOT));
        // Setting the restaurant's full address
        restaurantAddress.setText(marker.getSnippet());
        // Setting the restaurant's id
        restaurantId.setText(marker.getTag().toString());

        // Returning the view containing InfoWindow contents
        return view;

    }
}

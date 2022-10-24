package com.benlinux.go4lunch.modules;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

public class FormatAddressModule {

    // Return address according to Latitude & longitude params
    public static String getFormattedAddressFromLatLng(LatLng latLng, Context context) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        String strAdd;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            Address returnedAddress = addresses.get(0);
            StringBuilder strReturnedAddress = new StringBuilder();

            String mStreetNumber = returnedAddress.getSubThoroughfare();
            String mStreet = returnedAddress.getThoroughfare();
            String mPostalCode = returnedAddress.getPostalCode();
            String mCity = returnedAddress.getLocality();

            // If no street, display only postal code & city
            if (mStreet == null ) {
                strReturnedAddress.append(mPostalCode).append(" ").append(mCity);
            // Else, display full formatted address
            } else {
                strReturnedAddress.append(mStreetNumber).append(" ").append(mStreet).append(" - ").append(mPostalCode).append(" ").append(mCity);
            }
            strAdd = strReturnedAddress.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return strAdd;
    }
}

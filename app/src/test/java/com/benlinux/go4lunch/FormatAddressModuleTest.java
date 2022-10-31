package com.benlinux.go4lunch;

import static org.junit.Assert.assertEquals;

import android.location.Geocoder;

import com.benlinux.go4lunch.activities.MainActivity;
import com.benlinux.go4lunch.modules.FormatAddressModule;
import com.benlinux.go4lunch.modules.FormatRatingModule;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import org.mockito.Mockito;


public class FormatAddressModuleTest {

    MainActivity mainActivity = Mockito.mock(MainActivity.class);


    @Test
    public void shouldReturnFormattedAddress() {
        final LatLng latLng = new LatLng(48.868829858, 2.309832094);
        final String expectedAddress = "";

        String result = FormatAddressModule.getFormattedAddressFromLatLng(latLng, mainActivity.getApplicationContext());
        assertEquals(expectedAddress, result);
    }
}

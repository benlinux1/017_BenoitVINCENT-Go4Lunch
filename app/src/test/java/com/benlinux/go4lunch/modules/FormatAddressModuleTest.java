package com.benlinux.go4lunch.modules;

import static org.junit.Assert.assertEquals;

import com.benlinux.go4lunch.activities.MainActivity;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;


public class FormatAddressModuleTest {

    MainActivity mainActivity = Mockito.mock(MainActivity.class);


    @Test
    public void shouldReturnFormattedAddress() {
        final LatLng latLng = new LatLng(48.868829858, 2.309832094);
        final String expectedAddress = "my expected address";

        try (MockedStatic<FormatAddressModule> mockedStatic = Mockito.mockStatic(FormatAddressModule.class)) {
            mockedStatic
                .when(() -> FormatAddressModule.getFormattedAddressFromLatLng(latLng, mainActivity.getApplicationContext()))
                .thenReturn(expectedAddress);

            String result = FormatAddressModule.getFormattedAddressFromLatLng(latLng, mainActivity.getApplicationContext());
            assertEquals(expectedAddress, result);
        }
    }
}

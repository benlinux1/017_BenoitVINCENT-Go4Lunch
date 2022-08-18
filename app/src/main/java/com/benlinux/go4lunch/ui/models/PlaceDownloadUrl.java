package com.benlinux.go4lunch.ui.models;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PlaceDownloadUrl {

    public String downloadUrl(String url) throws IOException {
        // Initialize variables
        String urlData = "";
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try {
            // Initialize connection
            URL getUrl = new URL(url);
            httpURLConnection = (HttpURLConnection) getUrl.openConnection();
            httpURLConnection.connect();

            // Initialize input stream
            inputStream = httpURLConnection.getInputStream();
            // Initialize Buffer reader
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            // Initialize string builder
            StringBuilder builder = new StringBuilder();
            // Initialize string variable
            String line = "";
            // Use WHILE loop to close string
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            // Get append data
            urlData = builder.toString();
            // Close reader
            reader.close();
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        } finally {
            inputStream.close();
            httpURLConnection.disconnect();
        }
        // Return data
        return urlData;
    }
}

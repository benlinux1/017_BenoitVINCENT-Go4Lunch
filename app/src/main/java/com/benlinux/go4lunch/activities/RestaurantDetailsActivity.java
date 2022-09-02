package com.benlinux.go4lunch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.benlinux.go4lunch.databinding.ActivityRestaurantDetailsBinding;

public class RestaurantDetailsActivity extends AppCompatActivity {

    private ActivityRestaurantDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);
        getRestaurantIdFromIntent();

    }

    private String getRestaurantIdFromIntent() {
        Intent intent = getIntent();
        String restaurantID = intent.getStringExtra("PLACE_ID");
        return restaurantID;
    }
}

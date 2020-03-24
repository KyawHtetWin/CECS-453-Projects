// CECS 453 Mobile Development
// Homework 2
// Due date: Feb 23, 2020

// Team members:
// Ben Do
// Kyaw Htet Win


package com.example.homework2;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.annotation.Nullable;

public class CarDetailsActivity extends AppCompatActivity {
    public static final String ARG_VEHICLE_LISTING = "vehicle_listing";

    // Creates the CarDetailFragment to show the appropriate detail after
    // retrieving the vehicle listing that the user has selected.
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_details);

        CarDetailFragment carDetailFragment = (CarDetailFragment)
                getSupportFragmentManager().findFragmentById(R.id.car_detail_frag);

        Vehicle.Listing vehicleList = (Vehicle.Listing)
                getIntent().getSerializableExtra(ARG_VEHICLE_LISTING);

        carDetailFragment.setmSelectedListing(vehicleList);


    }
}

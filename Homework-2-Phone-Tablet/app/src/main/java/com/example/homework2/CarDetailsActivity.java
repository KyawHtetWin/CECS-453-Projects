package com.example.homework2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CarDetailsActivity extends AppCompatActivity {
    public static final String ARG_VEHICLE_LISTING = "vehicle_listing";

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

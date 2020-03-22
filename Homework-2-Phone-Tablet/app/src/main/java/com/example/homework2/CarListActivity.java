package com.example.homework2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

public class CarListActivity extends AppCompatActivity implements CarListFragment.Listener{
    private static final String TAG = "DEBUG: Homework2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_car_list);
        String username = getIntent().getStringExtra("username");
        username = StringUtils.capitalize(username);
        String message = "Welcome, " + username + "!";
        Toast.makeText(CarListActivity.this, message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void carSelected(Vehicle.Listing vehicle_listing) {

        View fragmentContainer = findViewById(R.id.fragment_container);

        if(fragmentContainer != null) {
            CarDetailFragment carDetailFragment =  new CarDetailFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            carDetailFragment.setmSelectedListing(vehicle_listing);
            ft.replace(R.id.fragment_container, carDetailFragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(null);
            ft.commit();

        }

        else{
            Intent intent = new Intent(this, CarDetailsActivity.class);
            intent.putExtra(CarDetailsActivity.ARG_VEHICLE_LISTING, vehicle_listing);
            startActivity(intent);
        }

    }
}

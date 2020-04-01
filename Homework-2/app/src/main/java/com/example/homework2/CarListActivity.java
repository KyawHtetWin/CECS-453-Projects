// CECS 453 Mobile Development
// Homework 2
// Due date: Feb 23, 2020

// Team members:
// Ben Do
// Kyaw Htet Win

package com.example.homework2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

// host the car list fragment
public class CarListActivity extends AppCompatActivity implements CarListFragment.Listener{
    private static final String TAG = "DEBUG: Homework2";
    private static final String CAR_LIST_FRAGMENT = "CarListFragment";
    private FragmentTransaction ft;
    private CarListFragment carListFragment = null;

    // Display a welcome message to the user
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "CarListActivity onSaveInstanceState()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_list);
        Log.i(TAG, "CarListActivity onCreate()");
        // The CarListActivity is being created for the first time
        if (savedInstanceState == null) {
            Log.i(TAG, "savedInstanceState is null");
            // Shows the welcome message
            String username = getIntent().getStringExtra("username");
            username = StringUtils.capitalize(username);
            String message = "Welcome, " + username + "!";
            Toast.makeText(CarListActivity.this, message, Toast.LENGTH_SHORT).show();

            // Create the CarListFragment dynamically
            carListFragment = new CarListFragment();
            ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.car_list_container, carListFragment);
            ft.addToBackStack(null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }

        else if(savedInstanceState != null) {
            Log.i(TAG, "savedInstanceState is not null");
            carListFragment = (CarListFragment) getSupportFragmentManager().
                    getFragment(savedInstanceState, CAR_LIST_FRAGMENT);
            Log.i(TAG, "savedInstanceState is not null");
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, CAR_LIST_FRAGMENT, carListFragment);
        Log.i(TAG, "CarListActivity onSaveInstanceState()");
    }

    // This method is called whenever the user select the car. If on tablet, it creates the
    // CarDetailFragment, and replaces it's framelayout with it. If on phone, it start
    // CarDetailActivity passing it the vehicle listing that the user has selected.
    @Override
    public void carSelected(Vehicle.Listing vehicle_listing) {

        View fragmentContainer = findViewById(R.id.fragment_container);

        if(fragmentContainer != null) {
            CarDetailFragment carDetailFragment =  new CarDetailFragment();
            ft = getSupportFragmentManager().beginTransaction();

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

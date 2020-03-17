package com.example.homework2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements  CarListFragment.Listener{

    private static final String TAG = "DEBUG: Homework2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public void carSelected(Vehicle.Listing vehicle_listing) {

        //Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        View fragmentContainer = findViewById(R.id.fragment_container);
        /*
        if (fragment == null) {
            fragment = new CarListFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
         */
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

    /*
    public void replaceFragments(Vehicle.Listing vehicle_listing) {
        Fragment newFragment = CarDetailFragment.newInstance(vehicle_listing);
        fm.beginTransaction()
                .replace(R.id.fragment_container, newFragment)
                .addToBackStack(null)
                .commit();
    }
    *
     */


//    @Override
//    public void onBackPressed() {
//        if(fm.getBackStackEntryCount() == 0) {
//            super.onBackPressed();
//        }
//        else {
//            fm.popBackStack();
//            Log.i(TAG, "popBackStack() called");
//        }
//    }

}

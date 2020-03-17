package com.example.homework2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DEBUG: Homework2";
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = new CarListFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }


    public void replaceFragments(Vehicle.Listing vehicle_listing) {
        Fragment newFragment = CarDetailFragment.newInstance(vehicle_listing);
        fm.beginTransaction()
                .replace(R.id.fragment_container, newFragment)
                .addToBackStack(null)
                .commit();
    }


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

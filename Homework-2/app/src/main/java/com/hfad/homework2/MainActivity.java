package com.hfad.homework2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements CarListFragment.Listener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void itemClicked(long id) {

        View carDetailFrag = findViewById(R.id.car_detail_frag);

        // On Tablet
        if(carDetailFrag != null) {

            CarDetailFragment carDetails = new CarDetailFragment();

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            carDetails.setCarId(id);

            fragmentTransaction.replace(R.id.car_detail_frag, carDetails);

            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

            fragmentTransaction.addToBackStack(null);

            fragmentTransaction.commit();

        }

        // Only on phone
        else {
            Intent intent = new Intent(this, CarDetailActivity.class);

            intent.putExtra(CarDetailActivity.SELECTED_CAR_ID, (int)id);

            startActivity(intent);
        }
    }
}

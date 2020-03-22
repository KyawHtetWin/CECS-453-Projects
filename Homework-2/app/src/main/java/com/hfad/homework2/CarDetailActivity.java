package com.hfad.homework2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class CarDetailActivity extends AppCompatActivity {

    public static final String SELECTED_CAR_ID = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);

        CarDetailFragment carDetail = (CarDetailFragment)
                getSupportFragmentManager().findFragmentById(R.id.detail_frag);

        int carID= (int) getIntent().getExtras().get(SELECTED_CAR_ID);
        carDetail.setCarId(carID);
    }
}

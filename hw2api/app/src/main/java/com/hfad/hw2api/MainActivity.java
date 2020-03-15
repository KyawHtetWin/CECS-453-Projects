package com.hfad.hw2api;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;



public class MainActivity extends AppCompatActivity implements OnItemSelectedListener{


    Spinner carMakeSpinner;
    int carMakeSelectedID = -1;
    Spinner carModelSpinner;
    CarMakeAPIManager carMakeAPIManager = new CarMakeAPIManager(this);
    CarModelAPIManager carModelAPIManager = new CarModelAPIManager(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Make API Call to Car Makes
        carMakeAPIManager.execute();
        carMakeSpinner = findViewById(R.id.car_make);
        carModelSpinner = findViewById(R.id.car_model);

        carMakeAPIManager.setCarMakeSpinner(carMakeSpinner);
        carMakeSpinner.setOnItemSelectedListener(this);
        carModelSpinner.setOnItemSelectedListener(this);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


        switch (parent.getId()) {

            case R.id.car_make:

                Toast.makeText(parent.getContext(),
                        "Selected Car Make : " + parent.getItemAtPosition(position).toString(),
                        Toast.LENGTH_LONG).show();

                // Get Car Make ID based on user selection
                carMakeSelectedID = carMakeAPIManager.getCarMakeId(parent.getItemAtPosition(position).toString());
                Toast.makeText(parent.getContext(),
                        "Selected Car Make ID : " + carMakeSelectedID,
                        Toast.LENGTH_LONG).show();

                // Pass the ID to the car model and make API Call to get car model
                carModelAPIManager.setSelectedMakeId(carMakeSelectedID);
                carModelAPIManager.setCarModelSpinner(carModelSpinner);
                carModelAPIManager.execute();


                break;

            case R.id.car_model:
                Toast.makeText(parent.getContext(),
                        "Car Model Pressed : ",
                        Toast.LENGTH_LONG).show();

                break;

        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}









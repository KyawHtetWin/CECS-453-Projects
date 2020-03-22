package com.hfad.hw2api;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CarMakeAPIManager extends AsyncTask<Void, Void, Void> {

    private String TAG = com.hfad.hw2api.CarMakeAPIManager.class.getSimpleName();
    private String carMakesURL = "https://thawing-beach-68207.herokuapp.com/carmakes";





    // Stores the make id and the make
    HashMap<Integer, String> idVehicleMake;
    ArrayList<Integer> makeID = new ArrayList<>();
    ArrayList<String> vehicleMakes = new ArrayList<>();
    private Context context;
    private Spinner carMakeSpinner;

    public void setCarMakeSpinner(Spinner carMakeSpinner) { this.carMakeSpinner = carMakeSpinner;}

    public int getCarMakeId (String carMake) {

        // First get the index of the car make
        int makeIndex = vehicleMakes.indexOf(carMake);

        // Retrieves the make ID
        int carMakeID = makeID.get(makeIndex);

        return carMakeID;
    }

    // Getters
    public HashMap<Integer, String> getIdVehicleMake() { return idVehicleMake; }

    public CarMakeAPIManager(Context context) {

        idVehicleMake = new HashMap<>();
        this.context = context;
    }

    @Override
    protected void onPreExecute() { super.onPreExecute(); }

    @Override
    protected Void doInBackground(Void... arg0) {

        HttpHandler sh = new HttpHandler();
        String jsonStr = sh.makeServiceCall(carMakesURL);
        Log.e(TAG, "Car Make Response url: " + jsonStr);

        if (jsonStr != null) {
            try {

                JSONArray carMakes = new JSONArray(jsonStr);
                for (int i = 0; i < carMakes.length(); i++) {
                    idVehicleMake.put(carMakes.getJSONObject(i).getInt("id"),
                            carMakes.getJSONObject(i).getString("vehicle_make"));
                }
            }

            catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
                // This might gives error
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,
                                "Json parsing error: " + e.getMessage(),
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }

        }

        else {

            Log.e(TAG, "Couldn't get json from server.");
            // This might gives error
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context,
                            "Couldn't get json from server. Check LogCat for possible errors!",
                            Toast.LENGTH_LONG)
                            .show();
                }
            });
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {

        super.onPostExecute(result);
        Toast.makeText(context, "Car Make Finished Loading", Toast.LENGTH_LONG).show();

        for (Map.Entry idMake: idVehicleMake.entrySet()) {
            makeID.add((Integer) idMake.getKey());
            vehicleMakes.add((String) idMake.getValue());
        }


        // Make an Adapter to dynamically populate the carMakeSpinner
        ArrayAdapter<String> makeAdapter = new ArrayAdapter<>
                (context
                        , android.R.layout.simple_spinner_item, vehicleMakes);

        carMakeSpinner.setAdapter(makeAdapter);

    }

}
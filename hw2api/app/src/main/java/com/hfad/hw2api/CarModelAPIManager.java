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

public class CarModelAPIManager extends AsyncTask<Void, Void, Void> {

    private String TAG = CarModelAPIManager.class.getSimpleName();
    private String carModelsURL = "https://thawing-beach-68207.herokuapp.com/carmodelmakes";
    private Context context;

    private Spinner carModelSpinner;

    ArrayList<Integer> modelID = new ArrayList<>();
    ArrayList<String> vehicleModel = new ArrayList<>();

    public void setCarModelSpinner(Spinner carModelSpinner) {
        this.carModelSpinner = carModelSpinner;
    }

    public void setSelectedMakeId(int selectedMakeId) {
        this.selectedMakeId = selectedMakeId;
    }

    private int selectedMakeId;

    // Stores the model id and the model
    HashMap<Integer, String> idVehicleModel;

    ArrayList<Integer> carModelIDs = new ArrayList<>();
    ArrayList<String> availbleCarModels = new ArrayList<>();

    public CarModelAPIManager(Context context) {
        this.context = context;
        idVehicleModel = new HashMap<>();
    }




    @Override
    protected void onPreExecute() { super.onPreExecute(); }

    @Override
    protected Void doInBackground(Void... arg0) {

        HttpHandler sh = new HttpHandler();
        // First, parse for the car make
        carModelsURL += "/" + selectedMakeId;
        String jsonStr = sh.makeServiceCall(carModelsURL);

        Log.e(TAG, "Car Model Response url: " + jsonStr);

        if (jsonStr != null) {
            try {
                JSONArray carModels = new JSONArray(jsonStr);

                for (int i = 0; i < carModels.length(); i++) {

                    int carModelID = carModels.getJSONObject(i).getInt("id");
                    String carModel = carModels.getJSONObject(i).getString("model");

                    carModelIDs.add(carModelID);
                    availbleCarModels.add(carModel);

                    idVehicleModel.put(carModelID, carModel);
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
        Toast.makeText(
                (context),
                "Car Model Finished Loading",
                Toast.LENGTH_LONG)
                .show();

        for (Map.Entry idMake: idVehicleModel.entrySet()) {
            modelID.add((Integer) idMake.getKey());
            vehicleModel.add((String) idMake.getValue());
        }


        // Make an Adapter to dynamically populate the carMakeSpinner
        ArrayAdapter<String> makeAdapter = new ArrayAdapter<>
                (context
                        , android.R.layout.simple_spinner_item, vehicleModel);

        carModelSpinner.setAdapter(makeAdapter);



    }

}
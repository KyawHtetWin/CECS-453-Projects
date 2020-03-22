package com.hfad.homework2;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class VehicleAPIManager extends AsyncTask<Void, Void, Void> {

    private String TAG = CarModelAPIManager.class.getSimpleName();
    private String vehicleURL = "https://thawing-beach-68207.herokuapp.com/cars";
    private Context context;
    private int selectedMakeId;
    private int selectedModelId;
    private static final String zipCode = "92603";

    // Stores the vehicle id and information on lastupdated, image_url, price, and vehicle_descirption
    HashMap<Integer, ArrayList<String>> vehicle;

    // Getters
    public HashMap<Integer, ArrayList<String>> getVehicle() { return vehicle; }

    public VehicleAPIManager(Context context, int selectedMakeId, int selectedModelId) {

        this.context = context;
        vehicle = new HashMap<>();
        this.selectedMakeId = selectedMakeId;
        this.selectedModelId = selectedModelId;
    }

    @Override
    protected void onPreExecute() { super.onPreExecute(); }

    @Override
    protected Void doInBackground(Void... arg0) {

        HttpHandler sh = new HttpHandler();
        vehicleURL += "/" + selectedMakeId + "/" + selectedModelId + "/" + zipCode;
        String jsonStr = sh.makeServiceCall(vehicleURL);

        Log.e(TAG, "Car Make Response url: " + jsonStr);

        int vehicleId;
        String created;
        String imageURL;
        String price;
        String vehDescription;

        ArrayList<String> vehicleInfo= new ArrayList<>();

        if (jsonStr != null) {
            try {

                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray carModels = jsonObj.getJSONArray("lists");

                for (int i = 0; i < carModels.length(); i++) {

                    // Get all the data
                    vehicleId = carModels.getJSONObject(i).getInt("id");
                    created = carModels.getJSONObject(i).getString("created_at");
                    imageURL = carModels.getJSONObject(i).getString("image_url");
                    price = Integer.toString(carModels.getJSONObject(i).getInt("price"));
                    vehDescription = carModels.getJSONObject(i).getString("veh_description");

                    // Add to the ArrayList
                    vehicleInfo.add(created);
                    vehicleInfo.add(imageURL);
                    vehicleInfo.add(price);
                    vehicleInfo.add(vehDescription);

                    // Put into HashMap
                    vehicle.put(vehicleId,vehicleInfo);
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
    protected void onPostExecute(Void result) { super.onPostExecute(result); }
}



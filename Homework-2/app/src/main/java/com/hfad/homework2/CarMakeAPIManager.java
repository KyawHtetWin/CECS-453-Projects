package com.hfad.homework2;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class CarMakeAPIManager extends AsyncTask<Void, Void, Void> {

    private String TAG = CarModelAPIManager.class.getSimpleName();
    private String carMakesURL = "https://thawing-beach-68207.herokuapp.com/carmakes";
    private Context context;
    // Stores the make id and the make
    HashMap<Integer, String> idVehicleMake;

    // Getters
    public HashMap<Integer, String> getIdVehicleMake() { return idVehicleMake; }

    public CarMakeAPIManager(Context context) {
        this.context = context;
        idVehicleMake = new HashMap<>();
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

                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray carMakes = jsonObj.getJSONArray("");

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
    protected void onPostExecute(Void result) { super.onPostExecute(result); }
}



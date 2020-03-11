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

public class CarModelAPIManager extends AsyncTask<Void, Void, Void> {

    private String TAG = CarModelAPIManager.class.getSimpleName();
    private String carModelsURL = "https://thawing-beach-68207.herokuapp.com/carmodelmakes";
    private Context context;
    private int selectedMakeId;
    // Stores the model id and the model
    HashMap<Integer, String> idVehicleModel;

    public HashMap<Integer, String> getIdVehicleModel() { return idVehicleModel; }

    public CarModelAPIManager(Context context, int selectedMakeId) {
        this.context = context;
        this.selectedMakeId = selectedMakeId;
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

        Log.e(TAG, "Car Make Response url: " + jsonStr);

        if (jsonStr != null) {
            try {

                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray carModels = jsonObj.getJSONArray("");

                for (int i = 0; i < carModels.length(); i++) {
                    idVehicleModel.put(carModels.getJSONObject(i).getInt("id")
                            , carModels.getJSONObject(i).getString("model"));
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



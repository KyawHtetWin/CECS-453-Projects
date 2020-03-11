package com.hfad.homework2;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarListFragment extends Fragment {

    private ListView carNamesList;
    private Spinner carMakeSpinner;
    private Spinner carModelSpinner;

    static interface Listener {
      void itemClicked(long id);
    };

    private Listener listener;


    public CarListFragment() {

        carNamesList = null;
        carMakeSpinner = null;
        carModelSpinner = null;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.listener = (Listener) context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_car_list, container, false);

        // Make API Call to Car Makes
        CarMakeAPIManager carMakeAPI = new CarMakeAPIManager(view.getContext());
        carMakeAPI.execute();

        HashMap<Integer, String> idVehicleMake = carMakeAPI.getIdVehicleMake();
        ArrayList<Integer> makeID = new ArrayList<>();
        ArrayList<String> vehicleMakes = new ArrayList<>();

        for (Map.Entry idMake: idVehicleMake.entrySet()) {
            makeID.add((Integer) idMake.getKey());
            vehicleMakes.add((String) idMake.getValue());
        }

        // Make an Adapter to dynamically populate the carMakeSpinner
        ArrayAdapter<String> makeAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, vehicleMakes);
        carMakeSpinner = (Spinner) view.findViewById(R.id.car_make);
        carMakeSpinner.setAdapter(makeAdapter);

        // Returns what the user selected
        String selectedMake = String.valueOf(carMakeSpinner.getSelectedItem());
        // Get the ID of the selected make
        int selectedMakeId = makeID.get(vehicleMakes.indexOf(selectedMake));

        // Make API Call to Car Models based on Car Make Id
        CarModelAPIManager carModeAPI = new CarModelAPIManager(view.getContext(), selectedMakeId);
        carModeAPI.execute();

        HashMap<Integer, String> idVehicleModel = carMakeAPI.getIdVehicleMake();
        ArrayList<Integer> modelID = new ArrayList<>();
        ArrayList<String> vehicleModels = new ArrayList<>();

        for (Map.Entry idModel: idVehicleModel.entrySet()) {
            modelID.add((Integer) idModel.getKey());
            vehicleModels.add((String) idModel.getValue());
        }

        // Make an Adapter to dynamically populate the carModelSpinner
        ArrayAdapter<String> modelAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, vehicleModels);
        carModelSpinner = (Spinner) view.findViewById(R.id.car_model);
        carModelSpinner.setAdapter(modelAdapter);

        String selectedModel = String.valueOf(carModelSpinner.getSelectedItem());
        // Get the ID of the selected model
        int selectedModelId = modelID.get(vehicleModels.indexOf(selectedModel));

        // Make API Call to Vehciel based on selected make and model
        VehicleAPIManager vehicleAPI = new VehicleAPIManager(view.getContext(), selectedMakeId, selectedModelId);
        vehicleAPI.execute();

        HashMap<Integer, ArrayList<String>> vehicle = vehicleAPI.getVehicle();
        ArrayList<Integer> availableVehiclesId = new ArrayList<>();

        for (Map.Entry car: vehicle.entrySet())
            availableVehiclesId.add((Integer) car.getKey());

        // Get the list view
        carNamesList = (ListView) view.findViewById(R.id.car_name);


        // Populate the ListView with the available cars by using the ArrayAdapter
        ArrayAdapter<Integer> arrayAdapter = new ArrayAdapter<>(view.getContext(),
                android.R.layout.simple_list_item_1, availableVehiclesId);

        // Set the adapter to the list view
        carNamesList.setAdapter(arrayAdapter);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (listener != null)
                    listener.itemClicked(id);
            }
        };

        // Add the listener to the list view
        carNamesList.setOnItemClickListener(itemClickListener);

        return view;
    }

}

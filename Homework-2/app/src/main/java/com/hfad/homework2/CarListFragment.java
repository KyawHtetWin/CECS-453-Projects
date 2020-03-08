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
import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 */
public class CarListFragment extends Fragment {

    private ListView carNames;
    private Spinner spinner;

    static interface Listener {
      void itemClicked(long id);
    };

    private Listener listener;


    public CarListFragment() {
        // Required empty public constructor
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.listener = (Listener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_car_list, container, false);

        // Get Spinner selected for makes and model
        spinner = (Spinner) view.findViewById(R.id.car_make);
        String selectedMake = String.valueOf(spinner.getSelectedItem());

        // Get Spinner selected for model
        spinner = (Spinner) view.findViewById(R.id.car_model);
        String selectedModel = String.valueOf(spinner.getSelectedItem());

        // Get the list view
        carNames = (ListView) view.findViewById(R.id.car_name);


        List<String> availableCars = new ArrayList<>();

        // Get all the cars available for that model
        for(int i=0; i < Car.cars.length; i++) {

            if(Car.cars[i].getModel().equals(selectedModel) && Car.cars[i].getMake().equals(selectedMake))
                availableCars.add(Car.cars[i].getName());
        }

        // Populate the ListView with the available cars by using the ArrayAdapter
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, availableCars);

        // Set the adapter to the list view
        carNames.setAdapter(arrayAdapter);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (listener != null)
                    listener.itemClicked(id);
            }
        };

        // Add the listener to the list view
        carNames.setOnItemClickListener(itemClickListener);

        return view;
    }






}

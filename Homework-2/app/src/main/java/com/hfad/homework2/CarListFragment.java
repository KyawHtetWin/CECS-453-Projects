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



        return view;
    }

}

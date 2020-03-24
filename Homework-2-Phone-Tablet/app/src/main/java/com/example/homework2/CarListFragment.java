package com.example.homework2;

import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class CarListFragment extends Fragment {

    private RecyclerView mCarRecyclerView;
    private VehicleAdapter mCarAdapter;
    private Spinner mCarMakeSpinner;
    private Spinner mCarModelSpinner;
    private Retrofit mRetroFit;
    private ApiManager mApiManager;

    private HashMap<String, Integer> mVehicleMakes = null;
    private HashMap<String, Integer> mVehicleModels = null;
    private ArrayList<Vehicle.Listing> mVehicleListings = null;

    private static final String baseURL = "https://thawing-beach-68207.herokuapp.com/";
    private static final String TAG = "DEBUG: Homework2";

    // This is the ArrayAdapter to carMakeSpinner
    private ArrayAdapter<String> carMakeAdapter = null;
    // This is the Array List that will store all the car makes
    private List<String> carMakes = null;

    // This is the ArrayAdapter to carModelSpinner
    private ArrayAdapter<String> carModelAdapter = null;
    // This the ArrayList that will store all the car models
    private List<String> carModels = null;

    boolean isFinished = false;

    // Interface that is used to communicate with CarDetailFragment
    interface Listener{
      void carSelected(Vehicle.Listing vehicle_listing); };

    private Listener listener;

    // This method saved the relevant data so that the app data is preserved on the screen rotation
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("car_makes", (Serializable) carMakes);
        outState.putSerializable("car_models", (Serializable) carModels);
        outState.putSerializable("vehicle_listing", mVehicleListings);
    }

    // Simple initialization of the objects
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRetroFit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mApiManager = mRetroFit.create(ApiManager.class);

        mVehicleMakes = new HashMap<>();
        mVehicleModels = new HashMap<>();
        mVehicleListings = new ArrayList<>();
        /*
        if(savedInstanceState != null) {
            carMakes = (List<String>) savedInstanceState.getSerializable("car_makes");
            carModels = (List<String>) savedInstanceState.getSerializable("car_models");

            //mVehicleModels = (HashMap<String, Integer>) savedInstanceState.getSerializable("vehcile_model");
            mVehicleListings = (ArrayList<Vehicle.Listing>) savedInstanceState.getSerializable("vehcile_listing");
            Toast.makeText(getContext(), "SavedInstanceState", Toast.LENGTH_SHORT).show();
        }

        else {

            mVehicleListings = new ArrayList<>();
        }

         */
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_car_list, container, false);

        // Initialize the RecyclerView and sets the LinearLayout for it
        mCarRecyclerView = (RecyclerView) v.findViewById(R.id.car_recycler_view);
        mCarRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mCarMakeSpinner = (Spinner) v.findViewById(R.id.car_make_spinner);
        mCarModelSpinner = (Spinner) v.findViewById(R.id.car_model_spinner);


        // API Call to get car makes
        getMakes();

        // set listener for makes spinner
        mCarMakeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                String currentMake = mCarMakeSpinner.getSelectedItem().toString();
                int currentMakeId = mVehicleMakes.get(currentMake);
                // Once Car Make is selected, make API Call to get car models
                getModels(currentMakeId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // set listener for models spinner
        mCarModelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String currentMake = mCarMakeSpinner.getSelectedItem().toString();
                int currentMakeId = mVehicleMakes.get(currentMake);

                String currentModel = mCarModelSpinner.getSelectedItem().toString();
                int currentModelId = mVehicleModels.get(currentModel);

                getListings(currentMakeId, currentModelId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return v;
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.listener = (Listener) context;
    }


    public void updateUI() {
        mCarAdapter = new VehicleAdapter();
        mCarRecyclerView.setAdapter(mCarAdapter);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (mVehicleListings.size() > 0) {
            CarDetailFragment carDetailFragment =  new CarDetailFragment();
            carDetailFragment.setmSelectedListing(mVehicleListings.get(0));
            ft.replace(R.id.fragment_container, carDetailFragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(null);
            ft.commit();
        } else {
            Fragment fragment = fm.findFragmentById(R.id.fragment_container);
            ft.remove(fragment).commit();
        }
    }

    // get vehicle makes from REST api and populate the makes spinner
    public void getMakes() {

        //if (carMakes == null) {
            Call<List<Vehicle.Make>> call = mApiManager.getMakes();
            call.enqueue(new Callback<List<Vehicle.Make>>() {
                @Override
                public void onResponse(Call<List<Vehicle.Make>> call, Response<List<Vehicle.Make>> response) {

                    if (!response.isSuccessful()) {
                        return;
                    }

                    List<Vehicle.Make> results = response.body();
                    carMakes = new ArrayList<>(); // list of makes for spinner

                    for (Vehicle.Make make : results) {
                        carMakes.add(make.getVehicle_make());
                        mVehicleMakes.put(make.getVehicle_make(), make.getId());
                    }

                    // setting up makes spinner
                    carMakeAdapter = new ArrayAdapter<>(getActivity(), R.layout.standard_spinner_format, carMakes);
                    carMakeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mCarMakeSpinner.setAdapter(carMakeAdapter);

                    // set up for getModels() call
                    String currentMake = mCarMakeSpinner.getSelectedItem().toString();
                    int currentMakeId = mVehicleMakes.get(currentMake);
                    getModels(currentMakeId);

                }

                @Override
                public void onFailure(Call<List<Vehicle.Make>> call, Throwable t) {
                    Log.i(TAG, "onFailure() in getMakes()");
                    isFinished = true;

                }

            });
        //}

        /*
        else {
            carMakeAdapter = new ArrayAdapter<>(getActivity(), R.layout.standard_spinner_format, carMakes);
            carMakeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mCarMakeSpinner.setAdapter(carMakeAdapter);
        }
        */
    }

    // get vehicle models from REST api based on currently selected make, and populate the models spinner
    public void getModels(int currentMakeId) {

        //if(carModels == null) {
            Call<List<Vehicle.Model>> call = mApiManager.getModels(currentMakeId);
            call.enqueue(new Callback<List<Vehicle.Model>>() {
                @Override
                public void onResponse(Call<List<Vehicle.Model>> call, Response<List<Vehicle.Model>> response) {

                    if (!response.isSuccessful()) {
                        Log.i(TAG, "Response not successful in getModels()!");
                        return;
                    }

                    List<Vehicle.Model> results = response.body();
                    carModels = new ArrayList<>(); // list of models for spinner

                    for (Vehicle.Model model : results) {
                        carModels.add(model.getModel());
                        mVehicleModels.put(model.getModel(), model.getId());
                    }

                    // setting up models spinner
                    carModelAdapter = new ArrayAdapter<>(getActivity(), R.layout.standard_spinner_format, carModels);
                    carModelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mCarModelSpinner.setAdapter(carModelAdapter);

                    // set up for getListings() call
                    String currentMake = mCarMakeSpinner.getSelectedItem().toString();
                    int currentMakeId = mVehicleMakes.get(currentMake);
                    String currentModel = mCarModelSpinner.getSelectedItem().toString();
                    int currentModelId = mVehicleModels.get(currentModel);
                    getListings(currentMakeId, currentModelId);

                }

                @Override
                public void onFailure(Call<List<Vehicle.Model>> call, Throwable t) {
                    Log.i(TAG, "onFailure() in getModels()");
                }

            });
        //}

        /*
        else{
            // setting up models spinner
            carModelAdapter = new ArrayAdapter<>(getActivity(), R.layout.standard_spinner_format, carModels);
            carModelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mCarModelSpinner.setAdapter(carModelAdapter);
        }

         */


    }

    // get listings of currently selected vehicle
    public void getListings(int currentMakeId, int currentModelId) {

        int zipCode = 92603; // irvine, ca

        //if(mVehicleListings.isEmpty()) {
            Call<Vehicle.ListingResponse> call = mApiManager.getListings(currentMakeId, currentModelId, zipCode);
            call.enqueue(new Callback<Vehicle.ListingResponse>() {
                @Override
                public void onResponse(Call<Vehicle.ListingResponse> call, Response<Vehicle.ListingResponse> response) {


                    if (!response.isSuccessful()) {
                        Log.i(TAG, "Response not successful in getListings()!");
                        return;
                    }

                    Vehicle.ListingResponse listResponse = response.body();

                    mVehicleListings = listResponse.getListings();

                    if (!mVehicleListings.isEmpty()) {
                        removeDuplicateListings();
                    }

                    updateUI();
                }

                @Override
                public void onFailure(Call<Vehicle.ListingResponse> call, Throwable t) {
                    Log.i(TAG, "onFailure() in getListings()" + t.toString());
                }

            });

        //}

        /*
        else {
            updateUI();
        }

         */


    }

    // remove duplicate vehicle listings based on vin number
    public void removeDuplicateListings() {

        for (int i = 0; i < mVehicleListings.size(); i++) {
            String vin = mVehicleListings.get(i).getVin_number();

            for (int j = i+1; j < mVehicleListings.size(); j++) {

                if (mVehicleListings.get(j).getVin_number().equals(vin)) {
                    mVehicleListings.remove(j);
                    j--;
                    Log.i(TAG, "Removed " + mVehicleListings.get(i).getModel() + " Vin: " + vin);
                }
            }
        }

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    // view holder for the RecyclerView adapter
    private class VehicleHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mMakeModelTextView;
        private TextView mDateTextView;
        private TextView mPriceTextView;
        private TextView mLocationTextView;
        private ImageView mThumbnailImageView;

        // Specifies what view that VehicleHolder should use & set the view with their related view id
        public VehicleHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.single_vehicle_preview, parent, false));
            itemView.setOnClickListener(this);

            mMakeModelTextView = (TextView) itemView.findViewById(R.id.make_model_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.date_text_view);
            mPriceTextView = (TextView) itemView.findViewById(R.id.price_text_view);
            mLocationTextView = (TextView) itemView.findViewById(R.id.locationTextView);
            mThumbnailImageView = (ImageView) itemView.findViewById(R.id.vehicle_thumbnail_image_view);
        }

        // Bind the relevant information about the vehicle to display in each view holder
        public void bind(String year, String make, String model, double price, String date, String location, String imageURL) {

            mMakeModelTextView.setText(year + " " + make + " " + model);
            mDateTextView.setText(date);
            mLocationTextView.setText(location);

            NumberFormat dollar = NumberFormat.getCurrencyInstance();
            mPriceTextView.setText(dollar.format(price));

            if (imageURL.isEmpty()) {
                Picasso.get().load(R.drawable.image_coming_soon).into(mThumbnailImageView);
            } else {
                Picasso.get()
                        .load(imageURL)
                        .error(R.drawable.image_coming_soon)      // Image to load when something goes wrong
                        .into(mThumbnailImageView);
            }
        }

        // Whenever the user clicks on one of the view holder, the relevant details of the
        // vehicle based on their click is passed through the listener interface
        @Override
        public void onClick(View view) {
            int listingPosition = mCarRecyclerView.getChildLayoutPosition(view);
            Vehicle.Listing listing = mVehicleListings.get(listingPosition);

            if(listener != null)
                listener.carSelected(listing);
        }


    }

    // adapter for RecyclerView
    private class VehicleAdapter extends RecyclerView.Adapter<VehicleHolder> {

        // Tell the adapter how to construct the ViewHolder
        @Override
        public VehicleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new VehicleHolder(layoutInflater, parent);
        }

        // This tells the RecyclerView when it wants to use (or reuse)
        // a view holder for a new piece of data
        @Override
        public void onBindViewHolder(VehicleHolder holder, int position) {
            Vehicle.Listing listing = mVehicleListings.get(position);

            holder.bind(Vehicle.getYear(listing.getVeh_description()), listing.getVehicle_make(),
                    listing.getModel(), listing.getPrice(), listing.getDate().substring(5,16),
                   Vehicle.getLocation(listing.getVeh_description()), listing.getImage_url());
        }

        // Tells the adapter how many data items there are
        @Override
        public int getItemCount() {
            return mVehicleListings.size();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

}

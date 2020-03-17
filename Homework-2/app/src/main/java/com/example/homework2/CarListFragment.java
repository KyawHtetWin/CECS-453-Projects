package com.example.homework2;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

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

    private HashMap<String, Integer> mVehicleMakes;
    private HashMap<String, Integer> mVehicleModels;
    private ArrayList<Vehicle.Listing> mVehicleListings;

    private static final String baseURL = "https://thawing-beach-68207.herokuapp.com/";
    private static final String TAG = "DEBUG: Homework2";


    boolean isFinished = false;


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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_car_list, container, false);

        mCarRecyclerView = (RecyclerView) v.findViewById(R.id.car_recycler_view);
        mCarRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mCarMakeSpinner = (Spinner) v.findViewById(R.id.car_make_spinner);
        mCarModelSpinner = (Spinner) v.findViewById(R.id.car_model_spinner);


        getMakes();



        // set listener for makes spinner
        mCarMakeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                String currentMake = mCarMakeSpinner.getSelectedItem().toString();
                int currentMakeId = mVehicleMakes.get(currentMake);
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

    public void updateUI() {
        mCarAdapter = new VehicleAdapter();
        mCarRecyclerView.setAdapter(mCarAdapter);
    }




    // get vehicle makes from REST api and populate the makes spinner
    public void getMakes() {

        Call<List<Vehicle.Make>> call = mApiManager.getMakes();
        call.enqueue(new Callback<List<Vehicle.Make>>() {
            @Override
            public void onResponse(Call<List<Vehicle.Make>> call, Response<List<Vehicle.Make>> response) {

                if (!response.isSuccessful()) {
                    return;
                }

                List<Vehicle.Make> results = response.body();
                List<String> carMakes = new ArrayList<>(); // list of makes for spinner

                for (Vehicle.Make make : results) {
                    carMakes.add(make.getVehicle_make());
                    mVehicleMakes.put(make.getVehicle_make(), make.getId());
                }

                // setting up makes spinner
                ArrayAdapter<String> carMakeAdapter = new ArrayAdapter<>(getActivity(), R.layout.standard_spinner_format, carMakes);
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








    }

    // get vehicle models from REST api based on currently selected make, and populate the models spinner
    public void getModels(int currentMakeId) {

        Call<List<Vehicle.Model>> call = mApiManager.getModels(currentMakeId);
        call.enqueue(new Callback<List<Vehicle.Model>>() {
            @Override
            public void onResponse(Call<List<Vehicle.Model>> call, Response<List<Vehicle.Model>> response) {

                if (!response.isSuccessful()) {
                    Log.i(TAG, "Response not successful in getModels()!");
                    return;
                }

                List<Vehicle.Model> results = response.body();
                List<String> carModels = new ArrayList<>(); // list of models for spinner

                for (Vehicle.Model model : results) {
                    carModels.add(model.getModel());
                    mVehicleModels.put(model.getModel(), model.getId());
                }

                // setting up models spinner
                ArrayAdapter<String> carModelAdapter = new ArrayAdapter<>(getActivity(), R.layout.standard_spinner_format, carModels);
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

    }

    // get listings of currently selected vehicle
    public void getListings(int currentMakeId, int currentModelId) {

        int zipCode = 92603; // irvine, ca

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
        private ImageView mThumbnailImageView;

        public VehicleHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.single_vehicle_preview, parent, false));
            itemView.setOnClickListener(this);

            mMakeModelTextView = (TextView) itemView.findViewById(R.id.make_model_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.date_text_view);
            mPriceTextView = (TextView) itemView.findViewById(R.id.price_text_view);
            mThumbnailImageView = (ImageView) itemView.findViewById(R.id.vehicle_thumbnail_image_view);
        }

        public void bind(String make, String model, double price, String date, String imageURL) {

            mMakeModelTextView.setText(make + " " + model);
            mDateTextView.setText(date);

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

        @Override
        public void onClick(View view) {
            int listingPosition = mCarRecyclerView.getChildLayoutPosition(view);
            Vehicle.Listing listing = mVehicleListings.get(listingPosition);

            ((MainActivity) getActivity()).replaceFragments(listing);
        }


    }

    // adapter for RecyclerView
    private class VehicleAdapter extends RecyclerView.Adapter<VehicleHolder> {


        @Override
        public VehicleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new VehicleHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(VehicleHolder holder, int position) {
            Vehicle.Listing listing = mVehicleListings.get(position);
            holder.bind(listing.getVehicle_make(), listing.getModel(), listing.getPrice(), listing.getDate().substring(5,16), listing.getImage_url());
        }

        @Override
        public int getItemCount() {
            return mVehicleListings.size();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////





}

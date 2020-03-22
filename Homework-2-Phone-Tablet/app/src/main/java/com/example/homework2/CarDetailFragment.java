package com.example.homework2;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CarDetailFragment extends Fragment {


    private TextView mMakeModelTextView;
    private TextView mPriceTextView;
    private TextView mVehicleDescriptionTextView;
    private TextView mDateTextView;
    private TextView mLocationTextView;
    private ImageView mVehicleImageView;

    private Vehicle.Listing mSelectedListing;

    /*
    public static CarDetailFragment newInstance(Vehicle.Listing vehicleListing) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_VEHICLE_LISTING, vehicleListing);
        CarDetailFragment fragment = new CarDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

     */

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("vehicle_listing", mSelectedListing);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            mSelectedListing = (Vehicle.Listing) savedInstanceState.getSerializable("vehicle_listing");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_car_detail, container, false);
        return v;

    }

    @Override
    public void onStart() {
        super.onStart();

        View view = getView();
        if (view != null) {
            mMakeModelTextView = (TextView) view.findViewById(R.id.make_model_text_view);
            mPriceTextView = (TextView) view.findViewById(R.id.price_text_view);
            mVehicleDescriptionTextView = (TextView) view.findViewById(R.id.vehicle_description_text_view);
            mDateTextView = (TextView) view.findViewById(R.id.date_text_view);
            mLocationTextView = (TextView) view.findViewById(R.id.locationTextView);
            mVehicleImageView = (ImageView) view.findViewById(R.id.vehicle_image_view);

            String year = Vehicle.getYear(mSelectedListing.getVeh_description());
            String location = Vehicle.getLocation(mSelectedListing.getVeh_description());
            String vehicleDescription = mSelectedListing.getVeh_description()
                    .replace("Location: " + location + ". ", "");

            mMakeModelTextView.setText(year + " " + mSelectedListing.getVehicle_make() + " " + mSelectedListing.getModel());
            mLocationTextView.setText(location);

            NumberFormat dollar = NumberFormat.getCurrencyInstance();
            mPriceTextView.setText(dollar.format(mSelectedListing.getPrice()));

            mVehicleDescriptionTextView.setText(vehicleDescription);
            mDateTextView.setText(mSelectedListing.getDate().substring(5, 16));


            if (mSelectedListing.getImage_url().isEmpty()) {
                Picasso.get().load(R.drawable.image_coming_soon).into(mVehicleImageView);
            } else {
                Picasso.get()
                        .load(mSelectedListing.getImage_url())
                        .error(R.drawable.image_coming_soon)
                        .into(mVehicleImageView);
            }
        }
    }

    public void setmSelectedListing(Vehicle.Listing mSelectedListing) {
        this.mSelectedListing = mSelectedListing;
    }


}

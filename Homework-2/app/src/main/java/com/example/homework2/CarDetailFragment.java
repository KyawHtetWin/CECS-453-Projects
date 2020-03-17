package com.example.homework2;

import android.media.Image;
import android.os.Bundle;
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

public class CarDetailFragment extends Fragment {


    private TextView mMakeModelTextView;
    private TextView mPriceTextView;
    private TextView mVehicleDescriptionTextView;
    private TextView mDateTextView;
    private ImageView mVehicleImageView;

    private Vehicle.Listing mSelectedListing;

    private static final String ARG_VEHICLE_LISTING = "vehicle_listing";

    public static CarDetailFragment newInstance(Vehicle.Listing vehicleListing) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_VEHICLE_LISTING, vehicleListing);
        CarDetailFragment fragment = new CarDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSelectedListing = (Vehicle.Listing) getArguments().getSerializable(ARG_VEHICLE_LISTING);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_car_detail, container, false);

        mMakeModelTextView = (TextView) v.findViewById(R.id.make_model_text_view);
        mPriceTextView = (TextView) v.findViewById(R.id.price_text_view);
        mVehicleDescriptionTextView = (TextView) v.findViewById(R.id.vehicle_description_text_view);
        mDateTextView = (TextView) v.findViewById(R.id.date_text_view);
        mVehicleImageView = (ImageView) v.findViewById(R.id.vehicle_image_view);

        mMakeModelTextView.setText(mSelectedListing.getVehicle_make() + " " + mSelectedListing.getModel());

        NumberFormat dollar = NumberFormat.getCurrencyInstance();
        mPriceTextView.setText(dollar.format(mSelectedListing.getPrice()));

        mVehicleDescriptionTextView.setText(mSelectedListing.getVeh_description());
        mDateTextView.setText(mSelectedListing.getDate().substring(5,16));

        if (mSelectedListing.getImage_url().isEmpty()) {
            Picasso.get().load(R.drawable.image_coming_soon).into(mVehicleImageView);
        } else {
            Picasso.get()
                    .load(mSelectedListing.getImage_url())
                    .error(R.drawable.image_coming_soon)
                    .into(mVehicleImageView);
        }

        return v;

    }

}

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


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null)
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
        if(view != null) {
            mMakeModelTextView = (TextView) view.findViewById(R.id.make_model_text_view);
            mPriceTextView = (TextView) view.findViewById(R.id.price_text_view);
            mVehicleDescriptionTextView = (TextView) view.findViewById(R.id.vehicle_description_text_view);
            mDateTextView = (TextView) view.findViewById(R.id.date_text_view);
            mVehicleImageView = (ImageView) view.findViewById(R.id.vehicle_image_view);

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
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("vehicle_listing", mSelectedListing);
    }


    public void setmSelectedListing(Vehicle.Listing mSelectedListing) { this.mSelectedListing = mSelectedListing; }

}

package com.hfad.homework2;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class CarDetailFragment extends Fragment {

    private long carId;
    private TextView makeModel, cPrice, cLocation, cLastUpdated;
    private ImageView carPicture;

    public CarDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null)
            carId = savedInstanceState.getLong("carId");
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();

        if (view != null) {

            Car car = Car.cars[(int) carId];

            makeModel = (TextView) view.findViewById(R.id.make_model);
            makeModel.setText(car.getMake() + " " + car.getModel());

            cPrice = (TextView) view.findViewById(R.id.price);
            cPrice.setText("$" + car.getPrice());

            cLocation = (TextView) view.findViewById(R.id.location);
            cLocation.setText(car.getLocation());

            carPicture = (ImageView) view.findViewById(R.id.car_image);
            carPicture.setImageResource(car.getImageResourceId());

            cLastUpdated = (TextView) view.findViewById(R.id.last_updated);
            cLastUpdated.setText(car.getLastUpdated());
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_car_detail, container, false);
    }

    public void onSavedInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("cardId", carId);
    }

    public void setCarId(long id) {this.carId = id;}

}

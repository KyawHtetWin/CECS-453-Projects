package com.example.homework3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import im.delight.android.location.SimpleLocation;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "Homework3 Debug:";

    // keys for outstate Bundle
    private static final String LAT_KEY = "lat_key";
    private static final String LNG_KEY = "lng_key";
    private static final String ZOOM_KEY = "zoom_key";
    private static final String ADDRESSES_KEY = "addresses_key";
    private static final String MAPTYPE_KEY = "maptype_key";

    private Bundle bundle;
    private ArrayList<Address> mAddresses;

    private GoogleMap mMap;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    public static final float INITIAL_ZOOM = 15f;

    private RecyclerView mAddressRecyclerView;
    private AddressAdapter mAddressAdapter;
    private EditText mAddressEditText;
    private ImageView mAddLocationIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            bundle = savedInstanceState;
            mAddresses = bundle.getParcelableArrayList(ADDRESSES_KEY);
        } else {
            bundle = null;
            mAddresses = new ArrayList<>();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, mapFragment)
                .commit();

        mapFragment.getMapAsync(this);
        setupUI();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        double lat = mMap.getCameraPosition().target.latitude;
        double lng = mMap.getCameraPosition().target.longitude;
        float zoom = mMap.getCameraPosition().zoom;
        int mapType = mMap.getMapType();

        outState.putDouble(LAT_KEY, lat);
        outState.putDouble(LNG_KEY, lng);
        outState.putFloat(ZOOM_KEY, zoom);
        outState.putInt(MAPTYPE_KEY, mapType);
        outState.putParcelableArrayList(ADDRESSES_KEY, mAddresses);
    }

    // set up the views and listeners
    public void setupUI() {
        mAddressRecyclerView = (RecyclerView) findViewById(R.id.address_recycler_view);
        mAddressRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        // display a divider between each address in the RecyclerView
        mAddressRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        mAddressAdapter = new AddressAdapter(mAddresses);
        mAddressRecyclerView.setAdapter(mAddressAdapter);

        mAddressEditText = (EditText) findViewById(R.id.address_edit_text);

        mAddLocationIcon = (ImageView) findViewById(R.id.add_location_button);
        mAddLocationIcon.setOnClickListener(new View.OnClickListener() {
            // get the Address from the user input when pressed
            // if valid address, add Address to the RecyclerView, then add a marker on map
            @Override
            public void onClick(View v) {
                String inputAddress = mAddressEditText.getText().toString();

                if (!inputAddress.isEmpty()) {

                    Address address = getAddressFromName(MainActivity.this, inputAddress);
                    if (address != null) {
                        mAddresses.add(address);
                        mAddressAdapter.notifyDataSetChanged();

                        LatLng location = new LatLng(address.getLatitude(), address.getLongitude());
                        addMarker(location);

                        mAddressEditText.getText().clear();
                        mAddressEditText.clearFocus();

                        Toast.makeText(MainActivity.this, "Location added", Toast.LENGTH_SHORT).show();
                    } else {
                        mAddressEditText.setError("Address not found!");
                    }
                }
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation();
        setUpMap();
    }

    // set up initial map or restore map state
    public void setUpMap() {

        // set up initial map
        if (bundle == null) {
            LatLng currentLocation = getCurrentLocation();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation, INITIAL_ZOOM);
            mMap.moveCamera(cameraUpdate);

            // restore map state on orientation change
        } else {
            double lat = bundle.getDouble(LAT_KEY);
            double lng = bundle.getDouble(LNG_KEY);
            float zoom = bundle.getFloat(ZOOM_KEY);
            int mapType = bundle.getInt(MAPTYPE_KEY);

            mMap.setMapType(mapType);

            LatLng savedLocation = new LatLng(lat, lng);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(savedLocation, zoom);
            mMap.moveCamera(cameraUpdate);

            // re-draw markers
            for (Address address : mAddresses) {
                LatLng location = new LatLng(address.getLatitude(), address.getLongitude());
                addMarker(location);
            }
        }
    }

    // inflate map type menu options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_options, menu);
        return true;
    }

    // change map type upon selection
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Change the map type based on the user's selection.
        switch (item.getItemId()) {
            case R.id.normal_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.hybrid_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.satellite_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.terrain_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // request and set device location permission
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation();
                    break;
                }
        }
    }

    // View holder for the address RecyclerView
    private class AddressHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnLongClickListener {

        private TextView mAddressTextView;
        private ImageView mRemoveAddressIcon;

        public AddressHolder(LayoutInflater inflater, final ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_address, parent, false));

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            mAddressTextView = (TextView) itemView.findViewById(R.id.address_text_view);

            mRemoveAddressIcon = (ImageView) itemView.findViewById(R.id.delete_image_view);
            mRemoveAddressIcon.setOnClickListener(new View.OnClickListener() {
                // remove address from RecyclerView on icon pressed
                @Override
                public void onClick(View v) {
                    int addressPosition = mAddressRecyclerView.getChildLayoutPosition(itemView);
                    mAddresses.remove(addressPosition);
                    mAddressAdapter.notifyDataSetChanged();
                }
            });
        }

        // set address text in RecyclerView
        public void bind(Address address) {
            String addressStr = address.getAddressLine(0);
            mAddressTextView.setText(addressStr);
        }

        // zoom to address marker when pressed
        @Override
        public void onClick(View v) {

            int addressPosition = mAddressRecyclerView.getChildLayoutPosition(v);
            Address address = mAddresses.get(addressPosition);

            LatLng newLocation = new LatLng(address.getLatitude(), address.getLongitude());
            float currentZoom = mMap.getCameraPosition().zoom;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(newLocation, currentZoom);
            mMap.moveCamera(cameraUpdate);
        }

        // un-hide the remove icon when long pressed
        @Override
        public boolean onLongClick(View v) {

            if (mRemoveAddressIcon.getVisibility() == View.INVISIBLE) {
                mRemoveAddressIcon.setVisibility(View.VISIBLE);
            } else {
                mRemoveAddressIcon.setVisibility(View.INVISIBLE);
            }

            return true;
        }

    }

    // Adapter for address RecyclerView
    private class AddressAdapter extends RecyclerView.Adapter<AddressHolder> {
        private List<Address> mAddresses;

        public AddressAdapter(List<Address> addresses) {
            mAddresses = addresses;
        }

        @Override
        public AddressHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);

            return new AddressHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(AddressHolder holder, int position) {
            Address address = mAddresses.get(position);
            holder.bind(address);
        }

        @Override
        public int getItemCount() {
            return mAddresses.size();
        }
    }

    // get Address from the address user input in search bar
    public Address getAddressFromName(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        Address address = null;

        try {
            // May throw an IOException
            List<Address> addresses = coder.getFromLocationName(strAddress, 5);

            // get first available address
            if (addresses != null && !addresses.isEmpty()) {
                address = addresses.get(0);
            } else {
                return null;
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return address;
    }

    // get the device current location
    // ref: https://github.com/delight-im/Android-SimpleLocation
    private LatLng getCurrentLocation() {
        SimpleLocation location = new SimpleLocation(MainActivity.this);

        // if we can't access the location yet
        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this);
        }

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        return new LatLng(latitude, longitude);
    }

    // add location marker
    public void addMarker(LatLng location) {
        MarkerOptions marker = new MarkerOptions();
        marker.position(location);
        marker.title(location.latitude + " : " + location.longitude);
        mMap.addMarker(marker);
    }

}

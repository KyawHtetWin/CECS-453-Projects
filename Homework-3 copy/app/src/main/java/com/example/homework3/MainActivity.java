// CECS 453 Mobile Development
// Homework 3 - Mapping
// Due date: April 17, 2020

// Team members:
// Ben Do
// Kyaw Htet Win


package com.example.homework3;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import im.delight.android.location.SimpleLocation;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnPolylineClickListener{

    private static final String TAG = "Homework3 Debug:";

    // keys for Bundle
    private static final String LAT_KEY = "lat_key";
    private static final String LNG_KEY = "lng_key";
    private static final String ZOOM_KEY = "zoom_key";
    private static final String ADDRESSES_KEY = "addresses_key";
    private static final String MAPTYPE_KEY = "maptype_key";
    private static final String HIDDEN_KEY = "hidden_key";

    // data
    private Bundle bundle;
    private ArrayList<Address> mAddresses;

    private GoogleMap mMap;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    public static final float INITIAL_ZOOM = 15f;


    // A list that stores all the location while user was running
    List<LatLng> mUserPath = new ArrayList<>();
    // Polyline to draw the user path on the Maps
    Polyline mUserPathPolyline;

    // A reference for real-time database
    private DatabaseReference mDatabaseRef;

    ImageView save_screenshot;

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


        save_screenshot = findViewById(R.id.save_screenshot);

        // Add the mUserPath with location's lat and long from the RunActivity
        mUserPath.add(new LatLng(33.880804, -118.089164));
        mUserPath.add(new LatLng(33.8808034, -118.089114));
        mUserPath.add(new LatLng(33.880804, -118.088957));
        mUserPath.add(new LatLng(33.880804, -118.088752));
        mUserPath.add(new LatLng(33.881120, -118.088589));



        mUserPathPolyline = null;

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, mapFragment)
                .commit();

        mapFragment.getMapAsync(this);
        setupUI();
    }



    // set up the views and listeners
    public void setupUI() {


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
            CameraUpdate cameraUpdate = CameraUpdateFactory
                    .newLatLngZoom(currentLocation, INITIAL_ZOOM);
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

        drawUserRoute(mUserPath);

    }

    // Add the user route & style the polyline
    public void drawUserRoute(List<LatLng> positions) {

        // Add marker to the start and end of the path
        addMarker(positions.get(0));
        addMarker(positions.get(positions.size() -1));

        // String type = "A";
        String type = "";

        // Width of the polyline
        int polyline_stroke_with_px = 20;


        // Adds a polyline, that traces the positions passed, on the Google Map
        mUserPathPolyline = mMap.addPolyline(new PolylineOptions().clickable(true).addAll(positions));

        // Set listeners for click events
        mMap.setOnPolylineClickListener(this);

        // Add a start cap at the start of the polyline
        switch (type) {
            case "A":
                // Use a custom bitmap as the cap at the start of the line
                mUserPathPolyline.setStartCap(new CustomCap(BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_place_black_24dp), 10));
                break;

            default:
                // If no type is given for custom marker, use a round cap at the start of the line
                //mUserPathPolyline.setStartCap(new RoundCap());
                break;

        }

        //mUserPathPolyline.setEndCap(new RoundCap());
        mUserPathPolyline.setWidth(polyline_stroke_with_px);
        mUserPathPolyline.setColor(Color.CYAN);
        mUserPathPolyline.setJointType(JointType.ROUND);

    }

    // Override the onPolylineClick() method. Gets called whenever the user pressed on the polyline
    @Override
    public void onPolylineClick(Polyline polyline) {

        PatternItem DOT = new Dot();
        int pattern_gap_length_px = 10;
        PatternItem GAP = new Gap(pattern_gap_length_px);

        // Create a stroke pattern of a gap followed by a dot
        List<PatternItem> pattern_polyline_dotted = Arrays.asList(GAP, DOT);

        // Flip from a solid stroke to the dotted stroke pattern
        if((polyline.getPattern() == null)||(!polyline.getPattern().contains(DOT)))
            polyline.setPattern(pattern_polyline_dotted);

        // Otherwise, default to the solid stroke
        else
            polyline.setPattern(null);
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

    public void onSaveScreen(View view) {
        final GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {

                save_screenshot.setImageBitmap(bitmap);
                uploadImage(bitmap);
            }
        };
        mMap.snapshot(callback);
    }


    public void uploadImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://homework3-5e976.appspot.com");

        //StorageReference imagesRef = storageRef.child("images/screenshot2.jpg");
        // INSTEAD OF Passing in an image name everytime, pass system time in milliseconds to get
        // unique name
        StorageReference imagesRef = storageRef.child("images/"+ System.currentTimeMillis() +".jpg");

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getApplicationContext(),"Firebase Saved not successful",Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                String downloadUrl = taskSnapshot.getStorage().getDownloadUrl().toString();
                // Do what you want
                Upload upload = new Upload("Google Map Screenshot", downloadUrl);
                // Creates a new entry in our database with unique id
                String uploadId = mDatabaseRef.push().getKey();
                // And set its data to our upload file
                mDatabaseRef.child(uploadId).setValue(upload);

                // Save the  screenshot
                Toast.makeText(getApplicationContext(),"Firebase Saved",Toast.LENGTH_LONG).show();

            }
        });
    }


    public void onUploadedImage(View view) {
        startActivity(new Intent(this, ImageActivity.class));
    }
}
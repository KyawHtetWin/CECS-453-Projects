package com.example.runningmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final int DEFAULT_INTERVAL = 2;
    public static final int FAST_INTERVAL = 1;
    private static final int PERMISSIONS_FINE_LOCATION = 111;

    // UI elements
    TextView tv_lat, tv_lon, tv_acc, tv_sensor, tv_speed, tv_distance, tv_old_new_location;
    ToggleButton tb_start_pause;
    Button btn_finish;

    // Indicates the number of seconds passed on StopWatch
    private int seconds;

    // Indicates whether the stopwatch is running (Users physically running)
    private boolean running;

    // Indicates whether the stopwatch was running before screen rotation (Users physically running)
    private boolean wasRunning;

    // Google's API for location services
    private FusedLocationProviderClient fusedLocationProviderClient;

    // Stores the user's current location at all time
    private Location mCurrentLocation = null;

    // Store the user's location fetched before the current location at all time
    private Location mOldLocation = null;

    // Shows the time when the GPS information is last updated
    private String mLastUpdateTime;

    // Store the total distance in meters that the users have travelled
    private double mTotalDistance = 0;

    // LocationRequest used to configure all settings related to FusedLocationProviderClient
    private LocationRequest locationRequest;
    private LocationCallback locationCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get References to UI
        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_acc = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        tv_distance = findViewById(R.id.tv_distance);
        tv_old_new_location = findViewById(R.id.tv_old_new_location);
        tb_start_pause = findViewById(R.id.tb_start_pause);
        btn_finish = findViewById(R.id.btn_finish);


        // Restore the variables appropriately
        if(savedInstanceState != null) {
            seconds = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
        }

        else{
            seconds = 0;
            running = false;
            wasRunning = false;
        }


        // Set the properties of LocationRequest
        locationRequest = new LocationRequest();

        // Retrieve location data every 4 secs
        locationRequest.setInterval(1000 * DEFAULT_INTERVAL);

        // Retrieve location data every 1 secs on the fastest interval
        locationRequest.setFastestInterval(1000 * FAST_INTERVAL);

        // Wants to retrieve with highest accuracy (i.e Using GPS)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Get the user's last known location or their current location when the app runs for
        // the first time
        updateGPS();

        // event that is triggered whenever the update interval is met
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if(locationResult != null) {

                    // Stores the time at which LocationCallback is triggered
                    mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                    // Location has previously been fetched. Update the old location
                    if(mCurrentLocation != null)
                        mOldLocation = mCurrentLocation;

                    // Update the mCurrentLocation with the latest location retrieved
                    mCurrentLocation = locationResult.getLastLocation();
                    updateGPSUI(mCurrentLocation, mOldLocation);
                }
            }
        };


        tb_start_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the stopwatch when the Start Run is pressed
                if(tb_start_pause.isChecked()) {
                    // Start running
                    running = true;
                }
                // Pause the stopwatch when the Pause is pressed
                else {
                    // Pause the running
                    running = false;
                }
            }
        });

        setStopWatch();

    } // End of OnCreate


    @Override
    protected void onResume() {
        super.onResume();
        // If the user was running before the activity gets destroyed for some reasons, they
        // should be able to continue running afterwards
        if(wasRunning)
            running = true;

        // Start Location Update
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack,
                null);
    }

    // Save the necessary variables to survive Activity destruction
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("seconds", seconds);
        outState.putBoolean("running", running);
        outState.putBoolean("wasRunning", wasRunning);
    }

    // This method is called when the user have finished a running section
    public void onClickFinishRun(View view) {
        running = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // If the user activity gets paused for some reason in the middle of the run,
        // set the wasRunning to be able to restore its state appropriately
        wasRunning = running;
        running = false;

        // Stop Location Update
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }

    // This method will update the stopwatch every second
    private void setStopWatch() {
        final  TextView tv_timer = findViewById(R.id.tv_timer);
        // Creating a reference to a new Handler
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds/3600;
                int minutes = (seconds%3600) / 60;
                int secs = seconds%60;

                // Format the time
                String time = String.format("%d:%02d:%02d", hours, minutes, secs);
                tv_timer.setText(time);

                if(running)
                    seconds++;

                // Keep posting the code to be run after a delay of 1 second
                handler.postDelayed(this, 1000* 1);
            }
        });
    }

    // Update the GPS
    private void updateGPS() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
                MainActivity.this);

        // Check to make sure that permission for location is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // User provided the permission. So, get the last know location.
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this,
                    new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Update the GPS GUI
                            if (location != null) {

                                // Location has previously been fetched. Update the old location
                                if(mCurrentLocation != null)
                                    mOldLocation = mCurrentLocation;

                                // Update the mCurrentLocation with the latest location retrieved
                                mCurrentLocation = location;
                                updateGPSUI(mCurrentLocation, mOldLocation);

                            }
                        }
                    });
        }

        else {
            // Permission not granted yet
            // Our build version needs to be higher than 23
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_FINE_LOCATION);
            }
        }
    }

    // This  method gets triggered after permission has been granted. requestCode is the
    // number assigned, permissions specify which permission was requested.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode) {
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted. Do nothing and carry on
                }

                else{
                    Toast.makeText(this, "This app requires permission to be granted" +
                            "to work properly", Toast.LENGTH_SHORT).show();
                    // Exit the program
                    finish();
                }
                break;
        }
    }


    // Update GPS related UI values with the location passed
    private void updateGPSUI(Location currentLocation, Location oldLocation) {

        tv_lat.setText("Latitude: " + String.valueOf(currentLocation.getLatitude()));
        tv_lon.setText("Longitude: " + String.valueOf(currentLocation.getLongitude()));
        tv_acc.setText("Accuracy: "+ String.valueOf(currentLocation.getAccuracy()));

        if(currentLocation.hasSpeed())
            tv_speed.setText("Speed: " + String.valueOf(currentLocation.getSpeed()) + " m/s");
        else
            tv_speed.setText("Speed not available");

        // This variable stores the result of the distance travelled between two points in one or
        // two seconds interval
        double distance = 0;
        String locationFetchInfo = "OLD LOCATION: ";

        if(oldLocation!=null) {
            locationFetchInfo += "(Lat " + oldLocation.getLatitude() + " , Lon " +
                    oldLocation.getLongitude() + ")";

            distance = findDistance(oldLocation, currentLocation);

            // Distance may not be fetched for every time interval that is requested,
            // but it might be good enough
            /*
            if(distance != 0)
                tv_distance.setText("Distance: "+ String.valueOf(distance) + " m");
            else
                tv_distance.setText("Distance Not Fetched ");

             */

            // Distance is fetched within my specified interval (DEFAULT or FAST_INTERVAL)
            if(distance != 0)
                mTotalDistance += distance;

            // Distance is not fetched within my specified interval, so increment the total distance
            // by zero
            else
                mTotalDistance += 0;

            tv_distance.setText("Total Distance: "+ String.valueOf(mTotalDistance) + " m");

        }

        // Since oldLocation is null, this is the first time fetching
        else{
            locationFetchInfo += "None";
            tv_distance.setText("Distance: 0.0 m");
        }

        String locationRetrievalInfo = locationFetchInfo + "\nNEW LOCATION: " +
                "(Lat " + mCurrentLocation.getLatitude() + " , Lon " +
                mCurrentLocation.getLongitude() + ")" + "\nLast Update Time: " + mLastUpdateTime;

        tv_old_new_location.setText(locationRetrievalInfo);

    }

    // This method computes and returns the distance between the start and end location
    private double findDistance(Location startLocation, Location endLocation) {
        float distance = 0;
        // Will store the distance computed
        float[] distanceResults = new float[2];

        double startLat = startLocation.getLatitude();
        double startLon = startLocation.getLongitude();
        double endLat = endLocation.getLatitude();
        double endLon = endLocation.getLongitude();

        Location.distanceBetween(startLat, startLon, endLat, endLon, distanceResults);
        // Get the distance
        distance = distanceResults[0];

        return distance;
    }


}

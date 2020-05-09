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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import java.util.ArrayList;
import java.util.Date;

public class RunActivity extends AppCompatActivity {

    public static final int DEFAULT_INTERVAL = 1;
    //public static final int FAST_INTERVAL = 5;
    private static final int PERMISSIONS_FINE_LOCATION = 111;

    // UI elements
    TextView tv_lat, tv_lon, tv_speed, tv_distance, tv_old_new_location;
    Button btn_start, btn_pause, btn_stop;

    // Indicates the number of seconds passed on StopWatch
    private int seconds;

    // Indicates whether the stopwatch is running (Users physically running)
    private boolean running;

    // Indicates whether the stopwatch was running before screen rotation (Users physically running)
    private boolean wasRunning;

    // Google's API for location services
    private FusedLocationProviderClient fusedLocationProviderClient;

    // Stores the user's current location at all time
    private Location mCurrentLocation;

    // Store the user's location fetched before the current location at all time
    private Location mOldLocation;

    // Shows the time when the GPS information is last updated
    private String mLastUpdateTime;

    // Store the total distance in meters that the users have travelled
    private double mTotalDistanceMeter = 0;

    private double mTotalDistanceMile;

    // LocationRequest used to configure all settings related to FusedLocationProviderClient
    private LocationRequest locationRequest;
    private LocationCallback locationCallBack;

    // Stores the last location while running
    private Location mRunningLocation;
    // Stores the previous to the last location while running
    private Location mPrevRunningLocation;

    //Stores a list of all the location that users have run
    private ArrayList<Location> runLocations;

    private boolean isRunPaused;

    // Animation
    Animation start_btn, pause_btn, stop_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        // Get References to UI
        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_speed = findViewById(R.id.tv_speed);
        tv_distance = findViewById(R.id.tv_distance);
        tv_old_new_location = findViewById(R.id.tv_old_new_location);


        btn_start = findViewById(R.id.btn_start);
        btn_pause = findViewById(R.id.btn_pause);
        btn_stop = findViewById(R.id.btn_stop);

        // Create optional animation
        btn_stop.setAlpha(0);
        btn_pause.setAlpha(0);

        // Load the Animation
        start_btn = AnimationUtils.loadAnimation(this, R.anim.start_btn);
        pause_btn = AnimationUtils.loadAnimation(this, R.anim.pause_btn);
        stop_btn = AnimationUtils.loadAnimation(this, R.anim.stop_btn);

        // Pass the animation to their view elements
        btn_start.startAnimation(start_btn);
        btn_pause.startAnimation(pause_btn);
        btn_stop.startAnimation(stop_btn);


        // Restore the variables appropriately
        if(savedInstanceState != null) {
            seconds = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
            isRunPaused = savedInstanceState.getBoolean("isRunPaused");

            runLocations= savedInstanceState.getParcelableArrayList("runLocations");
            mRunningLocation = savedInstanceState.getParcelable("mRunningLocation");
            mPrevRunningLocation = savedInstanceState.getParcelable("mPrevRunningLocation");
            mTotalDistanceMile = savedInstanceState.getDouble("mTotalDistanceMile");
            mLastUpdateTime = savedInstanceState.getString("mLastUpdateTime");

        }

        else{
            seconds = 0;
            running = false;
            wasRunning = false;
            isRunPaused = false;

            runLocations = new ArrayList<>();
            mRunningLocation = null;
            mPrevRunningLocation = null;
            mTotalDistanceMile = 0;
            mLastUpdateTime = "";
        }

        // Set the properties of LocationRequest
        locationRequest = new LocationRequest();

        // Retrieve location data
        locationRequest.setInterval(1000 * DEFAULT_INTERVAL);

        // Retrieve location data on its fastest interval
        //locationRequest.setFastestInterval(1000 * FAST_INTERVAL);

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

                    // Location has previously been fetched. Update the old location
                    if(mCurrentLocation != null)
                        mOldLocation = mCurrentLocation;
                    // Update the mCurrentLocation with the latest location retrieved
                    mCurrentLocation = locationResult.getLastLocation();

                    // Interested only in location when user is running
                    if(running) {
                        // Stores the time at which LocationCallback is triggered
                        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                        if(mRunningLocation != null)
                            mPrevRunningLocation = mRunningLocation;

                        mRunningLocation = locationResult.getLastLocation();
                        runLocations.add(mRunningLocation);

                    }

                    //updateGPSUI(mCurrentLocation, mOldLocation);
                    updateGPSUI(mRunningLocation, mPrevRunningLocation);
                }
            }
        };

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

    // Stopwatch starts running
    public void onClickStart(View view) {
        // Show pause and stop button, hide start button
        btn_pause.animate().alpha(1).translationY(-80).setDuration(300).start();
        btn_stop.animate().alpha(1).translationY(-80).setDuration(300).start();
        btn_start.animate().alpha(0).setDuration(300).start();

        // Starts running
        running = true;
        isRunPaused = false;
    }

    // Stopwatch pauses running
    public void onClickPause(View view) {
        // Show start button, hide start and stop button
        btn_start.animate().alpha(1).translationY(-80).setDuration(300).start();
        btn_stop.animate().alpha(0).setDuration(300).start();

        // Pause the running
        running = false;
        isRunPaused = true;
    }

    // Stopwatch finishes running
    public void onClickStop(View view) {
        // Show start button, hide pause and stop button
        btn_start.animate().alpha(1).translationY(-80).setDuration(300).start();
        btn_pause.animate().alpha(0).setDuration(300).start();
        btn_stop.animate().alpha(0).setDuration(300).start();

        // Finishes the running
        running = false;
        isRunPaused = false;
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

    // Save the necessary variables to survive Activity destruction
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("seconds", seconds);
        outState.putBoolean("running", running);
        outState.putBoolean("wasRunning", wasRunning);
        outState.putBoolean("isRunPaused", isRunPaused);

        outState.putParcelableArrayList("runLocations", runLocations);
        outState.putParcelable("mRunningLocation", mRunningLocation);
        outState.putParcelable("mPrevRunningLocation", mPrevRunningLocation);
        outState.putDouble("mTotalDistanceMile", mTotalDistanceMile);
        outState.putString("mLastUpdateTime", mLastUpdateTime);


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
                this);

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

                                // Update the mCurrentLocation with the latest location retrieved
                                mCurrentLocation = location;
                                //updateGPSUI(mCurrentLocation, mOldLocation);

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

        if(currentLocation != null) {
            tv_lat.setText("Latitude: " + String.valueOf(currentLocation.getLatitude()));
            tv_lon.setText("Longitude: " + String.valueOf(currentLocation.getLongitude()));

            if(currentLocation.hasSpeed()) {
                // 1 m/s = 0.0372823 miles/minutes
                float speedMeterSec = currentLocation.getSpeed();
                // Pace is the inverse of speed
                double paceMinMiles = 1 / speedMeterSec * 0.0372823;
                //tv_speed.setText("Speed: " + String.valueOf(speedMeterSec) + " m/s");
                tv_speed.setText("Pace: " + String.valueOf(paceMinMiles) + " min/miles");
            }
            else
                tv_speed.setText("Pace not available");

            // Distance Travelled between the old location and current location
            float distanceMeters;
            double distanceMiles;

            if(oldLocation != null) {
                distanceMeters = oldLocation.distanceTo(currentLocation);
            }

            else
                distanceMeters = 0;

            distanceMiles = distanceMeters / 1609.344;

            mTotalDistanceMeter += distanceMeters;

            // If the run is paused, no need to update the mTotalDistanceMile
            if(isRunPaused)
                mTotalDistanceMile += 0;
            else
                mTotalDistanceMile += distanceMiles;

            //tv_distance.setText("Total Distance: "+ String.valueOf(mTotalDistanceMeter) + " m");
            tv_distance.setText("Total Distance: "+ String.valueOf(mTotalDistanceMile) + " miles");


            //double distance = 0;
            String locationFetchInfo = "OLD LOCATION: ";

            if(oldLocation!=null) {
                locationFetchInfo += "(Lat " + oldLocation.getLatitude() + " , Lon " +
                        oldLocation.getLongitude() + ")";
            }

            // Since oldLocation is null, this is the first time fetching
            else{
                locationFetchInfo += "None";
                //tv_distance.setText("Distance: 0.0 m");
            }

            String locationRetrievalInfo = locationFetchInfo + "\nNEW LOCATION: " +
                    "(Lat " + currentLocation.getLatitude() + " , Lon " +
                    currentLocation.getLongitude() + ")" + "\nLast Update Time: " + mLastUpdateTime;

            tv_old_new_location.setText(locationRetrievalInfo);
        }

        else {
            tv_lat.setText("Latitude: Not Retrieved yet");
            tv_lon.setText("Longitude: Not Retrieved yet");
            tv_speed.setText("Pace: Not Retrieved yet");
            tv_distance.setText("Total Distance: Not Retrieved yet");
            tv_old_new_location.setText("Not Retrieved yet");
        }
    }

}

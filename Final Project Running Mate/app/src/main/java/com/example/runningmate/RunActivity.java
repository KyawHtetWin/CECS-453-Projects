package com.example.runningmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

import im.delight.android.location.SimpleLocation;


public class RunActivity extends AppCompatActivity implements OnMapReadyCallback {

    // buttons
    private ImageButton start_button;
    private ImageButton pause_button;
    private ImageButton end_button;
    private ImageButton run_stats_button;
    private ImageButton map_button;

    // textViews
    private TextView time_textView;
    private TextView distance_textView;
    private TextView currentPace_textView;
    private TextView avgPace_textView;

    private FrameLayout runMapFrameLayout;
    private LinearLayout runStatsLinearLayout;

    // run stats
    private int seconds;
    private double distance;
    private int currentPace;
    private int avgPace;
    private ArrayList<LatLng> positions;

    // timer
    private CountDownTimer timer;

    // while running or on pause
    private boolean isRunning;

    // mapping components
    private SimpleLocation location;
    private SimpleLocation.Point oldPosition;

    private GoogleMap map;
    private Polyline userPath;

    private String dateTime;

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    public static final float INITIAL_ZOOM = 15f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        distance = 0;
        currentPace = 0;
        avgPace = 0;
        positions = new ArrayList<>();

        setupViews();
        setupTimer();
        getLocation();
        getMap();

        getDateTime();
        startTimer();

    } // end of OnCreate()

    public void getMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.runMap_fragment, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);
    }

    public void getDateTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' h:mm a");
        LocalDateTime dt = LocalDateTime.now();
        dateTime = dtf.format(dt);
    }

    public void getLocation() {
        location = new SimpleLocation(this, true, false, 2000);
        oldPosition = location.getPosition();

        positions.add(new LatLng(location.getLatitude(), location.getLongitude())); // add initial position

        // if we can't access the location yet
        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this);
        }

        location.setListener(new SimpleLocation.Listener() {
            @Override
            public void onPositionChanged() {
                if (isRunning) {
                    updateSpeed();
                    updateDistance();

                    oldPosition = location.getPosition();
                    positions.add(new LatLng(location.getLatitude(), location.getLongitude())); // add new position
                }
            }
        });
    }

    // request and set device location permission
    private void enableLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
                    enableLocation();
                    break;
                }
        }
    }

    public void updateDistance() {
        double lat1 = oldPosition.latitude;
        double lng1 = oldPosition.longitude;

        double lat2 = location.getLatitude();
        double lng2 = location.getLongitude();

        distance = distance + SimpleLocation.calculateDistance(lat1, lng1, lat2, lng2);
        double mile = distance / 1609.344;
        String mileStr = String.format(Locale.US,"%.2f", mile);

        distance_textView.setText(mileStr);
    }

    public void updateSpeed() {
        // 1 m/s = 1609.344 seconds/mile
        double speed = (1609.34/location.getSpeed());
        int currentPaceInSeconds = (int) speed; // pace in seconds per mile

        int hours = currentPaceInSeconds / 3600;
        int minutes = (currentPaceInSeconds % 3600) / 60;
        int secs = currentPaceInSeconds % 60;

        String pace = "";
        if (hours == 0) {
            pace = String.format(Locale.US,"%02d:%02d", minutes, secs);
        } else {
            pace = "00:00";
        }

        currentPace_textView.setText(pace);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setupViews() {

        //TODO set up listeners for map and setting buttons
        runMapFrameLayout = findViewById(R.id.runMap_fragment);
        runStatsLinearLayout = findViewById(R.id.stats_LinearLayout);

        start_button = findViewById(R.id.start_button);
        pause_button = findViewById(R.id.pause_button);
        end_button = findViewById(R.id.end_button);
        run_stats_button = findViewById(R.id.run_stats_button);
        map_button = findViewById(R.id.map_button);


        map_button.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(new BlendModeColorFilter(0xe0f47521, BlendMode.SRC_ATOP));
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();

                        runMapFrameLayout.setVisibility(View.VISIBLE);
                        runStatsLinearLayout.setVisibility(View.INVISIBLE);
                        break;
                    }
                }
                return false;
            }
        });

        run_stats_button.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(new BlendModeColorFilter(0xe0f47521, BlendMode.SRC_ATOP));
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();

                        runMapFrameLayout.setVisibility(View.INVISIBLE);
                        runStatsLinearLayout.setVisibility(View.VISIBLE);
                        break;
                    }
                }
                return false;
            }
        });


        start_button.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(new BlendModeColorFilter(0xe0f47521, BlendMode.SRC_ATOP));
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();

                        startTimer(); // start or resume timer

                        start_button.setVisibility(View.INVISIBLE);
                        end_button.setVisibility(View.INVISIBLE);

                        pause_button.setVisibility(View.VISIBLE);
                        run_stats_button.setVisibility(View.VISIBLE);
                        map_button.setVisibility(View.VISIBLE);
                        break;
                    }
                }
                return false;
            }
        });

        pause_button.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(new BlendModeColorFilter(0xe0f47521, BlendMode.SRC_ATOP));
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();

                        stopTimer(); // stop or pause the timer

                        pause_button.setVisibility(View.INVISIBLE);
                        run_stats_button.setVisibility(View.INVISIBLE);
                        map_button.setVisibility(View.INVISIBLE);

                        start_button.setVisibility(View.VISIBLE);
                        end_button.setVisibility(View.VISIBLE);
                        break;
                    }
                }
                return false;
            }
        });

        end_button.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(new BlendModeColorFilter(0xe0f47521, BlendMode.SRC_ATOP));
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();

                        AlertDialog.Builder builder = new AlertDialog.Builder(RunActivity.this, android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth);
                        builder.setMessage("Save run?")
                                .setCancelable(false)
                                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        runMapFrameLayout.setVisibility(View.VISIBLE);
                                        runStatsLinearLayout.setVisibility(View.INVISIBLE);
                                        drawUserRoute();

                                        captureRoute();
                                        goToHomeScreen();
                                    }
                                })
                                .setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        goToHomeScreen();
                                    }
                                })
                                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                        break;
                    }
                }
                return false;
            }
        });

        time_textView = findViewById(R.id.timer_text_view);
        distance_textView = findViewById(R.id.distance_text_view);
        currentPace_textView = findViewById(R.id.current_pace_text_view);
        avgPace_textView = findViewById(R.id.avg_pace_text_view);

    } // end of setupViews()

    public void sendDataToDatabase(String imageUrl) {

        double mile = distance / 1609.344;
        String distance = String.format(Locale.US,"%.2f", mile);

        //=====================================================//
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;

        String elapseTime;
        if (hours == 0) {
            elapseTime = String.format(Locale.US,"%02d:%02d", minutes, secs);
        } else {
            elapseTime = String.format(Locale.US,"%d:%02d:%02d", hours, minutes, secs);
        }

        //=====================================================//
        int avgPace = (int) (seconds / mile); // avg pace in seconds per mile
        hours = avgPace / 3600;
        minutes = (avgPace % 3600) / 60;
        secs = avgPace % 60;

        String pace;
        if (hours == 0) {
            pace = String.format(Locale.US,"%02d:%02d", minutes, secs);
        } else {
            pace = "00:00";
        }

        //=====================================================//
        RunData runData = new RunData(elapseTime, pace, distance, dateTime, imageUrl);

        FirebaseHandler fbHandler = new FirebaseHandler();
        String uid = fbHandler.getCurrentUser().getUid();

        fbHandler.pushRunData(runData, uid);
    }

    // Add the user route & style the polyline
    public void drawUserRoute() {

        // Width of the polyline
        int polyline_stroke_with_px = 20;

        // Adds a polyline, that traces the positions passed, on the Google Map
        userPath = map.addPolyline(new PolylineOptions().clickable(true).addAll(positions));

        userPath.setWidth(polyline_stroke_with_px);
        userPath.setColor(Color.RED);
        userPath.setJointType(JointType.ROUND);
    }

    public void captureRoute() {

        final GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                uploadRouteImage(bitmap);
            }
        };
        map.snapshot(callback);
    }

    public void uploadRouteImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        FirebaseHandler fbHandler = new FirebaseHandler();
        String uid = fbHandler.getCurrentUser().getUid();

        UploadTask uploadTask = fbHandler.pushRouteImage(data, uid);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(RunActivity.this, "UPLOAD FAIL", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                if (taskSnapshot.getMetadata() != null) {
                    if (taskSnapshot.getMetadata().getReference() != null) {
                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();

                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                sendDataToDatabase(imageUrl);
                            }
                        });
                    }
                }
            }
        });
    }

    public void goToHomeScreen() {
        Intent intent = new Intent(RunActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void setupTimer() {
        long maxTime = 43200; // 12 hours max time
        long intervalSeconds = 1000; // 1 second interval
        seconds = 0; // elapsed time

        timer = new CountDownTimer(maxTime * 1000, intervalSeconds) {
            public void onTick(long millisUntilFinished) {
                seconds++;

                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                String time = "";
                if (hours == 0) {
                    time = String.format(Locale.US,"%02d:%02d", minutes, secs);
                } else {
                    time = String.format(Locale.US,"%d:%02d:%02d", hours, minutes, secs);
                }

                time_textView.setText(time);
            }

            @Override
            public void onFinish() { }
        };
    } // end of setupTimer()

    public void startTimer() {
        timer.start();
        isRunning = true;
    }

    public void stopTimer() {
        timer.cancel();
        isRunning = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // make the device update its location
        location.beginUpdates();
    }

    @Override
    protected void onPause() {
        // stop location updates
        location.endUpdates();
        super.onPause();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        enableLocation();

        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory
                .newLatLngZoom(currentLocation, INITIAL_ZOOM);
        map.moveCamera(cameraUpdate);
    }
}

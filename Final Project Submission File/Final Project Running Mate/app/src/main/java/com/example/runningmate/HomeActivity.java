/****
 * The HomeActivity displays the user profile information in the navigation drawer and
 * show the user's feed in the RecyclerView that retrieves all the user's previous run.
 *
 *****/

package com.example.runningmate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import im.delight.android.location.SimpleLocation;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    public static final float INITIAL_ZOOM = 15f;

    private FrameLayout mapContainer;
    private FrameLayout activityFeedContainer;

    private Button startRunButton;
    private ImageButton profileButton;
    private ImageButton mapButton;
    private ImageButton activityFeedButton;

    // nav drawer views
    private DrawerLayout drawer;
    private NavigationView navView;
    private TextView navDisplayName;
    private TextView navEmail;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();

        currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;

        } else {

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.mapFragment_container, mapFragment)
                    .commit();
            mapFragment.getMapAsync(this);

            setUpViews();
            setUpRV();
            setUpNavDrawer();

        }
    }

    // The navigation drawer displays the user information and provides a support section
    public void setUpNavDrawer() {
        navView = findViewById(R.id.nav_view);
        View parentView = navView.getHeaderView(0);

        // must use parent view to initialize the child views in navigation view
        navDisplayName = parentView.findViewById(R.id.nav_displayName_textView);
        navEmail = parentView.findViewById(R.id.nav_email_textView);

        navDisplayName.setText(currentUser.getDisplayName());
        navEmail.setText(currentUser.getEmail());

        // Set up listeners for each item in the navigation drawer
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_profile:
                        editProfilePressed();
                        break;
                    case R.id.nav_logout:
                        logOutPressed();
                        break;
                    case R.id.nav_contact:
                        contactPressed();
                        break;
                    case R.id.nav_about:
                        aboutPressed();
                        break;
                }
                return true;
            }

        });


    }

    // This sets up views and listener to respond to the user
    @SuppressLint("ClickableViewAccessibility")
    public void setUpViews() {
        mapContainer = findViewById(R.id.mapFragment_container);
        activityFeedContainer = findViewById(R.id.activityFeed_container);

        startRunButton = findViewById(R.id.startRun_button);
        profileButton = findViewById(R.id.profile_button);
        mapButton = findViewById(R.id.homeMap_button);
        activityFeedButton = findViewById(R.id.activityFeed_button);

        drawer = findViewById(R.id.drawer_layout);

        // Takes the user to the Run Activity
        startRunButton.setOnTouchListener(new View.OnTouchListener() {
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

                        Intent intent = new Intent(HomeActivity.this, RunActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case MotionEvent.ACTION_OUTSIDE: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });

        // Shows the map to when clicked
        mapButton.setOnTouchListener(new View.OnTouchListener() {
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

                        mapContainer.setVisibility(View.VISIBLE);
                        activityFeedContainer.setVisibility(View.INVISIBLE);
                        break;
                    }
                }
                return false;
            }
        });

        // Show the activity feed to the user with all the stats about the previous run
        activityFeedButton.setOnTouchListener(new View.OnTouchListener() {
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

                        mapContainer.setVisibility(View.INVISIBLE);
                        activityFeedContainer.setVisibility(View.VISIBLE);
                        break;
                    }
                }
                return false;
            }
        });

        // Show the navigation bar to the user with their profile information
        profileButton.setOnTouchListener(new View.OnTouchListener() {
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

                        drawer.openDrawer(GravityCompat.START);

//                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this, android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth);
//                        builder.setMessage("Are you sure you want to log out?")
//                                .setCancelable(false)
//                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        auth.signOut();
//                                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
//                                        startActivity(intent);
//                                        finish();
//                                    }
//                                })
//                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                    }
//                                });
//                        AlertDialog alert = builder.create();
//                        alert.show();
                        break;
                    }
                }
                return false;
            }
        });

    }

    // Gets called when the map is ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation();
        setUpMap();
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

    // Check if location permissions are granted and if so enable the
    // location data layer.
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation();
                    break;
                }
        }
    }

    // set up initial map or restore map state
    public void setUpMap() {
    // set up initial map
        LatLng currentLocation = getCurrentLocation();
        CameraUpdate cameraUpdate = CameraUpdateFactory
                .newLatLngZoom(currentLocation, INITIAL_ZOOM);
        mMap.moveCamera(cameraUpdate);

    }

    // get the device current location
    // ref: https://github.com/delight-im/Android-SimpleLocation
    private LatLng getCurrentLocation() {
        SimpleLocation location = new SimpleLocation(this);

        // if we can't access the location yet
        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this);
        }

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        return new LatLng(latitude, longitude);
    }

    // Use the Firebase's Recycler View that will fetch user information from Firebase for the
    // current user
    public void setUpRV() {
        recyclerView= findViewById(R.id.activityFeed_recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        Query query = FirebaseDatabase.getInstance()
                .getReference("Users Activity")
                .child(currentUser.getUid());

        FirebaseRecyclerOptions<RunData> options =
                new FirebaseRecyclerOptions.Builder<RunData>()
                        .setQuery(query, new SnapshotParser<RunData>() {
                            @NonNull
                            @Override
                            public RunData parseSnapshot(@NonNull DataSnapshot snapshot) {

                                String dateTime = snapshot.child("dateTime").getValue().toString();
                                String distance = snapshot.child("distance").getValue().toString();
                                String elapsedTime = snapshot.child("elapsedTime").getValue().toString();
                                String pace = snapshot.child("pace").getValue().toString();
                                String imageUrl = snapshot.child("imageUrl").getValue().toString();

                                return new RunData(elapsedTime, pace, distance, dateTime, imageUrl);
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<RunData, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rv_list_item, parent, false);

                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(ViewHolder holder, final int position, RunData model) {
                holder.bind(model.elapsedTime, model.distance, model.pace, model.dateTime, model.imageUrl);
            }

        };

        recyclerView.setAdapter(adapter);
    }

    // ViewHolder for the Recycler View that displays all information on a single run
    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView timeTextView;
        TextView distanceTextView;
        TextView paceTextView;
        TextView dateTimeTextView;
        TextView displayNameTextView;
        ImageView routeImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            timeTextView = itemView.findViewById(R.id.feedtime_textView);
            distanceTextView = itemView.findViewById(R.id.feedDistance_textView);
            paceTextView = itemView.findViewById(R.id.feedPace_textView);
            dateTimeTextView = itemView.findViewById(R.id.feedDateTime_textView);
            displayNameTextView = itemView.findViewById(R.id.feedName_textView);
            routeImageView = itemView.findViewById(R.id.mapRoute_imageView);
        }

        public void bind(String time, String distance, String pace, String dateTime, String imageUrl) {

            displayNameTextView.setText(currentUser.getDisplayName());
            distanceTextView.setText(distance + " mi");
            timeTextView.setText(time);
            paceTextView.setText(pace + "/mi");
            dateTimeTextView.setText(dateTime);

            // Retrieves data with the image url from the firebase
            if (imageUrl.equals("NO IMAGE")) {
                Picasso.get().load(R.drawable.sample_route).into(routeImageView);
            } else {
                Picasso.get()
                        .load(imageUrl)
                        .error(R.drawable.sample_route)      // Image to load when something goes wrong
                        .fit()
                        .centerCrop()
                        .into(routeImageView);
            }

        }

    }

    // This method enables the user to update their profile name
    public void editProfilePressed() {
        final EditText editText = new EditText(HomeActivity.this);

        AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this,
                android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth);
        alert.setMessage("Enter new name");
        alert.setView(editText);

        alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String newDisplayName = editText.getText().toString().trim();

                if (newDisplayName.isEmpty()) {
                    newDisplayName = "Unknown";
                }

                // Changes the request on the Firebase
                UserProfileChangeRequest update = new UserProfileChangeRequest.Builder()
                        .setDisplayName(newDisplayName)
                        .build();

                if (currentUser != null){
                    currentUser.updateProfile(update);
                    navDisplayName.setText(newDisplayName);
                }
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }

    // Allows the user to log out of the app
    public void logOutPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this,
                android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth);
        builder.setMessage("Are you sure you want to log out?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        auth.signOut();
                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // Show them the information where they can contact to get support on the application
    public void contactPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this,
                android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth);
        builder.setTitle("Support");
        builder.setMessage("Email: support@runningmate.com");
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    // Show the information on the version of our application
    public void aboutPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this,
                android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth);
        builder.setTitle("About");
        builder.setMessage("Running Mate\nVersion 1.0\nDate: May 15th, 2020");
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    // Close the navigation drawer if it's open
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}

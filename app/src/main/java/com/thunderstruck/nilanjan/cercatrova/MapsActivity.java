package com.thunderstruck.nilanjan.cercatrova;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.thunderstruck.nilanjan.cercatrova.support.EmergencyPersonnel;
import com.thunderstruck.nilanjan.cercatrova.support.User;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Maps UI Activity
 * @author Nilanjan and Debapriya
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final int REQUEST_ACCESS_LOCATION = 0;
    public static final String TAG = "MapsActivity";
    @BindView(R.id.name) TextView name;
    @BindView(R.id.id) TextView id;
    @BindView(R.id.car_number) TextView carNumber;
    @BindView(R.id.base) TextView base;
    @BindView(R.id.contact) Button contact;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private User user;
    private EmergencyPersonnel emergencyPersonnel;

    /**
     * Perform initialization of all fragments and loaders.
     * @param savedInstanceState is a Bundle object containing the activity's previously saved state.
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        user = (User) getIntent().getSerializableExtra("profile_data");
        emergencyPersonnel = (EmergencyPersonnel) getIntent().getSerializableExtra("emergency_responder");
        String name = "Name: " + emergencyPersonnel.getFirstName() + " " + emergencyPersonnel.getLastName();
        String id = "ID: " + emergencyPersonnel.getPersonnelId();
        String base = "Base Station: " + emergencyPersonnel.getBaseStation();
        String carNumber = "Car Number: " + emergencyPersonnel.getCarNumber();
        this.name.setText(name);
        this.id.setText(id);
        this.base.setText(base);
        this.carNumber.setText(carNumber);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + emergencyPersonnel.getContactNumber()));
                startActivity(intent);
            }
        });
    }

    /**
     * When the activity enters the Resumed state, it comes to the foreground, and then the system invokes the onResume() callback
     * In this state, the user interacts with the app.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient != null) {
            buildGoogleApiClient();
        }
    }

    /**
     * When an interruptive event occurs, the activity enters the Paused state, and the system invokes the onPause() callback.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //creating user location object
        LatLng userLocation = new LatLng(user.getLocation().getCoordinates().get(0),
                user.getLocation().getCoordinates().get(1));
        //creating personnel location object
        LatLng personnelLocation = new LatLng(emergencyPersonnel.getLocation().getCoordinates().get(0),
                emergencyPersonnel.getLocation().getCoordinates().get(1)
                );
        //instantiating personnel location bitmap marker
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.policecar);
        //marking personnel location and moving camera focus to user's location
        mMap.addMarker(new MarkerOptions().position(personnelLocation).icon(bitmapDescriptor));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f));
        getLocationUpdate();
    }

    /**
     * Setting up location update
     */
    private void getLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mayRequestLocation();
            return;
        }
        mMap.setMyLocationEnabled(true);
        buildGoogleApiClient();
    }

    /**
     * Connecting to Google's location API Client
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /**
     * dynamically requesting permission for accessing GPS location
     * @return status of the permission
     */
    private boolean mayRequestLocation() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
            Snackbar.make(((Activity) getBaseContext()).findViewById(android.R.id.content),
                    R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, REQUEST_ACCESS_LOCATION);
                        }
                    });
        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, REQUEST_ACCESS_LOCATION);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ACCESS_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationUpdate();
            }
        }
    }

    /**
     * method invoked on receiving GPS update
     * @param location object containing new GPS coordinates
     */
    @Override
    public void onLocationChanged(Location location) {
        //TODO update server about user location update
        com.thunderstruck.nilanjan.cercatrova.support.Location userLocation;
        ArrayList<Double> coordinates = new ArrayList<>();
        coordinates.add(location.getLatitude());
        coordinates.add(location.getLongitude());
        //updating user location according to new GPS coordinates
        userLocation = new com.thunderstruck.nilanjan.cercatrova.support.Location("POINT", coordinates);
        user.setLocation(userLocation);
        Log.d(TAG, "onLocationChanged: " + location.getLatitude() + " " + location.getLongitude());
    }

    /**
     * Callback method invoked on successful Google API connection
     * Location update request callbacks have started
     * @param bundle connection details
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

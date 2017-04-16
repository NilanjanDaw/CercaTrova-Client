package com.thunderstruck.nilanjan.cercatrova;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import butterknife.OnClick;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final int REQUEST_ACCESS_LOCATION = 0;
    public static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private User user;
    private EmergencyPersonnel emergencyPersonnel;
    @BindView(R.id.name) TextView name;
    @BindView(R.id.id) TextView id;
    @BindView(R.id.car_number) TextView carNumber;
    @BindView(R.id.base) TextView base;
    @BindView(R.id.contact) Button contact;

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

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient != null) {
            buildGoogleApiClient();
        }
    }

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

        LatLng userLocation = new LatLng(user.getLocation().getCoordinates().get(0),
                user.getLocation().getCoordinates().get(1));
        LatLng personnelLocation = new LatLng(emergencyPersonnel.getLocation().getCoordinates().get(0),
                emergencyPersonnel.getLocation().getCoordinates().get(1)
                );
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.policecar);
        mMap.addMarker(new MarkerOptions().position(personnelLocation).icon(bitmapDescriptor));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f));
        getLocationUpdate();
    }

    private void getLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mayRequestLocation();
            return;
        }
        mMap.setMyLocationEnabled(true);
        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

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

    @Override
    public void onLocationChanged(Location location) {
        //TODO update server about user location update
        com.thunderstruck.nilanjan.cercatrova.support.Location userLocation;
        ArrayList<Double> coordinates = new ArrayList<>();
        coordinates.add(location.getLatitude());
        coordinates.add(location.getLongitude());
        userLocation = new com.thunderstruck.nilanjan.cercatrova.support.Location("POINT", coordinates);
        user.setLocation(userLocation);
        Log.d(TAG, "onLocationChanged: " + location.getLatitude() + " " + location.getLongitude());
    }

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

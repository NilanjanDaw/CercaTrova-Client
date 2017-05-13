package com.thunderstruck.nilanjan.cercatrova;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.thunderstruck.nilanjan.cercatrova.support.Constants;
import com.thunderstruck.nilanjan.cercatrova.support.Emergency;
import com.thunderstruck.nilanjan.cercatrova.support.EmergencyPersonnel;
import com.thunderstruck.nilanjan.cercatrova.support.Endpoint;
import com.thunderstruck.nilanjan.cercatrova.support.UpdatePacket;
import com.thunderstruck.nilanjan.cercatrova.support.User;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

//import android.support.v4.app.ActivityCompatApi23;

/**
 * Created by Nilanjan and Debapriya on 09-Apr-17.
 * Project CercaTrova
 */

//TODO refactor to SRS nomenclature
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int REQUEST_ACCESS_LOCATION = 0;
    private static final int REQUEST_CHECK_SETTINGS = 10;
    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 10;
    @BindView(R.id.ambulance)
    Button ambulance;
    @BindView(R.id.police)
    Button police;
    @BindView(R.id.fire_fighter)
    Button fireFighter;
    @BindView(R.id.emergency_name)
    EditText emergencyName;
    @BindView(R.id.emergency_number)
    EditText emergencyNumber;
    @BindView(R.id.firstname)
    EditText firstName;
    @BindView(R.id.lastname)
    EditText lastName;
    String emergencyNo = emergencyNumber.getText().toString();
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationSettingsRequest locationSettingsRequest;
    private android.location.Location location;
    private User user;
    private int typeOfEmergency;
    private Endpoint apiService;
    private ProgressDialog progressDialog;

    /**
     * Perform initialization of all fragments and loaders.
     *
     * @param savedInstanceState is a Bundle object containing the activity's previously saved state.
     *                           When the user clicks on appropriate emergency type, a progress dialog is displayed with the message
     *                           that the nearest helping unit is being searched for.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        user = (User) getIntent().getSerializableExtra("profile_data");
        Log.d(TAG, "onCreate: " + user.getAdhaarNumber());
        getLocation();
        /*
          Retrofit is a type-safe REST client for Android, used for interacting with the APIs and sending network requests
         */
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //Create an implementation of the API endpoints defined by the service interface.
        apiService = retrofit.create(Endpoint.class);
        progressDialog = new ProgressDialog(MainActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Finding Help...");
    }

    /**
     * When the activity enters the Resumed state, it comes to the foreground, and then the system invokes the onResume() callback
     * In this state, the user interacts with the app.
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void sendSMSmessage() {

        if (checkSelfPermission(Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)) {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            } else {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }



    /**
     * When an interruptive event occurs, the activity enters the Paused state, and the system invokes the onPause() callback.
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * checks if the user has turned on the location updates
     * If not, enable the location settings.
     */
    private int getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            mayRequestPermission();
            return -1;
        }
        if (googleApiClient == null) {
            buildGoogleClientApi();
            createLocationRequest();
            buildLocationSettingsRequest();
            googleApiClient.connect();
        }

        startLocationUpdates();
        return 0;
    }

    /**
     * The Google API Client provides a common entry point to all the Google Play services
     * and manages the network connection between the user's device and each Google service.
     */
    protected synchronized void buildGoogleClientApi() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();
    }

    /**
     * Checks if the user's location is on and permissions are granted for acquiring the location.
     * If yes, the location is acquired.
     * If no, an error message indicating the same is displayed.
     */
    protected void startLocationUpdates() {
        LocationServices.SettingsApi.checkLocationSettings(
                googleApiClient,
                locationSettingsRequest
        ).setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                            mayRequestPermission();
                            return;
                        }
                        LocationServices.FusedLocationApi.requestLocationUpdates(
                                googleApiClient, locationRequest, MainActivity.this);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        String errorMessage = "Location settings are inadequate, and cannot be " +
                                "fixed here. Fix in Settings.";
                        Log.e(TAG, errorMessage);
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @OnClick({R.id.ambulance, R.id.fire_fighter, R.id.police})
    public void notify(View view) {
        if (view.getId() == R.id.ambulance)
            typeOfEmergency = 2;
        else if (view.getId() == R.id.police)
            typeOfEmergency = 1;
        else if (view.getId() == R.id.fire_fighter)
            typeOfEmergency = 3;
        Log.d(TAG, "notify: " + typeOfEmergency);
        new EmergencyNotificationTask().execute();
    }

    /**
     * gets the latitude and longitude of the user's location
     */
    @Override
    public void onLocationChanged(android.location.Location location) {
        this.location = location;
        Log.d(TAG, "onLocationChanged: " + location.getLatitude() + " " + location.getLongitude());
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * on being connected, displays an appropriate message on the log
     *
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: Google client connected");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private boolean mayRequestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, getString(R.string.permission_rationale_location), Toast.LENGTH_LONG).show();
            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, REQUEST_ACCESS_LOCATION);
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
                getLocation();
            } else {
                Toast.makeText(this, getString(R.string.permission_rationale_location), Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.d(TAG, "User agreed to make required location settings changes.");
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(this, getString(R.string.permission_rationale_location), Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                }
                break;
        }
    }

    private void showProgress(boolean show) {
        if (show)
            progressDialog.show();
        else
            progressDialog.dismiss();
    }

    private class EmergencyNotificationTask extends AsyncTask<Object, Object, EmergencyPersonnel> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
            progressDialog.show();
        }

        @Override
        protected EmergencyPersonnel doInBackground(Object... voids) {

            //waiting for a location update
            waitForLocationUpdate();
            //creating the user's location update
            com.thunderstruck.nilanjan.cercatrova.support.Location userLocation;
            ArrayList<Double> coordinates = new ArrayList<>();
            coordinates.add(location.getLatitude());
            coordinates.add(location.getLongitude());
            userLocation = new com.thunderstruck.nilanjan.cercatrova.support.Location("Point", coordinates);
            user.setLocation(userLocation);
            String locationString = "POINT(" + location.getLatitude() + " " + location.getLongitude() + ")";
            final int status[] = new int[1];
            updateUserProfile(user.getEmailId(), locationString);
            //TODO consider moving code to new method
            //An emergency packet is created and sent to the web server.
            Emergency emergency = new Emergency(user.getAdhaarNumber(), typeOfEmergency, locationString);
            Call<EmergencyPersonnel> call = apiService.notifyEmergency(emergency);
            final EmergencyPersonnel[] personnel = new EmergencyPersonnel[1];
            status[0] = 0;
            call.enqueue(new Callback<EmergencyPersonnel>() {
                /**
                 * onResponse method is invoked for a received HTTP response.
                 * @param call creates a new, identical call to this one which can be enqueued
                 *             or executed even if this call has already been.
                 * @param response synchronously sends the request and returns its response.
                 * The personnel id is displayed on the log.
                 */
                @Override
                public void onResponse(Call<EmergencyPersonnel> call, Response<EmergencyPersonnel> response) {
                    personnel[0] = response.body();
                    if (personnel[0] != null)
                        Log.d(TAG, "onResponse: personnelID " + personnel[0].getPersonnelId());
                    status[0] = 1;
                }

                /**
                 * onFailure method is invoked when a network exception occurred talking to the server
                 * or when an unexpected exception occurred creating the request or processing the response.
                 */

                @Override
                public void onFailure(Call<EmergencyPersonnel> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                    personnel[0] = null;
                    status[0] = 1;
                }
            });
            while (status[0] == 0) {
                try {
                    Thread.sleep(1000);
                    Log.d(TAG, "doInBackground: null");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //gets the details of the personnel who is nearest to the user
            return personnel[0];
        }


        private void updateUserProfile(String userID, String location) {
             /*
            shared preferences allows to save and retrieve data in the form of key, value pair
            the first parameter is the key, second parameter is the mode
            the device id will be saved in the shared preferences.
         */
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preference_file), MODE_PRIVATE);
            String deviceID = "";
            while (deviceID.equals("")) {
                deviceID = sharedPreferences.getString("device_id", "");
                if (!deviceID.equals(""))
                    break;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // a packet consisting od user id, user's location and the device id is sent to the web server.
            UpdatePacket packet = new UpdatePacket(userID, location, deviceID);
            Call<User> updateProfile = apiService.updateProfile(packet);
            updateProfile.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.body() != null)
                        Log.d(TAG, "onResponse: Profile update" + response.body().getDeviceID());
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                }
            });

        }

        /**
         * waits for location updates
         */
        private void waitForLocationUpdate() {

            while (location == null) {
                try {
                    Thread.sleep(1000);
                    Log.d(TAG, "doInBackground: null");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * If help has been found, the user data and the emergency personnel's data are sent to the next GUI screen
         * Otherwise, a toast indicating failure is displayed.
         */
        @Override
        protected void onPostExecute(EmergencyPersonnel emergencyPersonnel) {
            super.onPostExecute(emergencyPersonnel);

            String message = firstName.getText().toString() + lastName.getText().toString() + "is in trouble. " +
                    "For accessing his/her location, click http://maps.google.com/?q=" + user.getLocation().getCoordinates().get(0)
                    + "," + user.getLocation().getCoordinates().get(1);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(emergencyNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.",
                    Toast.LENGTH_LONG).show();

            if (emergencyPersonnel != null) {
                Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                intent.putExtra("profile_data", user);
                intent.putExtra("emergency_responder", emergencyPersonnel);
                startActivity(intent);
                finish();
            } else {
                showProgress(false);
                Toast.makeText(MainActivity.this, "Failed to find help. Please Try again!", Toast.LENGTH_SHORT).show();
            }
        }

    }
}

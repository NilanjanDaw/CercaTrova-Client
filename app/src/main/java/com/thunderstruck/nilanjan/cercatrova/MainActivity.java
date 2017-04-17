package com.thunderstruck.nilanjan.cercatrova;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

/**
 * Created by nilanjan on 09-Apr-17.
 * Project CercaTrova
 */

//TODO refactor to SRS nomenclature
public class MainActivity extends AppCompatActivity implements LocationListener {

    public static final int REQUEST_ACCESS_LOCATION = 0;
    private static final String TAG = "MainActivity";
    @BindView(R.id.ambulance)
    Button ambulance;
    @BindView(R.id.police)
    Button police;
    @BindView(R.id.fire_fighter)
    Button fireFighter;
    LocationManager locationManager;
    android.location.Location location;
    User user;
    private int typeOfEmergency;
    private Endpoint apiService;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        user = (User) getIntent().getSerializableExtra("profile_data");
        Log.d(TAG, "onCreate: " + user.getAdhaarNumber());
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getLocation();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(Endpoint.class);
        progressDialog = new ProgressDialog(MainActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Finding Help...");
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            mayRequestLocation();
            return;
        }
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (location != null)
                Log.d(TAG, "getLocation: " + location.getLatitude() + " " + location.getLongitude());
            else
                Log.d(TAG, "getLocation: " + null);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 5, this);
        } else {
            requestGPS();
        }

    }

    private void requestGPS() {
        Toast.makeText(getBaseContext(), R.string.permission_rationale_location, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, REQUEST_ACCESS_LOCATION);
    }

    @OnClick({ R.id.ambulance, R.id.fire_fighter, R.id.police})
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

    @Override
    protected void onResume() {
        super.onResume();
        getLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        this.location = location;
        Log.d(TAG, "onLocationChanged: " + location.getLatitude() + " " + location.getLongitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

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
                getLocation();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ACCESS_LOCATION)
            getLocation();
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

            waitForLocationUpdate();

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
            Emergency emergency = new Emergency(user.getAdhaarNumber(), typeOfEmergency, locationString);
            Call<EmergencyPersonnel> call = apiService.notifyEmergency(emergency);
            final EmergencyPersonnel[] personnel = new EmergencyPersonnel[1];
            status[0] = 0;
            call.enqueue(new Callback<EmergencyPersonnel>() {
                @Override
                public void onResponse(Call<EmergencyPersonnel> call, Response<EmergencyPersonnel> response) {
                    personnel[0] = response.body();
                    if (personnel[0] != null)
                        Log.d(TAG, "onResponse: personnelID " + personnel[0].getPersonnelId());
                    status[0] = 1;
                }

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

            return personnel[0];
        }

        private void updateUserProfile(String userID, String location) {
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

        @Override
        protected void onPostExecute(EmergencyPersonnel emergencyPersonnel) {
            super.onPostExecute(emergencyPersonnel);
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

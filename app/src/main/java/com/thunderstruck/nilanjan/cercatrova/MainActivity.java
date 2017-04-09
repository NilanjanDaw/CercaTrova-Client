package com.thunderstruck.nilanjan.cercatrova;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.thunderstruck.nilanjan.cercatrova.support.User;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.ambulance) Button ambulance;
    @BindView(R.id.police) Button police;
    @BindView(R.id.fire_fighter) Button fireFighter;

    private int typeOfEmergency;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        User user = (User)getIntent().getSerializableExtra("profile_data");
        Log.d(TAG, "onCreate: " + user.getAdhaarNumber());
    }

    @OnClick({ R.id.ambulance, R.id.fire_fighter, R.id.police})
    public void notify(View view) {
        if (view.getId() == R.id.ambulance)
            typeOfEmergency = 1;
        else if (view.getId() == R.id.police)
            typeOfEmergency = 2;
        else if (view.getId() == R.id.fire_fighter)
            typeOfEmergency = 3;
        Log.d(TAG, "notify: " + typeOfEmergency);
    }
}

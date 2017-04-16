package com.thunderstruck.nilanjan.cercatrova.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nilan on 10-Apr-17.
 * Project CercaTrova
 */

public class Emergency {

    @SerializedName("user_adhaar_number")
    @Expose
    private String adhaarNumber;
    @SerializedName("emergency_type")
    @Expose
    private int emergencyType;
    @SerializedName("location")
    @Expose
    private String location;


    public Emergency(String adhaarNumber, int emergencyType, String location) {

        this.adhaarNumber = adhaarNumber;
        this.emergencyType = emergencyType;
        this.location = location;
    }

    public String getAdhaarNumber() {
        return adhaarNumber;
    }

    public int getEmergencyType() {
        return emergencyType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

package com.thunderstruck.nilanjan.cercatrova.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nilanjan on 17-Apr-17.
 * Project CercaTrova
 */

public class UpdatePacket {

    @SerializedName("user_id")
    @Expose
    private String userID;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("device_id")
    @Expose
    private String deviceID;

    public UpdatePacket(String userID, String location, String deviceID) {
        this.userID = userID;
        this.location = location;
        this.deviceID = deviceID;
    }
}

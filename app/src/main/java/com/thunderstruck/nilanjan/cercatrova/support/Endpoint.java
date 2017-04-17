package com.thunderstruck.nilanjan.cercatrova.support;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by nilanjan on 09-Apr-17.
 * Project CercaTrova
 */

public interface Endpoint {

    @POST("login_server/user/")
    Call<User> createUser(@Body User user);

    @POST("login_server/account_authentication/")
    Call<User> validateLogin(@Body AuthenticationPacket authenticationPacket);

    @POST("login_server/update/")
    Call<User> updateProfile(@Body UpdatePacket updatePacket);

    @POST("emergency/notify/")
    Call<EmergencyPersonnel> notifyEmergency(@Body Emergency emergency);

}

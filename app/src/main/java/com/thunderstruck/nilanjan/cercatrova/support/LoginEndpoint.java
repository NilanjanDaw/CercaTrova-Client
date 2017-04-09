package com.thunderstruck.nilanjan.cercatrova.support;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by nilanjan on 09-Apr-17.
 * Project CercaTrova
 */

public interface LoginEndpoint {

    @GET("user/")
    Call<User[]> getUserList();
    @POST("user/")
    Call<User> createUser(@Body User user);
    @POST("account_authentication/")
    Call<User> validateLogin(@Body AuthenticationPacket authenticationPacket);

}

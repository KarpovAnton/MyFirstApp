package com.karpov.vacuum.network;

import com.google.gson.JsonElement;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface VacuumApi {

    @Headers("Content-Type: application/json")
    @POST("/api/v1/account/logout/")
    Call<ResponseBody> logout(@Body JsonElement empty);

}

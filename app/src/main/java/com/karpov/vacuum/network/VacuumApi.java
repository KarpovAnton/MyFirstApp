package com.karpov.vacuum.network;

import com.karpov.vacuum.network.data.dto.IdRequestDto;
import com.karpov.vacuum.network.data.dto.LoginRequestDto;
import com.karpov.vacuum.network.data.dto.LoginResponseDto;
import com.karpov.vacuum.network.data.dto.RegistrationRequestDto;
import com.karpov.vacuum.network.data.dto.RegistrationResponseDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface VacuumApi {

    @Headers("Content-Type: application/json")
    @POST("/api/users/register")
    Call<RegistrationResponseDto> registration(@Body RegistrationRequestDto registrationRequestDto);

    @Headers("Content-Type: application/json")
    @POST("/api/oauth/token")
    Call<LoginResponseDto> login(@Body LoginRequestDto loginRequestDto);

    @Headers("Content-Type: application/json")
    @POST("/account/card/default/")
    Call<Void> sendResult(@Header("Authorization") String token, @Body IdRequestDto dto);

}

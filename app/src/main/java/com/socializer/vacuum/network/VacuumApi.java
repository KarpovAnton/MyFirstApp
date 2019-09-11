package com.socializer.vacuum.network;

import com.socializer.vacuum.network.data.dto.LoginRequestDto;
import com.socializer.vacuum.network.data.dto.LoginResponseDto;
import com.socializer.vacuum.network.data.dto.PhotoDeleteRequestDto;
import com.socializer.vacuum.network.data.dto.PhotoRequestDto;
import com.socializer.vacuum.network.data.dto.PhotoResponseDto;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto;
import com.socializer.vacuum.network.data.dto.ProfilesRequestDto;
import com.socializer.vacuum.network.data.dto.RegistrationRequestDto;
import com.socializer.vacuum.network.data.dto.RegistrationResponseDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface VacuumApi {

    @Headers("Content-Type: application/json")
    @POST("/api/users/register")
    Call<RegistrationResponseDto> registration(@Body RegistrationRequestDto registrationRequestDto);

    @Headers("Content-Type: application/json")
    @POST("/api/oauth/token")
    Call<LoginResponseDto> login(@Body LoginRequestDto loginRequestDto);

    @Headers("Content-Type: application/json")
    @POST("/api/users")
    Call<List<ProfilePreviewDto>> getProfiles(@Header("Authorization") String token, @Body ProfilesRequestDto dto);

    @Headers("Content-Type: application/json")
    @GET("/api/users/{id}")
    Call<List<ProfilePreviewDto>> getProfile(@Header("Authorization") String token, @Path("id") String id);

    @Headers("Content-Type: application/json")
    @PUT("/api/users")
    Call<PhotoResponseDto> uploadPhotoImage(@Header("Authorization") String token, @Body PhotoRequestDto dto);

    @Headers("Content-Type: application/json")
    @PUT("/api/users")
    Call<PhotoResponseDto> deletePhotoImage(@Header("Authorization") String token, @Body PhotoDeleteRequestDto dto);
}
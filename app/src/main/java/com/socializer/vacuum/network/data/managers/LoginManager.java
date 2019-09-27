package com.socializer.vacuum.network.data.managers;

import android.app.Application;

import androidx.annotation.NonNull;

import com.socializer.vacuum.network.VacuumApi;
import com.socializer.vacuum.network.data.AbstractManager;
import com.socializer.vacuum.network.data.DtoCallback;
import com.socializer.vacuum.network.data.FailTypes;
import com.socializer.vacuum.network.data.dto.ApiError;
import com.socializer.vacuum.network.data.dto.BindSocialRequestDto;
import com.socializer.vacuum.network.data.dto.LoginRequestDto;
import com.socializer.vacuum.network.data.dto.LoginResponseDto;
import com.socializer.vacuum.network.data.dto.LoginSocialRequestDto;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto;
import com.socializer.vacuum.network.data.dto.RegistrationRequestDto;
import com.socializer.vacuum.network.data.dto.RegistrationResponseDto;
import com.socializer.vacuum.network.data.dto.ResponseDto;
import com.socializer.vacuum.network.data.dto.UnBindSocialRequestDto;
import com.socializer.vacuum.network.data.prefs.AuthSession;
import com.socializer.vacuum.utils.ErrorUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

@Singleton
public class LoginManager extends AbstractManager {

    @Inject
    AuthSession authSession;

    @Inject
    LoginManager(VacuumApi vacuumApi, ErrorUtils errorUtils, Application application) {
        super(vacuumApi, errorUtils, application);
    }

    public void sendUsername(@NonNull String username,
                             @NonNull String password,
                             @NonNull final DtoCallback<?> callback) {

        if (!checkNetworkAvailable(callback)) return;

        Call<RegistrationResponseDto> call = mVacuumApi.registration(new RegistrationRequestDto(username, password));
        call.enqueue(new Callback<RegistrationResponseDto>() {
            @Override
            public void onResponse(Call<RegistrationResponseDto> call, Response<RegistrationResponseDto> response) {
                if (response.isSuccessful()) {
                    callback.onSuccessful(response.body());
                } else {
                    // parse the response body …
                    ApiError error = mErrorUtils.parseError(response);
                    if (error.getCode() == ApiError.AUTH_EXCEPTION_NO_FOUND) {
                        // TODO add fail types
                        // callback.onFailedCardAdding(FailTypes.LOGIN_USER_NOT_FOUND);
                        Timber.d("error message: %s", error.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<RegistrationResponseDto> call, Throwable t) {
                callback.onFailed(FailTypes.UNKNOWN_ERROR);
            }
        });

    }

    public void login(@NonNull String username,
                      @NonNull String password,
                      @NonNull final DtoCallback<?> callback) {

        if (!checkNetworkAvailable(callback)) return;

        Call<LoginResponseDto> loginConfirm = mVacuumApi.login(new LoginRequestDto(username, password));
        loginConfirm.enqueue(new Callback<LoginResponseDto>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponseDto> call, @NonNull Response<LoginResponseDto> response) {
                if (response.isSuccessful()) {
                    LoginResponseDto body = response.body();
                    if (body != null) {
                        String token = body.getAccessToken();
                        long ExpiresIn = body.getExpiresIn();
                        authSession.update(token, ExpiresIn);
                    }
                    callback.onSuccessful(ResponseDto.empty());
                } else {

                }
            }
            @Override
            public void onFailure(@NonNull Call<LoginResponseDto> call, @NonNull Throwable t) {
                callback.onFailed(FailTypes.UNKNOWN_ERROR);
            }
        });
    }

    public void sendVkData(@NonNull String userId,
                           @NonNull String accessToken,
                           @NonNull int expiresIn,
                           @NonNull final DtoCallback<?> callback) {

        if (!checkNetworkAvailable(callback)) return;

        Call<ProfilePreviewDto> loginConfirm = mVacuumApi.sendVkData(new LoginSocialRequestDto(userId, accessToken));
        loginConfirm.enqueue(new Callback<ProfilePreviewDto>() {
            @Override
            public void onResponse(@NonNull Call<ProfilePreviewDto> call, @NonNull Response<ProfilePreviewDto> response) {
                if (response.isSuccessful()) {
                    authSession.update(accessToken, expiresIn);
                    if (response.body() != null)
                        callback.onSuccessful(response.body());
                } else {

                }
            }
            @Override
            public void onFailure(@NonNull Call<ProfilePreviewDto> call, @NonNull Throwable t) {
                callback.onFailed(FailTypes.UNKNOWN_ERROR);
            }
        });
    }

    public void sendFbData(@NonNull String userId,
                           @NonNull String accessToken,
                           @NonNull long expiresIn,
                           @NonNull final DtoCallback<?> callback) {

        if (!checkNetworkAvailable(callback)) return;

        Call<ProfilePreviewDto> loginConfirm = mVacuumApi.sendFbData(new LoginSocialRequestDto(userId, accessToken));
        loginConfirm.enqueue(new Callback<ProfilePreviewDto>() {
            @Override
            public void onResponse(@NonNull Call<ProfilePreviewDto> call, @NonNull Response<ProfilePreviewDto> response) {
                if (response.isSuccessful()) {
                    /*LoginResponseDto body = response.body();
                    if (body != null) {
                        String token = body.getAccessToken();
                        long ExpiresIn = body.getExpiresIn();
                        authSession.update(token, ExpiresIn);
                    }*/
                    authSession.update(accessToken, expiresIn);
                    if (response.body() != null)
                        callback.onSuccessful(response.body());
                } else {

                }
            }
            @Override
            public void onFailure(@NonNull Call<ProfilePreviewDto> call, @NonNull Throwable t) {
                callback.onFailed(FailTypes.UNKNOWN_ERROR);
            }
        });
    }


    public void bindSocial(@NonNull int kind,
                           @NonNull String url,
                           @NonNull String oid,
                           @NonNull String access_token,
                           @NonNull final DtoCallback<?> callback) {

        Call<ProfilePreviewDto> loginConfirm = mVacuumApi.bindSocial(getTokenString(), new BindSocialRequestDto(kind, url, oid, access_token));
        loginConfirm.enqueue(new Callback<ProfilePreviewDto>() {
            @Override
            public void onResponse(@NonNull Call<ProfilePreviewDto> call, @NonNull Response<ProfilePreviewDto> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null)
                        callback.onSuccessful(response.body());
                } else {

                }
            }
            @Override
            public void onFailure(@NonNull Call<ProfilePreviewDto> call, @NonNull Throwable t) {

            }
        });

    }

    public void unBindSocial(@NonNull int kind,
                             @NonNull final DtoCallback<?> callback) {
        Call<ProfilePreviewDto> loginConfirm = mVacuumApi.unBindSocial(getTokenString(), new UnBindSocialRequestDto(kind));
        loginConfirm.enqueue(new Callback<ProfilePreviewDto>() {
            @Override
            public void onResponse(@NonNull Call<ProfilePreviewDto> call, @NonNull Response<ProfilePreviewDto> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null)
                        callback.onSuccessful(response.body());
                } else {

                }
            }
            @Override
            public void onFailure(@NonNull Call<ProfilePreviewDto> call, @NonNull Throwable t) {

            }
        });
    }
}

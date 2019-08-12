package com.karpov.vacuum.network.data.managers;

import android.app.Application;

import androidx.annotation.NonNull;

import com.karpov.vacuum.network.VacuumApi;
import com.karpov.vacuum.network.data.AbstractManager;
import com.karpov.vacuum.network.data.DtoCallback;
import com.karpov.vacuum.network.data.FailTypes;
import com.karpov.vacuum.network.data.dto.ApiError;
import com.karpov.vacuum.network.data.dto.LoginRequestDto;
import com.karpov.vacuum.network.data.dto.LoginResponseDto;
import com.karpov.vacuum.network.data.dto.RegistrationRequestDto;
import com.karpov.vacuum.network.data.dto.RegistrationResponseDto;
import com.karpov.vacuum.network.data.dto.ResponseDto;
import com.karpov.vacuum.network.data.prefs.AuthSession;
import com.karpov.vacuum.utils.ErrorUtils;

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
            public void onFailure(@NonNull Call<LoginResponseDto> call, @NonNull Throwable t) {
                callback.onFailed(FailTypes.UNKNOWN_ERROR);
            }
        });
    }
}

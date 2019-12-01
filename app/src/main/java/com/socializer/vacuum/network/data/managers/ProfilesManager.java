package com.socializer.vacuum.network.data.managers;

import android.app.Application;

import androidx.annotation.NonNull;

import com.socializer.vacuum.network.VacuumApi;
import com.socializer.vacuum.network.data.AbstractManager;
import com.socializer.vacuum.network.data.DtoCallback;
import com.socializer.vacuum.network.data.DtoListCallback;
import com.socializer.vacuum.network.data.FailTypes;
import com.socializer.vacuum.network.data.dto.PhotoDeleteRequestDto;
import com.socializer.vacuum.network.data.dto.PhotoRequestDto;
import com.socializer.vacuum.network.data.dto.PhotoResponseDto;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto;
import com.socializer.vacuum.network.data.dto.ProfilesRequestDto;
import com.socializer.vacuum.network.data.dto.RenameRequestDto;
import com.socializer.vacuum.utils.ErrorUtils;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.socializer.vacuum.utils.Consts.TOKEN_NOT_FOUND;
import static com.socializer.vacuum.utils.Consts.UNAUTHORIZED;

public class ProfilesManager extends AbstractManager {

    @Inject
    ProfilesManager(VacuumApi vacuumApi, ErrorUtils errorUtils, Application application) {
        super(vacuumApi, errorUtils, application);
    }

    public void getUserProfiles(@NonNull String[] userId, @NonNull final DtoListCallback<?> callback) {

        if (!checkNetworkAvailable(callback)) return;

        Call<List<ProfilePreviewDto>> call = mVacuumApi.getProfiles(getTokenString(), new ProfilesRequestDto(userId));
        call.enqueue(new Callback<List<ProfilePreviewDto>>() {
            @Override
            public void onResponse(Call<List<ProfilePreviewDto>> call, Response<List<ProfilePreviewDto>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccessful(response.body());
                } else {
                    if (response.code() == TOKEN_NOT_FOUND || response.code() == UNAUTHORIZED)
                    callback.onFailed(FailTypes.AUTH_REQUIRED);
                }
            }

            @Override
            public void onFailure(Call<List<ProfilePreviewDto>> call, Throwable t) {
                callback.onFailed(FailTypes.UNKNOWN_ERROR);
            }
        });
    }

    public void getProfile(@NonNull String userId, @NonNull final DtoListCallback<?> callback) {

        if (!checkNetworkAvailable(callback)) return;

        Call<List<ProfilePreviewDto>> call = mVacuumApi.getProfile(getTokenString(), userId);
        call.enqueue(new Callback<List<ProfilePreviewDto>>() {
            @Override
            public void onResponse(Call<List<ProfilePreviewDto>> call, Response<List<ProfilePreviewDto>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccessful(response.body());
                } else {
                    if (response.code() == TOKEN_NOT_FOUND || response.code() == UNAUTHORIZED)
                        callback.onFailed(FailTypes.AUTH_REQUIRED);
                }
            }

            @Override
            public void onFailure(Call<List<ProfilePreviewDto>> call, Throwable t) {
                callback.onFailed(FailTypes.UNKNOWN_ERROR);
            }
        });
    }

    public void renameAcc(@NonNull String name,
                          @NonNull final DtoCallback<?> callback) {

        if (!checkNetworkAvailable(callback)) return;

        Call<ProfilePreviewDto> loginConfirm = mVacuumApi.renameAcc(getTokenString(), new RenameRequestDto(name));
        loginConfirm.enqueue(new Callback<ProfilePreviewDto>() {
            @Override
            public void onResponse(@NonNull Call<ProfilePreviewDto> call, @NonNull Response<ProfilePreviewDto> response) {
                if (response.isSuccessful()) {
                    callback.onSuccessful(response.body());
                }
            }
            @Override
            public void onFailure(@NonNull Call<ProfilePreviewDto> call, @NonNull Throwable t) {
                callback.onFailed(FailTypes.UNKNOWN_ERROR);
            }
        });
    }

    public void uploadPhotoImage(String photoString, @NonNull final DtoCallback<?> callback) {

        if (!checkNetworkAvailable(callback)) return;

        Call<PhotoResponseDto> call = mVacuumApi.uploadPhotoImage(getTokenString(), new PhotoRequestDto(photoString));
        call.enqueue(new Callback<PhotoResponseDto>() {
            @Override
            public void onResponse(Call<PhotoResponseDto> call, Response<PhotoResponseDto> response) {
                if (response.isSuccessful()) {
                    callback.onSuccessful(response.body());
                } else {
                    if (response.code() == TOKEN_NOT_FOUND || response.code() == UNAUTHORIZED)
                        callback.onFailed(FailTypes.AUTH_REQUIRED);
                }
            }

            @Override
            public void onFailure(Call<PhotoResponseDto> call, Throwable t) {
                callback.onFailed(FailTypes.UNKNOWN_ERROR);
            }
        });
    }

    public void deletePhotoImage(String url, @NonNull final DtoCallback<?> callback) {

        if (!checkNetworkAvailable(callback)) return;

        Call<PhotoResponseDto> call = mVacuumApi.deletePhotoImage(getTokenString(), new PhotoDeleteRequestDto(url));
        call.enqueue(new Callback<PhotoResponseDto>() {
            @Override
            public void onResponse(Call<PhotoResponseDto> call, Response<PhotoResponseDto> response) {
                if (response.isSuccessful()) {
                    callback.onSuccessful(response.body());
                } else {
                    if (response.code() == TOKEN_NOT_FOUND || response.code() == UNAUTHORIZED)
                        callback.onFailed(FailTypes.AUTH_REQUIRED);
                }
            }

            @Override
            public void onFailure(Call<PhotoResponseDto> call, Throwable t) {
                callback.onFailed(FailTypes.UNKNOWN_ERROR);
            }
        });
    }
}

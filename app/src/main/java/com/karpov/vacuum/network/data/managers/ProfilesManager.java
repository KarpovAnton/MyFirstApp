package com.karpov.vacuum.network.data.managers;

import android.app.Application;

import androidx.annotation.NonNull;

import com.karpov.vacuum.network.VacuumApi;
import com.karpov.vacuum.network.data.AbstractManager;
import com.karpov.vacuum.network.data.DtoCallback;
import com.karpov.vacuum.network.data.DtoListCallback;
import com.karpov.vacuum.network.data.FailTypes;
import com.karpov.vacuum.network.data.dto.PhotoDeleteRequestDto;
import com.karpov.vacuum.network.data.dto.PhotoRequestDto;
import com.karpov.vacuum.network.data.dto.PhotoResponseDto;
import com.karpov.vacuum.network.data.dto.ProfilePreviewDto;
import com.karpov.vacuum.network.data.dto.ProfilesRequestDto;
import com.karpov.vacuum.utils.ErrorUtils;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ProfilesManager extends AbstractManager {

    @Inject
    ProfilesManager(VacuumApi vacuumApi, ErrorUtils errorUtils, Application application) {
        super(vacuumApi, errorUtils, application);
    }

    public void getUserProfiles(@NonNull String[] userId, @NonNull final DtoListCallback<?> callback) {

        //if (!checkNetworkAvailable(callback)) return;

        Call<List<ProfilePreviewDto>> call = mVacuumApi.getProfiles(getTokenString(), new ProfilesRequestDto(userId));
        call.enqueue(new Callback<List<ProfilePreviewDto>>() {
            @Override
            public void onResponse(Call<List<ProfilePreviewDto>> call, Response<List<ProfilePreviewDto>> response) {
                if (response.isSuccessful()) {

                    callback.onSuccessful(response.body());
                    Timber.d("moe manager succ");

                } else {
                    callback.onFailed(FailTypes.UNKNOWN_ERROR);
                    Timber.d("moe manager fail");
                }
            }

            @Override
            public void onFailure(Call<List<ProfilePreviewDto>> call, Throwable t) {
                callback.onFailed(FailTypes.UNKNOWN_ERROR);
                Timber.d("moe manager failure" + t.getLocalizedMessage());
            }
        });
    }

    public void getProfile(@NonNull String userId, @NonNull final DtoListCallback<?> callback) {

        //if (!checkNetworkAvailable(callback)) return;

        Call<List<ProfilePreviewDto>> call = mVacuumApi.getProfile(getTokenString(), userId);
        call.enqueue(new Callback<List<ProfilePreviewDto>>() {
            @Override
            public void onResponse(Call<List<ProfilePreviewDto>> call, Response<List<ProfilePreviewDto>> response) {
                if (response.isSuccessful()) {

                    callback.onSuccessful(response.body());
                    Timber.d("moe manager succ");

                } else {
                    callback.onFailed(FailTypes.UNKNOWN_ERROR);
                    Timber.d("moe manager fail");
                }
            }

            @Override
            public void onFailure(Call<List<ProfilePreviewDto>> call, Throwable t) {
                callback.onFailed(FailTypes.UNKNOWN_ERROR);
                Timber.d("moe manager failure" + t.getLocalizedMessage());
            }
        });
    }

    public void uploadPhotoImage(String photoString, @NonNull final DtoCallback<?> callback) {


        Call<PhotoResponseDto> call = mVacuumApi.uploadPhotoImage(getTokenString(), new PhotoRequestDto(photoString));
        call.enqueue(new Callback<PhotoResponseDto>() {
            @Override
            public void onResponse(Call<PhotoResponseDto> call, Response<PhotoResponseDto> response) {
                callback.onSuccessful(response.body());
            }

            @Override
            public void onFailure(Call<PhotoResponseDto> call, Throwable t) {
                Timber.i("onFailure=%s", t);
                callback.onFailed(FailTypes.UNKNOWN_ERROR);
            }
        });
    }

    public void deletePhotoImage(String url, @NonNull final DtoCallback<?> callback) {


        Call<PhotoResponseDto> call = mVacuumApi.deletePhotoImage(getTokenString(), new PhotoDeleteRequestDto(url));
        call.enqueue(new Callback<PhotoResponseDto>() {
            @Override
            public void onResponse(Call<PhotoResponseDto> call, Response<PhotoResponseDto> response) {
                callback.onSuccessful(response.body());
            }

            @Override
            public void onFailure(Call<PhotoResponseDto> call, Throwable t) {
                Timber.i("onFailure=%s", t);
                callback.onFailed(FailTypes.UNKNOWN_ERROR);
            }
        });
    }
}

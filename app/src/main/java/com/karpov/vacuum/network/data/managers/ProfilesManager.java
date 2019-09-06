package com.karpov.vacuum.network.data.managers;

import android.app.Application;

import androidx.annotation.NonNull;

import com.karpov.vacuum.network.VacuumApi;
import com.karpov.vacuum.network.data.AbstractManager;
import com.karpov.vacuum.network.data.DtoCallback;
import com.karpov.vacuum.network.data.DtoListCallback;
import com.karpov.vacuum.network.data.FailTypes;
import com.karpov.vacuum.network.data.dto.ProfilePreviewDto;
import com.karpov.vacuum.network.data.dto.ProfilesRequestDto;
import com.karpov.vacuum.utils.ErrorUtils;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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

    public void uploadPhotoImage(File coverFile, @NonNull final DtoCallback<?> callback) {
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), coverFile);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("image", coverFile.getName(), requestFile);

        Call<BookImageResponseDto> call = mBookSharingApi.uploadBookImage(getTokenString(), body);
        call.enqueue(new Callback<BookImageResponseDto>() {
            @Override
            public void onResponse(Call<BookImageResponseDto> call, Response<BookImageResponseDto> response) {
                callback.onSuccessful(response.body());
            }

            @Override
            public void onFailure(Call<BookImageResponseDto> call, Throwable t) {
                Timber.i("onFailure=%s", t);
                callback.onFailed(FailTypes.UNKNOWN_ERROR);
            }
        });
    }
}

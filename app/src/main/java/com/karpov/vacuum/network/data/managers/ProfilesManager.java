package com.karpov.vacuum.network.data.managers;

import android.app.Application;

import androidx.annotation.NonNull;

import com.karpov.vacuum.network.VacuumApi;
import com.karpov.vacuum.network.data.AbstractManager;
import com.karpov.vacuum.network.data.DtoListCallback;
import com.karpov.vacuum.network.data.FailTypes;
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

    //public void getImagesProfile()

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
}

package com.karpov.vacuum.activities.account;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.karpov.vacuum.VacuumApplication;
import com.karpov.vacuum.di.base.ActivityScoped;
import com.karpov.vacuum.network.data.DtoListCallback;
import com.karpov.vacuum.network.data.FailTypes;
import com.karpov.vacuum.network.data.dto.ProfilePreviewDto;
import com.karpov.vacuum.network.data.dto.ProfilePreviewDto.ProfileImageDto;
import com.karpov.vacuum.network.data.dto.ResponseDto;
import com.karpov.vacuum.network.data.managers.ProfilesManager;
import com.karpov.vacuum.utils.StringPreference;
import com.karpov.vacuum.views.adapters.PhotosAdapter;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import static com.karpov.vacuum.network.data.prefs.PrefsModule.NAMED_PREF_DEVICE_NAME;

@ActivityScoped
public class AccountPresenter implements AccountContract.Presenter {

    PhotosAdapter adapter;
    ProfilePreviewDto currentAccountDto;

    @Nullable
    AccountContract.View view;

    @Inject
    public AccountPresenter() {
        adapter = new PhotosAdapter(VacuumApplication.applicationContext);
    }

    @Inject
    ProfilesManager profilesManager;

    @Inject
    @Named(NAMED_PREF_DEVICE_NAME)
    StringPreference deviceNameSP;

    @Override
    public void takeView(AccountContract.View view) {
        this.view = view;
        this.view.setAdapter(adapter);

        loadAccount();
    }

    @Override
    public void dropView() {
        this.view = null;
    }

    private void loadAccount() {
        String userID = "";
        if (deviceNameSP != null)
             userID = deviceNameSP.get();
        userID = userID.split("@")[0];

        profilesManager.getProfile(userID, new DtoListCallback<ResponseDto>() {
            @Override
            public void onSuccessful(@NonNull List<ProfilePreviewDto> response) {
                if (!response.isEmpty()) {
                    currentAccountDto = response.get(0);

                    List<ProfileImageDto> photos = currentAccountDto.getImages();
                    if (photos.size() > 0) {
                        adapter.setPhotos(photos);
                    }

                    view.onAccountLoaded(currentAccountDto);
                }
            }

            @Override
            public void onFailed(FailTypes fail) {

            }
        });
    }
}
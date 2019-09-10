package com.socializer.vacuum.activities.account;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.socializer.vacuum.VacuumApplication;
import com.socializer.vacuum.di.base.ActivityScoped;
import com.socializer.vacuum.network.data.DtoListCallback;
import com.socializer.vacuum.network.data.FailTypes;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto.ProfileImageDto;
import com.socializer.vacuum.network.data.dto.ResponseDto;
import com.socializer.vacuum.network.data.managers.ProfilesManager;
import com.socializer.vacuum.utils.StringPreference;
import com.socializer.vacuum.views.adapters.PhotosAdapter;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import static com.socializer.vacuum.network.data.prefs.PrefsModule.NAMED_PREF_DEVICE_NAME;

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
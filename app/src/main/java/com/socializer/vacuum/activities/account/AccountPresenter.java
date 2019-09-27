package com.socializer.vacuum.activities.account;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.socializer.vacuum.VacuumApplication;
import com.socializer.vacuum.di.base.ActivityScoped;
import com.socializer.vacuum.network.data.DtoCallback;
import com.socializer.vacuum.network.data.DtoListCallback;
import com.socializer.vacuum.network.data.FailTypes;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto.ProfileAccountDto;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto.ProfileImageDto;
import com.socializer.vacuum.network.data.dto.ResponseDto;
import com.socializer.vacuum.network.data.managers.LoginManager;
import com.socializer.vacuum.network.data.managers.ProfilesManager;
import com.socializer.vacuum.network.data.prefs.AuthSession;
import com.socializer.vacuum.utils.StringPreference;
import com.socializer.vacuum.views.adapters.PhotosAdapter;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import static com.socializer.vacuum.activities.account.AccountActivity.VK;
import static com.socializer.vacuum.network.data.prefs.PrefsModule.NAMED_PREF_DEVICE_NAME;
import static com.socializer.vacuum.utils.Consts.BASE_DEVICE_NAME_PART;

@ActivityScoped
public class AccountPresenter implements AccountContract.Presenter {

    public static final String VK_BASE_URL = "https://www.vk.com/profile.php?id=";
    public static final String FB_BASE_URL = "https://www.facebook.com/profile.php?id=";
    public static final String INST_BASE_URL = "https://www.instagram.com/";

    @Inject
    @Named(NAMED_PREF_DEVICE_NAME)
    StringPreference deviceNameSP;

    PhotosAdapter adapter;
    ProfilePreviewDto currentAccountDto;

    @Nullable
    AccountContract.View view;

    @Inject
    AccountRouter router;

    @Inject
    LoginManager loginManager;

    @Inject
    public AccountPresenter() {
        adapter = new PhotosAdapter(VacuumApplication.applicationContext);
    }

    @Inject
    ProfilesManager profilesManager;

    @Override
    public void takeView(AccountContract.View view) {
        this.view = view;
        this.view.setAdapter(adapter);
    }

    @Override
    public void dropView() {
        this.view = null;
    }

    @Override
    public void loadAccount(String profileId) {
        profilesManager.getProfile(profileId, new DtoListCallback<ResponseDto>() {
            @Override
            public void onSuccessful(@NonNull List<ProfilePreviewDto> response) {
                if (!response.isEmpty()) {
                    currentAccountDto = (ProfilePreviewDto) response.get(0);

                    List<ProfileImageDto> photos = currentAccountDto.getImages();
                    if (photos != null) {
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

    @Override
    public void bindVK(String socialUserId, String accessToken) {
        loginManager.bindSocial(
                1,
                VK_BASE_URL.concat(socialUserId),
                socialUserId,
                accessToken,
                new DtoCallback<ResponseDto>() {
                    @Override
                    public void onSuccessful(@NonNull ResponseDto response) {
                        AuthSession.getInstance().setToken(accessToken);
                        ProfilePreviewDto result = (ProfilePreviewDto) response;
                        String newId = result.getUserId();
                        String deviceName = newId + BASE_DEVICE_NAME_PART;
                        deviceNameSP.set(deviceName);
                        loadAccount(newId);
                        if (view != null)
                            view.setIdFromSP();
                    }

                    @Override
                    public void onFailed(FailTypes fail) {

                    }
                });
    }

    @Override
    public void unBindVK() {
        loginManager.unBindSocial(1, new DtoCallback<ResponseDto>() {
            @Override
            public void onSuccessful(@NonNull ResponseDto response) {
                if (view != null)
                    view.onVkUnBind();
            }

            @Override
            public void onFailed(FailTypes fail) {

            }
        });

    }

    @Override
    public void openVKProfile() {
        List<ProfileAccountDto> accounts = currentAccountDto.getAccounts();
        for (int i = 0; i < accounts.size(); i++) {
            ProfileAccountDto acc = accounts.get(i);
            if (acc.getKind() == VK) {
                String url = acc.getUrl();
                String profileId = url.split("=")[1];
                router.openVKProfile(profileId);
                return;
            }
        }
    }
}
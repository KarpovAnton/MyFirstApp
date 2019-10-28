package com.socializer.vacuum.activities.account;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.socializer.vacuum.di.base.BasePresenter;
import com.socializer.vacuum.di.base.BaseView;
import com.socializer.vacuum.network.data.FailTypes;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto;

public interface AccountContract {

    interface Router {

        void openPhotoActivity(String[] photoArray);

        void openVKProfile(String profileId);

        void openFBProfile(String profileId);

        void openINSTProfile(String profileId);
    }

    interface View extends BaseView<Presenter> {

        void setAdapter(PagerAdapter adapter);

        void onAccountLoaded(ProfilePreviewDto currentAccountDto);

        void setAccountIdFromSP();

        void onSocUnBind(int kind);

        void showErrorNetworkDialog(FailTypes fail);

        void onSocialBinded();
    }

    interface Presenter extends BasePresenter<View> {

        void takeView(AccountContract.View view);

        void dropView();

        void loadAccount(String profileId);

        void bindSocial(int kind, String socialUserId, String accessToken, @Nullable String username);

        void unBindSocial(int kind);

        void openVKProfile();

        void openFBProfile();

        void openInstProfile();
    }
}

package com.socializer.vacuum.activities.account;

import androidx.viewpager.widget.PagerAdapter;

import com.socializer.vacuum.di.base.BasePresenter;
import com.socializer.vacuum.di.base.BaseView;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto;

public interface AccountContract {

    interface Router {

        void openPhotoActivity(String[] photoArray);

        void openVKProfile(String profileId);
    }

    interface View extends BaseView<Presenter> {

        void setAdapter(PagerAdapter adapter);

        void onAccountLoaded(ProfilePreviewDto currentAccountDto);

        void setIdFromSP();

        void onVkUnBind();
    }

    interface Presenter extends BasePresenter<View> {

        void takeView(AccountContract.View view);

        void dropView();

        void loadAccount(String profileId);

        void bindVK(String socialUserId, String accessToken);

        void unBindVK();

        void openVKProfile();
    }
}

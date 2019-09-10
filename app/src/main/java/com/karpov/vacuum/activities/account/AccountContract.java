package com.karpov.vacuum.activities.account;

import androidx.viewpager.widget.PagerAdapter;

import com.karpov.vacuum.di.base.BasePresenter;
import com.karpov.vacuum.di.base.BaseView;
import com.karpov.vacuum.network.data.dto.ProfilePreviewDto;

public interface AccountContract {

    interface Router {

        void openPhotoActivity(String[] photoArray);
    }

    interface View extends BaseView<Presenter> {

        void setAdapter(PagerAdapter adapter);

        void onAccountLoaded(ProfilePreviewDto currentAccountDto);
    }

    interface Presenter extends BasePresenter<View> {

        void takeView(AccountContract.View view);

        void dropView();
    }
}

package com.socializer.vacuum.activities.main;

import android.bluetooth.le.AdvertiseCallback;

import androidx.recyclerview.widget.RecyclerView;

import com.socializer.vacuum.di.base.BasePresenter;
import com.socializer.vacuum.di.base.BaseView;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto;

public interface MainContract {

    interface Router {
        void openProfile(ProfilePreviewDto profileDto);

        void removeFragment();

        void openAccountActivity();

        void openChatListActivity();
    }

    interface View extends BaseView<Presenter> {
        void setAdapter(RecyclerView.Adapter adapter);

        void onProfileSelected(ProfilePreviewDto previewDto);

        void refreshed();
    }

    interface Presenter extends BasePresenter<View> {
        void takeView(MainContract.View view);

        void dropView();

        void refresh();

        boolean isBlueEnable();

        void startAdvertising(AdvertiseCallback advertiseCallback);

        void startScan();

        void loadTestProfiles();

        void clearAdapter();

        void setBtName();
    }
}

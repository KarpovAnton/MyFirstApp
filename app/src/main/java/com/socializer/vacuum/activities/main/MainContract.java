package com.socializer.vacuum.activities.main;

import android.app.Activity;
import android.bluetooth.le.AdvertiseCallback;

import androidx.recyclerview.widget.RecyclerView;

import com.socializer.vacuum.di.base.BasePresenter;
import com.socializer.vacuum.di.base.BaseView;
import com.socializer.vacuum.network.data.FailTypes;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto;

public interface MainContract {

    interface Router {
        void openProfile(ProfilePreviewDto profileDto);

        void removeFragments();

        void openAccountActivity();

        void openChatListActivity();
    }

    interface View extends BaseView<Presenter> {
        void setAdapter(RecyclerView.Adapter adapter);

        void onProfileSelected(ProfilePreviewDto previewDto);

        void refreshed();

        void showErrorDialog(FailTypes fail);

        void showSingleItem(ProfilePreviewDto profileDto);

        void hideSingleItem();
    }

    interface Presenter extends BasePresenter<View> {
        void takeView(MainContract.View view);

        void dropView();

        void refresh();

        void startScan();

        void loadTestProfiles();

        void clearAdapter();

        void setBtName();

        void startAdvertise(AdvertiseCallback callback);

        boolean isBlueEnable(Activity activity);
    }
}

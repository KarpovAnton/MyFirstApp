package com.karpov.vacuum.activities.photo;

import com.karpov.vacuum.di.base.BasePresenter;
import com.karpov.vacuum.di.base.BaseView;
import com.karpov.vacuum.views.adapters.PhotoEditAdapter;

import java.util.ArrayList;

public interface PhotoContract {

    interface Router {

    }

    interface View extends BaseView<Presenter> {

        void setAdapter(PhotoEditAdapter adapter);

        void onRemoveImage(int pos);

        void onPhotoUploaded();
    }

    interface Presenter extends BasePresenter<View> {

        void takeView(PhotoContract.View view);

        void dropView();

        void setUpAdapter();

        void setPhotos(ArrayList<String> photoImages);

        void sendPhotoImage(String photoString, String uri);
    }
}

package com.karpov.vacuum.activities.photo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.karpov.vacuum.di.base.ActivityScoped;
import com.karpov.vacuum.models.PhotoEditItem;
import com.karpov.vacuum.network.data.DtoCallback;
import com.karpov.vacuum.network.data.FailTypes;
import com.karpov.vacuum.network.data.dto.ResponseDto;
import com.karpov.vacuum.network.data.managers.ProfilesManager;
import com.karpov.vacuum.views.adapters.PhotoEditAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@ActivityScoped
public class PhotoPresenter implements PhotoContract.Presenter, PhotoEditAdapter.Callback {

    @Nullable
    PhotoContract.View view;

    @Inject
    ProfilesManager profilesManager;

    @Inject
    public PhotoPresenter() {
        adapter = new PhotoEditAdapter(this);
    }

    PhotoEditAdapter adapter;

    List<PhotoEditItem> photoList = new ArrayList<>();

    @Override
    public void takeView(PhotoContract.View view) {
        this.view = view;
        this.view.setAdapter(adapter);
    }

    @Override
    public void dropView() {
        this.view = null;
    }

    @Override
    public void setUpAdapter() {
        adapter.onRemoveAll();
        adapter.onAddAll(photoList);
    }

    @Override
    public void setPhotos(ArrayList<String> photoImages) {
        for (String image : photoImages) {
            photoList.add(new PhotoEditItem(image));
        }
        adapter.onRemoveAll();
        adapter.onAddAll(photoList);
    }

    @Override
    public void sendPhotoImage(File photoFile, String uri) {
        final PhotoEditItem item = new PhotoEditItem(uri, true);
        adapter.onAdd(item);

        profilesManager.uploadPhotoImage(photoFile, new DtoCallback<ResponseDto>() {
            @Override
            public void onSuccessful(@NonNull ResponseDto response) {
                item.setLoading(false);
                adapter.notifyItemChanged(adapter.getItemCount()-2);
                if (view != null)
                    view.onCoverImageUploaded(((BookImageResponseDto)response).getUrl());
            }

            @Override
            public void onFailed(FailTypes fail) {

            }
        });
    }

    @Override
    public void onClickDeleteImage(int pos) {
        adapter.onRemove(pos);
        if (view != null)
            view.onRemoveImage(pos);
    }

    @Override
    public void onClickCancelUploadImage(int pos) {
        adapter.onRemove(pos);
    }
}
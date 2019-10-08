package com.socializer.vacuum.activities.photo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.socializer.vacuum.di.base.ActivityScoped;
import com.socializer.vacuum.models.PhotoEditItem;
import com.socializer.vacuum.network.data.DtoCallback;
import com.socializer.vacuum.network.data.FailTypes;
import com.socializer.vacuum.network.data.dto.PhotoResponseDto;
import com.socializer.vacuum.network.data.dto.ResponseDto;
import com.socializer.vacuum.network.data.managers.ProfilesManager;
import com.socializer.vacuum.views.adapters.PhotoEditAdapter;

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
    public void sendPhotoImage(String photoString, String uri) {
        final PhotoEditItem item = new PhotoEditItem(uri, true);
        adapter.onAdd(item);

        profilesManager.uploadPhotoImage(photoString, new DtoCallback<ResponseDto>() {
            @Override
            public void onSuccessful(@NonNull ResponseDto response) {
                item.setLoading(false);
                adapter.notifyItemChanged(adapter.getItemCount() - 1);
                PhotoResponseDto currentDto = (PhotoResponseDto)response;
                //TODO proverit
                //item.setUri(currentDto.getImages().get(adapter.getItemCount() - 1).getUrl());//URL перезаписываем URI для возможности удаления, не уходя с активити

                if (view != null)
                    view.onPhotoUploaded();
            }

            @Override
            public void onFailed(FailTypes fail) {
                if (view != null)
                    view.showErrorNetworkDialog(fail);
            }
        });
    }

    @Override
    public void onClickDeleteImage(int pos) {
        String deletePhotoUrl = adapter.getUrlPhotoByPos(pos);
        profilesManager.deletePhotoImage(deletePhotoUrl, new DtoCallback<ResponseDto>() {
            @Override
            public void onSuccessful(@NonNull ResponseDto response) {

            }

            @Override
            public void onFailed(FailTypes fail) {
                if (view != null)
                    view.showErrorNetworkDialog(fail);
            }
        });

        adapter.onRemove(pos);
        /*if (view != null)
            view.onRemoveImage(pos);*/
    }

    @Override
    public void onClickCancelUploadImage(int pos) {
        adapter.onRemove(pos);
    }
}
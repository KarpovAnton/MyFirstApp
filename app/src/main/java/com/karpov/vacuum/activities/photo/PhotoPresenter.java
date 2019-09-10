package com.karpov.vacuum.activities.photo;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.karpov.vacuum.VacuumApplication;
import com.karpov.vacuum.di.base.ActivityScoped;
import com.karpov.vacuum.models.PhotoEditItem;
import com.karpov.vacuum.network.data.DtoCallback;
import com.karpov.vacuum.network.data.FailTypes;
import com.karpov.vacuum.network.data.dto.PhotoResponseDto;
import com.karpov.vacuum.network.data.dto.ResponseDto;
import com.karpov.vacuum.network.data.managers.ProfilesManager;
import com.karpov.vacuum.views.adapters.PhotoEditAdapter;

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
                PhotoResponseDto currentDto = (PhotoResponseDto)response;
                item.setUri(currentDto.getImages().get(0).getUrl());//URL перезаписываем URI для возможности удаления, не уходя с активити
                adapter.notifyItemChanged(adapter.getItemCount() - 1);

                if (view != null)
                    view.onPhotoUploaded(/*((PhotoResponseDto)response).getImages().*/);//TODO delete
            }

            @Override
            public void onFailed(FailTypes fail) {

            }
        });
    }

    @Override
    public void onClickDeleteImage(int pos) {
        String deletePhotoUrl = adapter.getUrlPhotoByPos(pos);
        profilesManager.deletePhotoImage(deletePhotoUrl, new DtoCallback<ResponseDto>() {
            @Override
            public void onSuccessful(@NonNull ResponseDto response) {
                Toast.makeText(VacuumApplication.applicationContext, "URA DELETE", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(FailTypes fail) {

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
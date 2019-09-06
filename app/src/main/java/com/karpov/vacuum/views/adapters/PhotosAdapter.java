package com.karpov.vacuum.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.karpov.vacuum.R;
import com.karpov.vacuum.network.data.dto.ProfilePreviewDto.ProfileImageDto;
import com.karpov.vacuum.network.data.prefs.AuthSession;
import com.karpov.vacuum.utils.ImageUtils;

import java.util.List;

public class PhotosAdapter extends PagerAdapter {

    Context context;
    List<ProfileImageDto> photos;

    ImageView photoImage;

    public PhotosAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        if (photos != null) {
            return photos.size();
        } else {
            return 0;
        }
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.pager_item, null);
        photoImage = view.findViewById(R.id.photoImage);
        if (photos != null && !photos.isEmpty()) {
            ProfileImageDto avatar = photos.get(position);
            setPhoto(avatar.getPreview(), avatar.getUrl());
        } else {
            setPhotoPlaceholder();
        }
        container.addView(view);
        return view;
    }

    private void setPhoto(String preview, String url) {
        new ImageUtils().setAuthImage(context, getTokenString(), photoImage, url, preview,
                R.drawable.default_avatar);
    }

    private void setPhotoPlaceholder() {
        new ImageUtils().setImage(photoImage, null, null,
                R.drawable.default_avatar);
    }

    private String getTokenString() {
        return "Bearer " + AuthSession.getInstance().getToken();
    }

    public void setPhotos(List<ProfileImageDto> photos) {
        if (photos != null && !photos.isEmpty()) {
            this.photos = photos;
            notifyDataSetChanged();
        }
    }
}

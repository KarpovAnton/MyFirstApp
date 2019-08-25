package com.karpov.vacuum.views.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.karpov.vacuum.R;
import com.karpov.vacuum.network.data.dto.ProfilePreviewDto;
import com.karpov.vacuum.network.data.dto.ProfilePreviewDto.ProfileAccountDto;
import com.karpov.vacuum.network.data.dto.ProfilePreviewDto.ProfileImageDto;
import com.karpov.vacuum.network.data.prefs.AuthSession;
import com.karpov.vacuum.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ProfileViewHolder extends RecyclerView.ViewHolder {

    Context context;

    @BindView(R.id.avatarImage)
    ImageView avatarImage;

    @BindView(R.id.nameText)
    TextView nameText;

    public ProfileViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = itemView.getContext();
    }

    private String getTokenString() {
        return "Bearer " + AuthSession.getInstance().getToken();
    }

    public void bind(ProfilePreviewDto item) {
        List<ProfileImageDto> photos = item.getImages();
        List<ProfileAccountDto> accounts = item.getAccounts();

        if (!photos.isEmpty()) {
            ProfileImageDto avatar = photos.get(0);
            setAvatar(avatar.getPreview(), avatar.getUrl());
        } else {
            setAvatarPlaceholder();
        }

        setName(item.getUsername());
        Timber.d("moe" + getTokenString());
    }

    private void setAvatarPlaceholder() {
        new ImageUtils().setImage(avatarImage, null, null,
                R.drawable.default_avatar);
    }

    private void setAvatar(String preview, String url) {
        new ImageUtils().setAuthImage(context, getTokenString(), avatarImage, url, preview,
                R.drawable.default_avatar);
    }

    private void setName(String username) {
        nameText.setText(username);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        itemView.setOnClickListener(listener);
    }
}

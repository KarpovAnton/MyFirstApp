package com.socializer.vacuum.views.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.socializer.vacuum.R;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto.ProfileAccountDto;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto.ProfileImageDto;
import com.socializer.vacuum.network.data.prefs.AuthSession;
import com.socializer.vacuum.utils.ImageUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileViewHolder extends RecyclerView.ViewHolder {

    Context context;

    @BindView(R.id.avatarImage)
    ImageView avatarImage;

    @BindView(R.id.statusImage)
    ImageView statusImage;

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

        if (photos != null && !photos.isEmpty()) {
            ProfileImageDto avatar = photos.get(0);
            setAvatar(avatar.getPreview(), avatar.getUrl());
        } else {
            setAvatarPlaceholder();
        }

        setStatus(item.getStatus());
        setName(item.getUsername());
    }

    private void setAvatarPlaceholder() {
        new ImageUtils().setImage(avatarImage, null, null,
                R.drawable.default_avatar);
    }

    private void setAvatar(String preview, String url) {
        new ImageUtils().setAuthCircleImage(context, avatarImage, url, preview,
                R.drawable.default_avatar);
    }

    private void setStatus(int status) {
        if (status == 1) {
            new ImageUtils().setImage(statusImage, null, null, R.drawable.online);
        } else {
            new ImageUtils().setImage(statusImage, null, null, R.drawable.offline);
        }
    }

    private void setName(String username) {
        nameText.setText(username);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        itemView.setOnClickListener(listener);
    }
}

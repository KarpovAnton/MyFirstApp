package com.karpov.vacuum.views.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.karpov.vacuum.R;
import com.karpov.vacuum.models.PhotoEditItem;
import com.karpov.vacuum.network.data.prefs.AuthSession;
import com.karpov.vacuum.utils.ImageUtils;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoEditViewHolder extends RecyclerView.ViewHolder {

    private Context context;

    @BindView(R.id.photoImage)
    ImageView photoImage;

    @BindView(R.id.deleteButton)
    ImageView deleteButton;

    @BindView(R.id.cancelButton)
    ImageView cancelButton;

    @BindView(R.id.progressBar)
    CircularProgressBar progressBar;

    public PhotoEditViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = itemView.getContext();
    }

    public void bind(PhotoEditItem item) {
        setCoverImage(item.getUri());
        updateViewByState(item.isLoading());
    }

    public void setCoverImage(String url) {
        new ImageUtils().setAuthImage(context, getTokenString(), photoImage, url, null,
                R.drawable.default_avatar);
    }

    private String getTokenString() {
        return "Bearer " + AuthSession.getInstance().getToken();
    }

    public void setProgress(int progress) {
        progressBar.setProgress(progress);
    }

    void updateViewByState(boolean isLoading) {
        cancelButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.VISIBLE);
        }
    }

    public void setDeleteClickListener(View.OnClickListener listener) {
        deleteButton.setOnClickListener(listener);
    }

    public void setCancelClickListener(View.OnClickListener listener) {
        cancelButton.setOnClickListener(listener);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        itemView.setOnClickListener(listener);
    }
}

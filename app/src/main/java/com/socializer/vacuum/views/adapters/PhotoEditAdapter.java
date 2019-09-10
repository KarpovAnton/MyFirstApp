package com.socializer.vacuum.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.socializer.vacuum.R;
import com.socializer.vacuum.models.PhotoEditItem;
import com.socializer.vacuum.views.viewholders.PhotoEditViewHolder;

public class PhotoEditAdapter extends BaseAdapter<PhotoEditItem, RecyclerView.ViewHolder> {

    private Callback listener;

    public PhotoEditAdapter(Callback listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder result;
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.viewholder_photo_edit, viewGroup, false);
        result = new PhotoEditViewHolder(view);
        return result;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        PhotoEditViewHolder photoEditViewHolder = (PhotoEditViewHolder) viewHolder;
        photoEditViewHolder.bind(items.get(i));
        photoEditViewHolder.setDeleteClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickDeleteImage(i);
            }
        });
        photoEditViewHolder.setCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickCancelUploadImage(i);
            }
        });

        photoEditViewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public String getUrlPhotoByPos(int pos) {
        return items.get(pos).getUri();
    }

    public interface Callback {
        void onClickDeleteImage(int pos);
        void onClickCancelUploadImage(int pos);
    }
}

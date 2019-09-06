package com.karpov.vacuum.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.karpov.vacuum.R;
import com.karpov.vacuum.models.PhotoEditItem;
import com.karpov.vacuum.views.viewholders.PhotoEditViewHolder;

import java.util.List;

public class PhotoEditAdapter extends BaseAdapter<PhotoEditItem, RecyclerView.ViewHolder> {

    private Callback listener;

    public PhotoEditAdapter(Callback listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder result;
        View viewCover = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.viewholder_photo_edit, viewGroup, false);
        result = new PhotoEditViewHolder(viewCover);
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

    @Override
    public void onAdd(PhotoEditItem item) {
        int position = getItemCount()-1;
        this.items.remove(position);
        this.items.add(item);
        this.items.add(new PhotoEditItem());
        notifyItemInserted(position);
    }

    @Override
    public void onAddAll(List<PhotoEditItem> items) {
        super.onAddAll(items);
        this.items.add(new PhotoEditItem());
    }

    public void onRemove(int pos) {
        items.remove(pos);
        notifyItemRemoved(pos);
    }

    @Override
    public void onRemoveAll() {
        super.onRemoveAll();
    }

    public interface Callback {
        void onClickDeleteImage(int pos);
        void onClickCancelUploadImage(int pos);
    }
}

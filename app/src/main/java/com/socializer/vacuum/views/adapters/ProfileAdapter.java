package com.socializer.vacuum.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.socializer.vacuum.R;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto;
import com.socializer.vacuum.views.viewholders.ProfileViewHolder;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    RecyclerItemClickListener mItemClickListener;

    List<ProfilePreviewDto> items;

    public ProfileAdapter(RecyclerItemClickListener itemClickListener) {
        super();
        mItemClickListener = itemClickListener;
        items = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        ProfileViewHolder holder;
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.viewholder_profile, viewGroup, false);
        holder = new ProfileViewHolder(view);
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    mItemClickListener.onClick(adapterPosition);
                }
            }
        });
        Timber.d("moe create ");
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ProfileViewHolder viewHolder = (ProfileViewHolder)holder;
        viewHolder.bind(items.get(position));
        /*viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.onClick(position);
            }
        });*/

        Timber.d("moe bind profile " + position);
    }

    @Override
    public int getItemCount() {
        Timber.d("getItemCount() %s", items.size());
        return items.size();
    }

    public void onAddList(List<ProfilePreviewDto> dtoList) {
        int initPosition = 0;

        // Remove loading indicator if exists
        /*if (items.size()>0) {
            initPosition = items.size() - 1;
            items.remove(initPosition);
            notifyItemRemoved(initPosition);
            Timber.d("notifyItemRemoved %d", initPosition);
        }*/

        items.addAll(dtoList);
        int updateCount = dtoList.size();
/*        if (hasNext) {
            items.add(loadingItem);
            updateCount++;
        }*/
        notifyItemRangeInserted(initPosition, updateCount);
    }

    public void onAdd(List<ProfilePreviewDto> dtoList) {
        int position = getItemCount();
        this.items.add(dtoList.get(0));
        notifyItemInserted(position);
    }

    public String getUserIdByPosition(int position) {
        ProfilePreviewDto item = items.get(position);
        return item.getUserId();
    }

    public ProfilePreviewDto getProfileByPosition(int position) {
        return items.get(position);

    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }
}


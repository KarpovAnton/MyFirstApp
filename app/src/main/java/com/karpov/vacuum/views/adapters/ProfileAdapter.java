package com.karpov.vacuum.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.karpov.vacuum.R;
import com.karpov.vacuum.commons.adapter.ViewType;
import com.karpov.vacuum.network.data.dto.ProfilePreviewDto;
import com.karpov.vacuum.views.viewholders.ProfileViewHolder;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_PROFILE = 1;
    public static final int TYPE_LOADER = 2;

    LoaderDelegateAdapter mLoadingDelegateAdapter;

    List<ViewType> items;

    protected boolean hasNext;

    protected ViewType loadingItem = new ViewType() {
        @Override
        public int getViewType() {
            return TYPE_LOADER;
        }
    };

    public ProfileAdapter() {
        super();
        mLoadingDelegateAdapter = new LoaderDelegateAdapter();
        items = new ArrayList<>();
        hasNext = true;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder result;
        switch (viewType) {
            case TYPE_PROFILE:
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.viewholder_profile, viewGroup, false);
                result = new ProfileViewHolder(view);
                break;

            case TYPE_LOADER:
                result = mLoadingDelegateAdapter.onCreateViewHolder(viewGroup);
                break;
            default:
                throw new IllegalStateException("unknown type");
        }
        return result;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        int itemViewType = getItemViewType(position);
        switch (itemViewType) {
            case TYPE_PROFILE:
                ProfileViewHolder viewHolder = (ProfileViewHolder)holder;
                viewHolder.bind((ProfilePreviewDto) items.get(position));
                Timber.d("bind profile");
                break;

            case TYPE_LOADER:
                mLoadingDelegateAdapter.onBindViewHolder(holder, items.get(position));
                Timber.d("bind loader");
                break;
            default:
                throw new IllegalStateException("unknown type");
        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        Timber.d("getItemCount() %s", items.size());
        return items.size();
    }

    public void onAddList(List<ProfilePreviewDto> dtoList, boolean hasNext) {
        int initPosition = 0;
        this.hasNext = hasNext;

        // Remove loading indicator if exists
        if (items.size()>0) {
            initPosition = items.size() - 1;
            items.remove(initPosition);
            notifyItemRemoved(initPosition);
            Timber.d("notifyItemRemoved %d", initPosition);
        }

        items.addAll(dtoList);
        int updateCount = dtoList.size();
        if (hasNext) {
            items.add(loadingItem);
            updateCount++;
        }
        notifyItemRangeInserted(initPosition, updateCount);
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }
}


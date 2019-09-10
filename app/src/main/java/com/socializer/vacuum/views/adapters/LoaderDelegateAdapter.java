package com.socializer.vacuum.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.socializer.vacuum.R;
import com.socializer.vacuum.commons.adapter.ViewType;
import com.socializer.vacuum.commons.adapter.ViewTypeDelegateAdapter;

public class LoaderDelegateAdapter implements ViewTypeDelegateAdapter {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_item_loader, parent, false);
        return new LoadingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, ViewType item) {
        //do nothing
    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }
}
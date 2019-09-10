package com.socializer.vacuum.commons.adapter;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public interface ViewTypeDelegateAdapter {

    RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent);

    void onBindViewHolder(RecyclerView.ViewHolder holder, ViewType item);

}

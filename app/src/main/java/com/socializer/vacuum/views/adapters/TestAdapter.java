package com.socializer.vacuum.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.socializer.vacuum.R;
import com.socializer.vacuum.views.viewholders.TestVH;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class TestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<String> items;

    public TestAdapter() {
        items = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_test, parent, false);
        return new TestVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TestVH testVH = (TestVH) holder;
        testVH.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        Timber.d("getItemCount() %s", items.size());
        return items.size();
    }

    public void onAddAll(List<String> deviceNames) {
        if (items.size()>0)
            items.clear();

        items.addAll(deviceNames);
        notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }
}

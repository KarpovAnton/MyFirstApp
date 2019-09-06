package com.karpov.vacuum.views.adapters;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAdapter<T, V extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<V> implements AdapterInterface<T> {

    List<T> items;

    public BaseAdapter() {
        super();
        this.items = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onAdd(T item) {
        int position = getItemCount();
        this.items.add(item);
        notifyItemInserted(position);
    }

    @Override
    public void onAddAll(List<T> items) {
        int positionStart = getItemCount();
        int itemCount = items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(positionStart-1, itemCount);
    }

    @Override
    public void onRemove(int pos) {
        items.remove(pos);
        notifyItemRemoved(pos);
    }

    @Override
    public void onRemoveAll() {
        items.clear();
        notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }
}

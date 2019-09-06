package com.karpov.vacuum.views.adapters;

import java.util.List;

public interface AdapterInterface<T> {
    void onAdd(T item);
    void onAddAll(List<T> items);
    void onRemove(int pos);
    void onRemoveAll();
}

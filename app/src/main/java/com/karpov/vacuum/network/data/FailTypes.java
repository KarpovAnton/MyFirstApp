package com.karpov.vacuum.network.data;

import com.karpov.vacuum.R;

public enum FailTypes {
    CONNECTION_ERROR(R.string.app_name),
    UNKNOWN_ERROR(R.string.app_name),
    AUTH_REQUIRED(R.string.app_name);

    private final int mResId;

    FailTypes(int resId) {
        mResId = resId;
    }
    public int getResId() {
        return mResId;
    }
}
package com.karpov.vacuum.activities.account;

import android.content.Intent;

import com.karpov.vacuum.activities.photo.PhotoActivity;

import javax.inject.Inject;

public class AccountRouter implements AccountContract.Router {
    @Inject
    AccountActivity activity;

    @Inject
    public AccountRouter() {

    }

    public void openPhotoActivity(String[] photoArray) {
        Intent intent = new Intent(activity.getApplicationContext(), PhotoActivity.class);
        intent.putExtra("imageArray", photoArray);
        activity.startActivity(intent);
    }
}

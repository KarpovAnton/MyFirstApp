package com.socializer.vacuum.activities.photo;

import android.content.Intent;

import com.socializer.vacuum.activities.account.AccountActivity;
import com.socializer.vacuum.activities.chatlist.ChatListActivity;

import javax.inject.Inject;

public class PhotoRouter implements PhotoContract.Router {
    @Inject
    PhotoActivity activity;

    @Inject
    public PhotoRouter() {

    }

    @Override
    public void openProfileActivity() {
        Intent mainIntent = new Intent(activity.getApplicationContext(), ChatListActivity.class);
        activity.startActivity(mainIntent);
    }

    @Override
    public void openAccountActivity() {
        Intent mainIntent = new Intent(activity.getApplicationContext(), AccountActivity.class);
        activity.startActivity(mainIntent);
    }
}

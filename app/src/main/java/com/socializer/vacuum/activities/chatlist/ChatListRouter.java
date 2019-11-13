package com.socializer.vacuum.activities.chatlist;

import android.content.Intent;

import com.socializer.vacuum.activities.account.AccountActivity;
import com.socializer.vacuum.activities.photo.PhotoActivity;
import com.socializer.vacuum.activities.photo.PhotoContract;

import javax.inject.Inject;

public class ChatListRouter implements ChatListContract.Router {

    @Inject
    ChatListActivity activity;

    @Inject
    public ChatListRouter() {

    }

    @Override
    public void openAccountActivity() {
        Intent mainIntent = new Intent(activity.getApplicationContext(), AccountActivity.class);
        activity.startActivity(mainIntent);
    }
}

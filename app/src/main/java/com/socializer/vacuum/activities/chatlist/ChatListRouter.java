package com.socializer.vacuum.activities.chatlist;

import android.content.Intent;

import com.socializer.vacuum.activities.account.AccountActivity;

import javax.inject.Inject;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class ChatListRouter implements ChatListContract.Router {

    @Inject
    ChatListActivity activity;

    @Inject
    public ChatListRouter() {

    }

    @Override
    public void openAccountActivity() {
        Intent intent = new Intent(activity.getApplicationContext(), AccountActivity.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }
}

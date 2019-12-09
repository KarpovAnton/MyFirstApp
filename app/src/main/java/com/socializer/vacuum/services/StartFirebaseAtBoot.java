/*
package com.socializer.vacuum.services;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;


public class StartFirebaseAtBoot extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                context.startService(new Intent(context, FirebaseMsgService.class));
            } else {
                context.startForegroundService(new Intent(context, FirebaseMsgService.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}*/

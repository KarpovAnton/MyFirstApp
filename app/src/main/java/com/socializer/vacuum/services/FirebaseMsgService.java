package com.socializer.vacuum.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.socializer.vacuum.R;
import com.socializer.vacuum.activities.chatlist.ChatListActivity;

import java.util.Date;

import timber.log.Timber;


public class FirebaseMsgService extends FirebaseMessagingService {

    private static final String GROUP_KEY = "com.socializer.vacuum.NOTIFICATIONS";

/*    @Inject
    @Named(NAMED_PREF_PUSH_TOKEN)
    StringPreference pushTokenSP;*/


    @Override
    public void onNewToken(String token) {
        Timber.d("moe Refreshed token: " + token);

/*        SharedPreferences.Editor editor = VacuumApplication.applicationContext.getSharedPreferences("prefs", MODE_PRIVATE).edit();
        editor.putString("PUSH_TOKEN", token);
        editor.apply();
        editor.commit();*/

/*        loginManager.sendPushToken(token, new DtoCallback<ResponseDto>() {
            @Override
            public void onSuccessful(@NonNull ResponseDto response) {
                Timber.d("moe sendPushToken succ");
            }

            @Override
            public void onFailed(FailTypes fail) {
                Timber.d("moe sendPushToken fail");
            }
        });*/

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            Timber.d("moe msg rec");
            //sendNotification(remoteMessage);
            remoteMessage.getData().get("message");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendNotification(RemoteMessage remoteMessage) {
        try {
            Intent notificationIntent = new Intent(this, ChatListActivity.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                    PendingIntent.FLAG_ONE_SHOT);

            String message = remoteMessage.getData().get("message");

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_icon)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_icon))
                    .setAutoCancel(true)
                    .setGroup(GROUP_KEY)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVibrate(new long[]{1000, 1000, 1000})
                    .setLights(Color.RED, 3000, 3000)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .setBigContentTitle(getString(R.string.app_name))
                            .bigText(message));

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager
                    .notify((int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE), notificationBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

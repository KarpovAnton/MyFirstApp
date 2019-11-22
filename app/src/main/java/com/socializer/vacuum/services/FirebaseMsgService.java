package com.socializer.vacuum.services;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.socializer.vacuum.network.data.DtoCallback;
import com.socializer.vacuum.network.data.FailTypes;
import com.socializer.vacuum.network.data.dto.ResponseDto;
import com.socializer.vacuum.network.data.managers.LoginManager;

import javax.inject.Inject;

import timber.log.Timber;

public class FirebaseMsgService extends FirebaseMessagingService {

    @Inject
    LoginManager loginManager;

    @Override
    public void onNewToken(String token) {
        Timber.d("moe Refreshed token: " + token);

        loginManager.sendPushToken(token, new DtoCallback<ResponseDto>() {
            @Override
            public void onSuccessful(@NonNull ResponseDto response) {
                Timber.d("moe sendPushToken succ");
            }

            @Override
            public void onFailed(FailTypes fail) {
                Timber.d("moe sendPushToken fail");
            }
        });
    }
}

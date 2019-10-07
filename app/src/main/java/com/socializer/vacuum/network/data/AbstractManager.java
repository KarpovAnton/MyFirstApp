package com.socializer.vacuum.network.data;

import android.app.Application;
import android.content.Context;

import com.socializer.vacuum.network.VacuumApi;
import com.socializer.vacuum.network.data.dto.socket.ChatCallback;
import com.socializer.vacuum.network.data.managers.ChatManager;
import com.socializer.vacuum.network.data.prefs.AuthSession;
import com.socializer.vacuum.utils.ErrorUtils;
import com.socializer.vacuum.utils.NetworkUtils;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import timber.log.Timber;

public abstract class AbstractManager {

    protected final ErrorUtils mErrorUtils;
    protected final Context mContext;
    protected VacuumApi mVacuumApi;

    @Inject
    AuthSession authSession;

    public AbstractManager(VacuumApi vacuumApi, ErrorUtils errorUtils, Application application) {
        mVacuumApi = vacuumApi;
        mErrorUtils = errorUtils;
        mContext = application.getApplicationContext();
    }

    protected boolean checkNetworkAvailable(DtoCallback<?> callback) {
        if (!NetworkUtils.isConnected(mContext)) {
            callback.onFailed(FailTypes.CONNECTION_ERROR);
            return false;
        }
        return true;
    }

    protected boolean checkNetworkAvailable(DtoListCallback<?> callback) {
        if (!NetworkUtils.isConnected(mContext)) {
            callback.onFailed(FailTypes.CONNECTION_ERROR);
            return false;
        }
        return true;
    }

    protected boolean checkNetworkAvailable(ChatCallback<?> callback) {
        if (!NetworkUtils.isConnected(mContext)) {
            callback.onFailed(FailTypes.CONNECTION_ERROR);
            return false;
        }
        return true;
    }

    protected boolean checkNetworkAvailable(ChatManager.ChatListCallback<?> callback) {
        if (!NetworkUtils.isConnected(mContext)) {
            callback.onFailed(FailTypes.CONNECTION_ERROR);
            return false;
        }
        return true;
    }

    protected boolean isTokenValid() {
        return authSession.isValid();
    }

    protected String getTokenString() {
        return "Bearer " + authSession.getToken();
    }

    protected void onParseErrorResponse(int status, ResponseBody body, final DtoCallback<?> callback) {
        if (status == 401) {
            Timber.i(body.toString());
            callback.onFailed(FailTypes.AUTH_REQUIRED);
        } else if (status==400) {
            // TODO parse error code
            callback.onFailed(FailTypes.UNKNOWN_ERROR);
        } else if ((status>=500) && (status<=502)) {
            callback.onFailed(FailTypes.CONNECTION_ERROR);
        } else {
            Timber.i(body.toString());
            callback.onFailed(FailTypes.UNKNOWN_ERROR);
        }
    }
}

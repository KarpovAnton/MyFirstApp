package com.socializer.vacuum.network.data;

import android.app.Application;
import android.content.Context;

import androidx.annotation.Nullable;

import com.socializer.vacuum.network.VacuumApi;
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

    public boolean checkAuth(@Nullable DtoCallback<?> callback) {
        if (!authSession.isValid()) {
            if (callback!=null)
                callback.onFailed(FailTypes.AUTH_REQUIRED);
            return false;
        }
        return true;
    }

    protected boolean checkNetworkAndAuthAvailable(DtoCallback<?> callback) {
        return checkNetworkAvailable(callback) && checkAuth(callback);
    }

    protected boolean checkNetworkAvailable(DtoCallback<?> callback) {
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

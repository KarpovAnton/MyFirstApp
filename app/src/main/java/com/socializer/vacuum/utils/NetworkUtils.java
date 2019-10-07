package com.socializer.vacuum.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.socializer.vacuum.R;
import com.socializer.vacuum.VacuumApplication;
import com.socializer.vacuum.network.data.prefs.AuthSession;

import timber.log.Timber;

public class NetworkUtils {

    public static boolean isConnected(@NonNull Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            return isConnected;
        } else {
            return true;
        }
    }

    public void logoutError(Context context) {
        Timber.d("moe WTF.......");
        DialogUtils.showErrorDialogMessage(
                context,
                R.string.dialog_title_auth_error,
                R.string.dialog_msg_auth_error,
                new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        VacuumApplication.restartApp();
                    }
                });
        AuthSession.getInstance().invalidate();
    }
}

package com.socializer.vacuum.utils;

import android.content.Context;

import androidx.annotation.StringRes;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.socializer.vacuum.R;

public class DialogUtils {

    public static void showInvalidParameterMessage(
            Context context, @StringRes int titleRes, @StringRes int messageRes) {
        new MaterialDialog.Builder(context)
                .theme(Theme.LIGHT)
                .title(titleRes)
                .content(messageRes)
                .positiveText(android.R.string.ok)
                .show();
    }

    public static void showInfoDialogMessage(
            Context context, @StringRes int titleRes, @StringRes int messageRes,
            MaterialDialog.SingleButtonCallback callback ){
        showErrorMessage(context, titleRes, messageRes, callback);
    }

    public static void showErrorMessage(
            Context context, @StringRes int titleRes, @StringRes int messageRes,
            MaterialDialog.SingleButtonCallback callback) {
        new MaterialDialog.Builder(context)
                .theme(Theme.LIGHT)
                .title(titleRes)
                .content(messageRes)
                .positiveText(android.R.string.ok)
                .onPositive(callback)
                .cancelable(false)
                .show();
    }

    public static void showParamRequiredMessage(Context context, @StringRes int key) {
        String content = String.format(
                //context.getString(R.string.message_field_required),
                context.getString(key)
        );
        new MaterialDialog.Builder(context)
                .theme(Theme.LIGHT)
                //.title(R.string.title_invalid_parameter)
                .content(content)
                .positiveText(android.R.string.ok)
                .cancelable(true)
                .show();
    }

    public static MaterialDialog getErrorDialogMessage(
            Context context, @StringRes int titleRes, @StringRes int messageRes,
            MaterialDialog.SingleButtonCallback callback) {

        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .theme(Theme.LIGHT)
                .title(titleRes)
                .content(messageRes)
                .positiveText(android.R.string.ok)
                .onPositive(callback)
                .cancelable(false)
                .show();

        return dialog;
    }

    public static MaterialDialog showWaitingDialog(
            Context context, @StringRes int titleRes, @StringRes int messageRes) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .theme(Theme.LIGHT)
                .title(titleRes)
                .content(messageRes)
                .cancelable(false).build();
        dialog.show();

        return dialog;
    }

    public static MaterialDialog showConfirmDialog(
            Context context, @StringRes int titleRes, @StringRes int messageRes,
            MaterialDialog.SingleButtonCallback positiveCallback,
            MaterialDialog.SingleButtonCallback negativeCallback
    ) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .theme(Theme.LIGHT)
                .title(titleRes)
                .content(messageRes)
                .negativeText(android.R.string.no)
                .positiveText(android.R.string.yes)
                .onPositive(positiveCallback)
                .onNegative(negativeCallback)
                .cancelable(false).build();
        dialog.show();

        return dialog;
    }

    public static MaterialDialog showChooseActionDialog(
            Context context, @StringRes int titleRes, @StringRes int messageRes,
            MaterialDialog.SingleButtonCallback positiveCallback,
            MaterialDialog.SingleButtonCallback negativeCallback
    ) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .theme(Theme.LIGHT)
                .title(titleRes)
                .content(messageRes)
                .negativeText(R.string.dialog_unbind)
                .positiveText(R.string.dialog_show_social_account)
                .buttonsGravity(GravityEnum.CENTER)
                .onPositive(positiveCallback)
                .onNegative(negativeCallback)
                .cancelable(false).build();
        dialog.show();

        return dialog;
    }

    public static MaterialDialog showInputDialog(
            Context context, @StringRes int titleRes, @StringRes int messageRes,
            MaterialDialog.InputCallback callback
    ) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .theme(Theme.DARK)
                .title(titleRes)
                .content(messageRes)
                .input("username", null, false, callback)
                .positiveText(android.R.string.yes)
                .cancelable(false).build();
        dialog.show();

        return dialog;
    }
}

package com.karpov.vacuum.activities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.karpov.vacuum.R;
import com.karpov.vacuum.network.data.DtoCallback;
import com.karpov.vacuum.network.data.FailTypes;
import com.karpov.vacuum.network.data.dto.RegistrationResponseDto;
import com.karpov.vacuum.network.data.dto.ResponseDto;
import com.karpov.vacuum.network.data.managers.LoginManager;
import com.karpov.vacuum.services.BleManager;
import com.karpov.vacuum.utils.DialogUtils;

import java.util.UUID;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import timber.log.Timber;

import static com.karpov.vacuum.utils.Consts.LOCATION_PERMISSION_CODE;

public class LoginActivity extends DaggerAppCompatActivity {

    @Inject
    LoginManager loginManager;

    @Inject
    BleManager bleManager;

    String uniqueUUID;

    Activity mActivity;
    MaterialDialog regWaitingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkPermissions();
        uniqueUUID = UUID.randomUUID().toString();
        startRegistrationDialog();
        mActivity = this;
    }

    private void startRegistrationDialog() {
        DialogUtils.showInputDialog(
                this,
                R.string.title_dialog,
                R.string.title_dialog,
                new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        loginManager.sendUsername(input.toString(), uniqueUUID, new DtoCallback<ResponseDto>() {
                            @Override
                            public void onSuccessful(@NonNull ResponseDto response) {
                                RegistrationResponseDto responseDto = (RegistrationResponseDto) response;
                                String userID = responseDto.getUserId();
                                Toast.makeText(mActivity, userID, Toast.LENGTH_SHORT).show();
                                Timber.d("registration %s", userID);
                                regWaitingDialog = DialogUtils.showWaitingDialog(
                                        mActivity,
                                        R.string.registration_dialog_title,
                                        R.string.waiting_dialog_msg);
                                registrationWithUserId(userID);
//                                mActivity.setResult(RESULT_OK);
//                                mActivity.finish();
                            }

                            @Override
                            public void onFailed(FailTypes fail) {
                                Toast.makeText(mActivity, "failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }

    private void registrationWithUserId(String userID) {
        loginManager.login(userID, uniqueUUID, new DtoCallback<ResponseDto>() {
            @Override
            public void onSuccessful(@NonNull ResponseDto response) {
                regWaitingDialog.dismiss();
                mActivity.setResult(RESULT_OK);
                mActivity.finish();
            }

            @Override
            public void onFailed(FailTypes fail) {

            }
        });
    }

    void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "permission granted", Toast.LENGTH_SHORT).show();
                onResume();
            }
        }
    }
}

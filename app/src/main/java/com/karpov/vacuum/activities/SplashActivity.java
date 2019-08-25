package com.karpov.vacuum.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.karpov.vacuum.R;
import com.karpov.vacuum.activities.main.MainActivity;
import com.karpov.vacuum.network.data.prefs.AuthSession;
import com.karpov.vacuum.utils.Consts;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

import static com.karpov.vacuum.activities.main.MainActivity.LOGIN_REQUEST_CODE;

public class SplashActivity extends DaggerAppCompatActivity {
    @Inject
    AuthSession authSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                initNextActivity();
            }
        }, Consts.SPLASH_DELAY);
    }

    void initNextActivity() {
        //if (authRequired()) {
        //    startLoginActivity();
        //} else {
            startMainActivity();
        //}
    }

    boolean authRequired() {
        if (authSession !=null)
            return !authSession.isValid();
        return true;
    }

    void startLoginActivity() {
        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivityForResult(loginIntent, LOGIN_REQUEST_CODE);
    }

    void startMainActivity() {
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOGIN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                startMainActivity();
            }
        }
    }
}

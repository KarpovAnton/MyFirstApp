package com.karpov.vacuum.network.data.prefs;

import android.content.SharedPreferences;

import com.karpov.vacuum.utils.StringPreference;

import timber.log.Timber;

public class AuthSession {
    private static final String PREF_KEY_TOKEN = "PREF_KEY_TOKEN";
    private static final String PREF_KEY_EXP_DATE = "PREF_KEY_EXP_DATE";

    private static AuthSession instance;

    private String token;
    private long expiredAt;

    SharedPreferences prefs;

    public AuthSession(SharedPreferences prefs) {
        this.prefs = prefs;
        token  = new StringPreference(prefs, PREF_KEY_TOKEN, "").get();
        expiredAt = Long.parseLong(
                new StringPreference(prefs, PREF_KEY_EXP_DATE, "1").get());
        instance = this;
    }

    public static synchronized AuthSession getInstance() {
        AuthSession localInstance = instance;
        if (localInstance == null) {
            synchronized (AuthSession.class) {
                localInstance = instance;
                if (localInstance == null) {
                    //instance = localInstance = new AuthSession();
                    Timber.d("moe AuthSession null");
                }
            }
        }
        return localInstance;
    }

    public boolean isValid() {
        long now = System.currentTimeMillis();
        return token != null && token.length()>0 && now > expiredAt;
    }

    public String getToken() {
        return token;
    }

    public void update(String token, long expiredAt) {
        this.token = token;
        this.expiredAt = expiredAt;
        new StringPreference(prefs, PREF_KEY_TOKEN, "").set(token);
        new StringPreference(prefs, PREF_KEY_EXP_DATE, "1").set(""+expiredAt);
    }

    public void invalidate() {
        token = null;
        expiredAt = 0;
        update(null, 0);
    }

    public String toString() {
        return this.token+" "+this.expiredAt;
    }
}
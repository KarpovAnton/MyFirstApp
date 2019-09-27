package com.socializer.vacuum.network.data.prefs;

import android.content.SharedPreferences;

import com.socializer.vacuum.utils.StringPreference;

import timber.log.Timber;

public class AuthSession {
    private static final String PREF_KEY_TOKEN = "PREF_KEY_TOKEN";
    private static final String PREF_KEY_EXP_DATE = "PREF_KEY_EXP_DATE";

    private static AuthSession instance;

    private String token;
    private long expiresIn;

    SharedPreferences prefs;

    public AuthSession(SharedPreferences prefs) {
        this.prefs = prefs;
        token  = new StringPreference(prefs, PREF_KEY_TOKEN, "").get();
        expiresIn = Long.parseLong(
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
                    Timber.d("moe AuthSession null");//TODO
                }
            }
        }
        return localInstance;
    }

    public boolean isValid() {
        long now = System.currentTimeMillis();
        return token != null && token.length()>0 && now < expiresIn /*&& expiresIn != 1*/;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        new StringPreference(prefs, PREF_KEY_TOKEN, "").set(token);
    }

    public void update(String token, long expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn * 1000 + System.currentTimeMillis();
        new StringPreference(prefs, PREF_KEY_TOKEN, "").set(token);
        new StringPreference(prefs, PREF_KEY_EXP_DATE, "1").set(""+this.expiresIn);
    }

    public void invalidate() {
        token = null;
        expiresIn = 0;
        update(null, 0);
    }

    public String toString() {
        return this.token+" "+this.expiresIn;
    }
}
package com.socializer.vacuum.network.data.prefs;

import android.content.SharedPreferences;

import com.socializer.vacuum.utils.StringPreference;

public class Credentials {
    private static final String PREF_KEY_LOGIN = "PREF_KEY_LOGIN";
    private static final String PREF_KEY_PASSWORD = "PREF_KEY_PASSWORD";

    private String login;
    private String password;

    private SharedPreferences prefs;

    public Credentials(SharedPreferences prefs) {
        this.prefs = prefs;
        login  = new StringPreference(prefs, PREF_KEY_LOGIN, "").get();
        password = new StringPreference(prefs, PREF_KEY_PASSWORD, "").get();
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void update(String login, String password) {
        this.login = login;
        this.password = password;
        new StringPreference(prefs, PREF_KEY_LOGIN, "").set(login);
        new StringPreference(prefs, PREF_KEY_PASSWORD, "").set(password);
    }
}

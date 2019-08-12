package com.karpov.vacuum.network.data.prefs;

import android.content.SharedPreferences;

import com.karpov.vacuum.utils.StringPreference;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = DataModule.class)
public class PrefsModule {

    private static final String PREF_KEY_PHONE = "PHONE";
    private static final String PREF_KEY_TOKEN = "TOKEN";

    public static final String NAMED_PREF_PHONE = "pref_phone";
    public static final String NAMED_PREF_TOKEN = "pref_token";

    @Provides
    @Singleton
    @Named(NAMED_PREF_PHONE)
    StringPreference providePhone(SharedPreferences prefs) {
        return new StringPreference(prefs, PREF_KEY_PHONE, null);
    }

    @Provides
    @Singleton
    @Named(NAMED_PREF_TOKEN)
    StringPreference provideToken(SharedPreferences prefs) {
        return new StringPreference(prefs, PREF_KEY_TOKEN, null);
    }
}

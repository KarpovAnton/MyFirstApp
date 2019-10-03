package com.socializer.vacuum.network.data.prefs;

import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = DataModule.class)
public class SessionModule {

    @Provides
    @Singleton
    AuthSession provideAuthSession(SharedPreferences prefs) {
        return new AuthSession(prefs);
    }
}


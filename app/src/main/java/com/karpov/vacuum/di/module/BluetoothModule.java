package com.karpov.vacuum.di.module;

import android.app.Application;

import com.karpov.vacuum.services.BleManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class BluetoothModule {
    @Provides
    @Singleton
    BleManager provideBleManager(Application application) {
        return new BleManager(application);
    }
}

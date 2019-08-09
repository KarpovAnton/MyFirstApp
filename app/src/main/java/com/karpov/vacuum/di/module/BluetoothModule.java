package com.karpov.vacuum.di.module;

import com.karpov.vacuum.services.BluetoothManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class BluetoothModule {
    @Provides
    @Singleton
    BluetoothManager provideBluetoothManager() {
        return new BluetoothManager();
    }
}

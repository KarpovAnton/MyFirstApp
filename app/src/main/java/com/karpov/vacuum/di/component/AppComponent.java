package com.karpov.vacuum.di.component;

import android.app.Application;

import com.karpov.vacuum.VacuumApplication;
import com.karpov.vacuum.di.module.ActivityBindingModule;
import com.karpov.vacuum.di.module.AppModule;
import com.karpov.vacuum.di.module.BluetoothModule;
import com.karpov.vacuum.di.module.NetModule;
import com.karpov.vacuum.network.data.prefs.PrefsModule;
import com.karpov.vacuum.network.data.prefs.SessionModule;
import com.karpov.vacuum.services.BleManager;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
        AppModule.class,
        NetModule.class,
        BluetoothModule.class,
        SessionModule.class,
        PrefsModule.class,
        ActivityBindingModule.class,
        AndroidSupportInjectionModule.class})

public interface AppComponent extends AndroidInjector<VacuumApplication> {
    BleManager getBleManager();
    // Gives us syntactic sugar. we can then do DaggerAppComponent.builder().application(this).build().inject(this);
    // never having to instantiate any modules or say which module we are passing the application to.
    // Application will just be provided into our app graph now.
    @Component.Builder
    interface Builder {
        @BindsInstance
        AppComponent.Builder application(Application application);
        AppComponent build();
    }
}
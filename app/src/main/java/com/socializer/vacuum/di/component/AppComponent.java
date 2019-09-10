package com.socializer.vacuum.di.component;

import android.app.Application;

import com.socializer.vacuum.VacuumApplication;
import com.socializer.vacuum.di.module.ActivityBindingModule;
import com.socializer.vacuum.di.module.AppModule;
import com.socializer.vacuum.di.module.BluetoothModule;
import com.socializer.vacuum.di.module.NetModule;
import com.socializer.vacuum.network.data.prefs.PrefsModule;
import com.socializer.vacuum.network.data.prefs.SessionModule;
import com.socializer.vacuum.services.BleManager;

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
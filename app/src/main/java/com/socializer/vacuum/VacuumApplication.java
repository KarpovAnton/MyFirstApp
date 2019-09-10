package com.socializer.vacuum;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.multidex.MultiDex;

import com.socializer.vacuum.di.component.AppComponent;
import com.socializer.vacuum.di.component.DaggerAppComponent;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import timber.log.Timber;

public class VacuumApplication extends DaggerApplication implements Application.ActivityLifecycleCallbacks {

    private static AppComponent component;

    Activity currentActivity;
    public static volatile Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        applicationContext = getApplicationContext();
        Timber.plant(new Timber.DebugTree());
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        component = DaggerAppComponent.builder().application(this).build();
        return component;
    }

    public static AppComponent getComponent() {
        return component;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        currentActivity = activity;
    }
    @Override
    public void onActivityStarted(Activity activity) {
        currentActivity = activity;
    }
    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = activity;
    }
    @Override
    public void onActivityPaused(Activity activity) {
        Timber.d( "onPause %s", activity.getClass().getSimpleName());
    }
    @Override
    public void onActivityStopped(Activity activity) {
        Timber.d( "onStop %s", activity.getClass().getSimpleName());
    }
    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        Timber.d( "onSaveInstanceState %s", activity.getClass().getSimpleName());
    }
    @Override
    public void onActivityDestroyed(Activity activity) {
        Timber.d( "onStop %s", activity.getClass().getSimpleName());
    }
}

package com.karpov.vacuum;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.multidex.MultiDex;

import com.karpov.vacuum.di.component.DaggerAppComponent;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import timber.log.Timber;

public class VacuumApplication extends DaggerApplication implements Application.ActivityLifecycleCallbacks {
    private static VacuumApplication instance;

    Activity currentActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        Timber.plant(new Timber.DebugTree());
        instance = this;
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().application(this).build();
    }

    public static VacuumApplication getInstance() {
        return instance;
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

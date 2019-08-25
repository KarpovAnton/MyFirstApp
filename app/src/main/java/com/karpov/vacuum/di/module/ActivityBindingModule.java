package com.karpov.vacuum.di.module;

import com.karpov.vacuum.activities.LoginActivity;
import com.karpov.vacuum.activities.SplashActivity;
import com.karpov.vacuum.activities.main.MainActivity;
import com.karpov.vacuum.activities.main.ProfileModule;
import com.karpov.vacuum.di.base.ActivityScoped;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBindingModule {
    @ActivityScoped
    @ContributesAndroidInjector(modules = {
            ProfileModule.class
    })
    abstract MainActivity mainActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = {})
    abstract LoginActivity loginActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = {})
    abstract SplashActivity splashActivity();
}
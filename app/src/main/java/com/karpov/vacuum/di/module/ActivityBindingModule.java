package com.karpov.vacuum.di.module;

import com.karpov.vacuum.activities.LoginActivity;
import com.karpov.vacuum.activities.SplashActivity;
import com.karpov.vacuum.activities.account.AccountActivity;
import com.karpov.vacuum.activities.main.MainActivity;
import com.karpov.vacuum.activities.photo.PhotoActivity;
import com.karpov.vacuum.di.base.ActivityScoped;
import com.karpov.vacuum.fragments.ProfileModule;

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

    @ActivityScoped
    @ContributesAndroidInjector(modules = {})
    abstract AccountActivity profileActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = {})
    abstract PhotoActivity photoActivity();
}
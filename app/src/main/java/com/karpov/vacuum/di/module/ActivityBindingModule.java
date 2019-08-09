package com.karpov.vacuum.di.module;

import com.karpov.vacuum.activities.main.MainActivity;
import com.karpov.vacuum.di.base.ActivityScoped;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBindingModule {
    @ActivityScoped
    @ContributesAndroidInjector(modules = {})
    abstract MainActivity mainActivity();
}
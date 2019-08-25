package com.karpov.vacuum.activities.main;

import com.karpov.vacuum.di.base.FragmentScoped;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ProfileModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract ProfileFragment profileFragment();

}

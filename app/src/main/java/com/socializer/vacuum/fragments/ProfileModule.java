package com.socializer.vacuum.fragments;

import com.socializer.vacuum.di.base.FragmentScoped;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ProfileModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract ProfileFragment profileFragment();

}

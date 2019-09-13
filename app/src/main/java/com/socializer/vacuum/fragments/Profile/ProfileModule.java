package com.socializer.vacuum.fragments.Profile;

import com.socializer.vacuum.di.base.FragmentScoped;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ProfileModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract ProfileFragment profileFragment();

}

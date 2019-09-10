package com.socializer.vacuum.activities.main;

import com.socializer.vacuum.di.base.ActivityScoped;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class MainModule {

    @ActivityScoped
    @Binds
    abstract MainContract.Presenter mainPresenter(MainPresenter presenter);
}

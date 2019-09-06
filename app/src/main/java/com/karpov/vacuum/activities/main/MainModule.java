package com.karpov.vacuum.activities.main;

import com.karpov.vacuum.di.base.ActivityScoped;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class MainModule {

    @ActivityScoped
    @Binds
    abstract MainContract.Presenter mainPresenter(MainPresenter presenter);
}

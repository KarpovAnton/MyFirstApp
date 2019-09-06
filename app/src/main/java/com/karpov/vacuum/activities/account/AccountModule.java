package com.karpov.vacuum.activities.account;

import com.karpov.vacuum.di.base.ActivityScoped;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class AccountModule {

    @ActivityScoped
    @Binds
    abstract AccountContract.Presenter accountPresenter(AccountPresenter presenter);
}

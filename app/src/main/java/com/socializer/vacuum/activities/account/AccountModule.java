package com.socializer.vacuum.activities.account;

import com.socializer.vacuum.di.base.ActivityScoped;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class AccountModule {

    @ActivityScoped
    @Binds
    abstract AccountContract.Presenter accountPresenter(AccountPresenter presenter);
}

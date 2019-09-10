package com.socializer.vacuum.activities.photo;

import com.socializer.vacuum.di.base.ActivityScoped;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class PhotoModule {

    @ActivityScoped
    @Binds
    abstract PhotoContract.Presenter photoPresenter(PhotoPresenter presenter);
}

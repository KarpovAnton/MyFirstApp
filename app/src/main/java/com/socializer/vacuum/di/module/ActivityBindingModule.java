package com.socializer.vacuum.di.module;

import com.socializer.vacuum.activities.ChatActivity;
import com.socializer.vacuum.activities.chatlist.ChatListActivity;
import com.socializer.vacuum.activities.LoginActivity;
import com.socializer.vacuum.activities.SplashActivity;
import com.socializer.vacuum.activities.account.AccountActivity;
import com.socializer.vacuum.activities.main.MainActivity;
import com.socializer.vacuum.activities.photo.PhotoActivity;
import com.socializer.vacuum.di.base.ActivityScoped;
import com.socializer.vacuum.fragments.Profile.ProfileModule;

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

    @ActivityScoped
    @ContributesAndroidInjector(modules = {})
    abstract ChatActivity chatActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = {})
    abstract ChatListActivity chatListActivity();
}
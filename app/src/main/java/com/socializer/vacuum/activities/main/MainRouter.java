package com.socializer.vacuum.activities.main;

import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.socializer.vacuum.R;
import com.socializer.vacuum.activities.ChatListActivity;
import com.socializer.vacuum.activities.account.AccountActivity;
import com.socializer.vacuum.fragments.Profile.ProfileFragment;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto;

import javax.inject.Inject;

public class MainRouter implements MainContract.Router {
    @Inject
    MainActivity activity;

    @Inject
    public MainRouter() {

    }

    public void openProfile(ProfilePreviewDto profileDto) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.configure(profileDto);
        replaceFragment(fragment);
    }

    @Override
    public void removeFragment() {
        for (Fragment fragment : activity.getSupportFragmentManager().getFragments()) {
            if (fragment != null)
                activity.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    @Override
    public void openAccountActivity() {
        Intent mainIntent = new Intent(activity.getApplicationContext(), AccountActivity.class);
        activity.startActivity(mainIntent);
    }

    @Override
    public void openChatListActivity() {
        Intent mainIntent = new Intent(activity.getApplicationContext(), ChatListActivity.class);
        activity.startActivity(mainIntent);
    }

    void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.profileContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

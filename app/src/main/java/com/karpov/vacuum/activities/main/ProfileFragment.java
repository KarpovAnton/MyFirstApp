package com.karpov.vacuum.activities.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.karpov.vacuum.R;
import com.karpov.vacuum.network.data.dto.ProfilePreviewDto;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnFocusChange;
import dagger.android.support.DaggerFragment;
import timber.log.Timber;

public class ProfileFragment extends DaggerFragment {

    private ProfilePreviewDto profileDto;
    private SwipeRefreshLayout act;

    @Inject
    public ProfileFragment() {}

    public void configure(SwipeRefreshLayout activity, ProfilePreviewDto profilePreviewDto) {
        act = activity;
        profileDto = profilePreviewDto;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        initViews();
        return view;
    }



    void initViews() {

    }

    public void setData() {

    }
}

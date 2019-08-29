package com.karpov.vacuum.activities.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.karpov.vacuum.R;
import com.karpov.vacuum.network.data.dto.ProfilePreviewDto;
import com.karpov.vacuum.network.data.dto.ProfilePreviewDto.ProfileImageDto;
import com.karpov.vacuum.views.adapters.PhotosAdapter;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;

public class ProfileFragment extends DaggerFragment {

    private ProfilePreviewDto profileDto;
    private SwipeRefreshLayout act;

    PhotosAdapter photosAdapter;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.nameText)
    TextView nameText;

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
        photosAdapter = new PhotosAdapter(getContext());

        List<ProfileImageDto> photos = profileDto.getImages();
        photosAdapter.setPhotos(photos);

        viewPager.setAdapter(photosAdapter);

        setName(profileDto.getUsername());
    }

    private void setName(String username) {
        nameText.setText(username);
    }

}

package com.socializer.vacuum.fragments.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.socializer.vacuum.R;
import com.socializer.vacuum.activities.ChatActivity;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto.ProfileImageDto;
import com.socializer.vacuum.views.adapters.PhotosAdapter;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerFragment;

public class ProfileFragment extends DaggerFragment {

    private ProfilePreviewDto profileDto;

    PhotosAdapter photosAdapter;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.nameText)
    TextView nameText;

    @Inject
    public ProfileFragment() {}

    public void configure(ProfilePreviewDto profilePreviewDto) {
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

    @OnClick(R.id.chatBtn)
    void onChatBtnClick() {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        String deviceName = profileDto.getUserId();
        deviceName = deviceName.split("@")[0];
        intent.putExtra("receiverId", deviceName);
        getActivity().startActivity(intent);
    }

}

package com.karpov.vacuum.activities.account;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.karpov.vacuum.R;
import com.karpov.vacuum.network.data.dto.ProfilePreviewDto;
import com.karpov.vacuum.network.data.dto.ProfilePreviewDto.ProfileImageDto;
import com.karpov.vacuum.utils.ImageUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

public class AccountActivity extends DaggerAppCompatActivity implements AccountContract.View {

    @Inject
    AccountPresenter presenter;

    @Inject
    AccountRouter router;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.nameText)
    TextView nameText;

    @BindView(R.id.statusImage)
    ImageView statusImage;

    ArrayList<String> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.takeView(this);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onAccountLoaded(ProfilePreviewDto accountDto) {
        imageList = new ArrayList<>();
        for (ProfileImageDto imageDto : accountDto.getImages()) {
            imageList.add(imageDto.getUrl());
        }

        int status = accountDto.getStatus();
        if (status == 1) {
            new ImageUtils().setImage(statusImage, null, null, R.drawable.online);
        } else {
            new ImageUtils().setImage(statusImage, null, null, R.drawable.offline);
        }

        nameText.setText(accountDto.getUsername());
    }

    @OnClick(R.id.editPhotoButton)
    void onEditPhotoClick() {
        router.openPhotoActivity(imageList);
    }

    @OnClick({R.id.backImage, R.id.backText})
    void onBackClick() {
        onBackPressed();
    }
}

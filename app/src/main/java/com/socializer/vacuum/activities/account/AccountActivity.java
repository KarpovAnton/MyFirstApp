package com.socializer.vacuum.activities.account;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.socializer.vacuum.R;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto.ProfileImageDto;
import com.socializer.vacuum.utils.ImageUtils;

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

    @BindView(R.id.vpPlaceholder)
    TextView vpPlaceholder;

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
        if (imageList.size() > 0) {
            vpPlaceholder.setVisibility(View.GONE);
        } else {
            vpPlaceholder.setVisibility(View.VISIBLE);
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
        String[] photoArray = imageList.toArray(new String[0]);
        router.openPhotoActivity(photoArray);
    }

    @OnClick({R.id.backImage, R.id.backText})
    void onBackClick() {
        onBackPressed();
    }
}

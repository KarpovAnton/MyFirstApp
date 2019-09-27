package com.socializer.vacuum.activities.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.socializer.vacuum.R;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto.ProfileImageDto;
import com.socializer.vacuum.utils.DialogUtils;
import com.socializer.vacuum.utils.ImageUtils;
import com.socializer.vacuum.utils.StringPreference;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

import static com.socializer.vacuum.network.data.prefs.PrefsModule.NAMED_PREF_DEVICE_NAME;

public class AccountActivity extends DaggerAppCompatActivity implements AccountContract.View {

    public static final int FB = 0;
    public static final int VK = 1;
    public static final int INST = 2;
    public static final int NOONE = 3;


    @Inject
    @Named(NAMED_PREF_DEVICE_NAME)
    StringPreference deviceNameSP;

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

    @BindView(R.id.vkButton)
    ImageView vkButton;

    @BindView(R.id.fbButton)
    ImageView fbButton;

    @BindView(R.id.instButton)
    ImageView instButton;

    String profileId;
    ArrayList<String> imageList;
    boolean isVkBind;
    boolean isFbBind;
    boolean isInstBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        vkButton.setImageAlpha(100);
        fbButton.setImageAlpha(100);
        instButton.setImageAlpha(100);

        setIdFromSP();
        presenter.takeView(this);
        presenter.loadAccount(profileId);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onAccountLoaded(ProfilePreviewDto accountDto) {
        imageList = new ArrayList<>();
        List<ProfileImageDto> imageDtoList = accountDto.getImages();
        if (imageDtoList != null) {
            for (ProfileImageDto imageDto : imageDtoList) {
                imageList.add(imageDto.getUrl());
            }
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

        List<ProfilePreviewDto.ProfileAccountDto> accounts = accountDto.getAccounts();
        for (int i = 0; i < accounts.size(); i++) {
            ProfilePreviewDto.ProfileAccountDto acc = accounts.get(i);
            int accKind = acc.getKind();
            setIconState(accKind, true);
        }
    }

    @OnClick(R.id.vkButton)
    void onVkClick() {
        if (isVkBind) {
            MaterialDialog.SingleButtonCallback positiveCallback = new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    presenter.openVKProfile();
                }
            };

            MaterialDialog.SingleButtonCallback negativeCallback = new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    unBindVK();
                }
            };
            DialogUtils.showChooseActionDialog(this, R.string.dialog_social_action_title, R.string.empty_string, positiveCallback, negativeCallback);
        } else {
            bindVK();
        }
    }

    private void bindVK() {
        VKSdk.login(this);
    }

    private void unBindVK() {
        presenter.unBindVK();
    }

    @Override
    public void onVkUnBind() {
        setIconState(VK, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                String socialUserId = res.userId;
                String accessToken = res.accessToken;
                presenter.bindVK(socialUserId, accessToken);
            }
            @Override
            public void onError(VKError error) {
                // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
            }})) {

            //if not vk, then...
            //callbackManager.onActivityResult(requestCode, resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void setIdFromSP() {
        String userID = "";
        if (deviceNameSP != null)
            userID = deviceNameSP.get();
        profileId = userID.split("@")[0];
    }

    private void setIconState(int kind, boolean isBind) {
        switch (kind) {
            case FB:
                isFbBind = isBind;
                if (isBind) {
                    fbButton.setImageAlpha(255);
                } else {
                    fbButton.setImageAlpha(100);
                }

                break;

            case VK:
                isVkBind = isBind;
                if (isBind) {
                    vkButton.setImageAlpha(255);
                } else {
                    vkButton.setImageAlpha(100);
                }
                break;

            case INST:
                isInstBind = isBind;
                if (isBind) {
                    instButton.setImageAlpha(255);
                } else {
                    instButton.setImageAlpha(100);
                }
                break;
            case NOONE:
                break;
            default:
                throw new IllegalStateException("unknown type");
        }
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

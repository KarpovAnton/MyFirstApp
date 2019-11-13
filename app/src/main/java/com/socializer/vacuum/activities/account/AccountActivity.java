package com.socializer.vacuum.activities.account;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.socializer.vacuum.R;
import com.socializer.vacuum.commons.AuthenticationDialog;
import com.socializer.vacuum.network.data.FailTypes;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto.ProfileImageDto;
import com.socializer.vacuum.utils.DialogUtils;
import com.socializer.vacuum.utils.StringPreference;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

import static android.os.Looper.getMainLooper;
import static com.socializer.vacuum.network.data.prefs.PrefsModule.NAMED_PREF_DEVICE_NAME;
import static com.socializer.vacuum.network.data.prefs.PrefsModule.NAMED_PREF_SOCIAL;

public class AccountActivity extends DaggerAppCompatActivity implements AccountContract.View, AuthenticationDialog.AuthInstListener {

    public static final int FB = 0;
    public static final int VK = 1;
    public static final int INST = 2;
    public static final int NOONE = 3;


    @Inject
    @Named(NAMED_PREF_DEVICE_NAME)
    StringPreference deviceNameSP;

    @Inject
    @Named(NAMED_PREF_SOCIAL)
    StringPreference socialSP;

    @Inject
    AccountPresenter presenter;

    @Inject
    AccountRouter router;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.nameText)
    TextView nameText;

    @BindView(R.id.vpPlaceholder)
    TextView vpPlaceholder;

    @BindView(R.id.vkButton)
    ImageView vkButton;

    @BindView(R.id.fbButton)
    ImageView fbButton;

    @BindView(R.id.instButton)
    ImageView instButton;

    CallbackManager callbackManager;
    String profileId;
    ArrayList<String> imageList;
    boolean isVkBind;
    boolean isFbBind;
    boolean isInstBind;
    private boolean isDialogShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        vkButton.setImageAlpha(100);
        fbButton.setImageAlpha(100);
        instButton.setImageAlpha(100);
        setAccountIdFromSP();
        callbackManager = CallbackManager.Factory.create();
        fbCallbackRegistration();
        presenter.takeView(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
            //new ImageUtils().setImage(statusImage, null, null, R.drawable.online);
        } else {
            //new ImageUtils().setImage(statusImage, null, null, R.drawable.offline);
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
                    presenter.unBindSocial(VK);
                }
            };
            DialogUtils.showChooseActionDialog(this, R.string.dialog_social_action_title, R.string.empty_string, positiveCallback, negativeCallback);
        } else {
            bindVK();
        }
    }

    @OnClick(R.id.fbButton)
    void onFbClick() {
        if (isFbBind) {
            MaterialDialog.SingleButtonCallback positiveCallback = new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    presenter.openFBProfile();
                }
            };

            MaterialDialog.SingleButtonCallback negativeCallback = new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    presenter.unBindSocial(FB);
                }
            };
            DialogUtils.showChooseActionDialog(this, R.string.dialog_social_action_title, R.string.empty_string, positiveCallback, negativeCallback);
        } else {
            bindFB();
        }
    }

    @OnClick(R.id.instButton)
    void onInstClick() {
        if (isInstBind) {
            MaterialDialog.SingleButtonCallback positiveCallback = new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    presenter.openInstProfile();
                }
            };

            MaterialDialog.SingleButtonCallback negativeCallback = new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    presenter.unBindSocial(INST);
                }
            };
            DialogUtils.showChooseActionDialog(this, R.string.dialog_social_action_title, R.string.empty_string, positiveCallback, negativeCallback);
        } else {
            bindINST();
        }
    }

    private void bindVK() {
        VKSdk.login(this);
    }

    private void bindFB() {
        com.facebook.login.LoginManager.getInstance().logIn(this, Arrays.asList("public_profile"));
    }

    private void bindINST() {
        AuthenticationDialog dialog = new AuthenticationDialog(this, this);
        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    public void onSocialBinded() {
        setAccountIdFromSP();
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                presenter.loadAccount(profileId);
            }
        }, 500);
    }

    @Override
    public void onSocUnBind(int kind) {
        setIconState(kind, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                String socialUserId = res.userId;
                String accessToken = res.accessToken;
                presenter.bindSocial(VK, socialUserId, accessToken, null);
            }
            @Override
            public void onError(VKError error) {
                // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
            }})) {

            //if not vk, then...
            callbackManager.onActivityResult(requestCode, resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void fbCallbackRegistration() {
        com.facebook.login.LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                String socialUserId = accessToken.getUserId();
                String token = accessToken.getToken();
                presenter.bindSocial(FB, socialUserId, token, null);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException exception) {

            }
        });
    }

    @Override
    public void onInstTokenReceived(String username, String userId, String auth_token) {
        if (auth_token == null) return;
        //presenter.getInstSocialUserIdAndBind(auth_token);
        presenter.bindSocial(INST, userId, auth_token, username);
    }

    @Override
    public void setAccountIdFromSP() {
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
        String[] photoArray = new String[0];
        if (imageList != null)
            photoArray = imageList.toArray(new String[0]);

        router.openPhotoActivity(photoArray);
    }

    @OnClick(R.id.chatListBtn)
    void onChatListBtnClick() {
        if (socialSP.get().equals("true")) {
            router.openChatListActivity();
        } else {
            DialogUtils.showErrorMessage(this, R.string.dialog_msg_social_error);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.dropView();
    }

    @Override
    public void showErrorNetworkDialog(FailTypes fail) {
        switch (fail) {
            case UNKNOWN_ERROR:
                //new NetworkUtils().logoutError(getApplicationContext());
                break;
            case CONNECTION_ERROR:

                if (!isDialogShow) {
                    DialogUtils.showNetworkErrorMessage(this);
                    isDialogShow = true;
                }
                new Handler(getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isDialogShow = false;
                    }
                }, 3000);

                break;
        }
    }

    @OnClick(R.id.backBtn)
    void onBackClick() {
        onBackPressed();
    }
}

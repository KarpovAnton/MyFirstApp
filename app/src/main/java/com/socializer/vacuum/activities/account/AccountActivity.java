package com.socializer.vacuum.activities.account;

import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
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
import com.socializer.vacuum.network.data.prefs.AuthSession;
import com.socializer.vacuum.services.BleManager;
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
import timber.log.Timber;

import static com.socializer.vacuum.network.data.prefs.PrefsModule.NAMED_PREF_DEVICE_NAME;
import static com.socializer.vacuum.network.data.prefs.PrefsModule.NAMED_PREF_SOCIAL;
import static com.socializer.vacuum.utils.Consts.BASE_DEVICE_NAME_PART;

public class AccountActivity extends DaggerAppCompatActivity implements AccountContract.View, AuthenticationDialog.AuthInstListener {

    public static final int FB = 0;
    public static final int VK = 1;
    public static final int INST = 2;


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

    @BindView(R.id.nameLayout)
    LinearLayout nameLayout;

    @BindView(R.id.nameEditText)
    EditText nameEditText;

    @BindView(R.id.editNameBtn)
    ImageView editNameBtn;

    @BindView(R.id.aboutText)
    TextView aboutText;

    @BindView(R.id.vpPlaceholder)
    RelativeLayout vpPh;

    @BindView(R.id.barLayout)
    RelativeLayout barLayout;

    @BindView(R.id.cardView)
    CardView cardView;

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
    private String oldAccName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);

        //show keyboard when push edit btn
        nameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null)
                        imm.showSoftInput(nameEditText, 0);
                }
            }
        });

        //done keyboard button
        nameEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String newName = nameEditText.getText().toString().trim();
                    if (TextUtils.isEmpty(newName)) {
                        onNameChanged(oldAccName);
                    } else {
                        presenter.renameAcc(newName);
                    }
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null)
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        aboutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.vacuum.live/ru/privacy_policy.htm";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        vkButton.setImageAlpha(100);
        fbButton.setImageAlpha(100);
        instButton.setImageAlpha(100);
        setAccountIdFromSP();
        callbackManager = CallbackManager.Factory.create();
        fbCallbackRegistration();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.takeView(this);
        presenter.loadAccount(profileId);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onAccountLoaded(ProfilePreviewDto accountDto) {
        editNameBtn.setVisibility(View.VISIBLE);
        imageList = new ArrayList<>();
        List<ProfileImageDto> imageDtoList = accountDto.getImages();
        if (imageDtoList != null) {
            for (ProfileImageDto imageDto : imageDtoList) {
                imageList.add(imageDto.getUrl());
            }
        }

        viewPager.post(new Runnable() {
            @Override
            public void run() {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) cardView.getLayoutParams();
                params.width = cardView.getHeight();
                params.height = cardView.getHeight();
                cardView.setLayoutParams(params);
            }
        });

        if (imageList.size() > 0) {
            vpPh.setVisibility(View.GONE);
        } else {
            vpPh.setVisibility(View.VISIBLE);
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
        editNameBtn.setVisibility(View.GONE);
        presenter.loadAccount(profileId);
        String deviceName = profileId + BASE_DEVICE_NAME_PART;
        //presenter.restartAdvertising(advertiseCallback, deviceName);
    }

    AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            Timber.d("moe Device share successful");
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            if (errorCode == BleManager.ADVERTISING_ERROR) {
                showErrorDialog(FailTypes.UNKNOWN_ERROR);
            }
            Timber.d("moe Устройству не удалось раздать Bluetooth");
        }
    };

    @Override
    public void showErrorDialog(FailTypes fail) {
        switch (fail) {
            case UNKNOWN_ERROR:
                DialogUtils.showErrorMessage(this, R.string.bluetooth_adv_error);
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

            case AUTH_REQUIRED:
                AuthSession.getInstance().invalidate(this);
                break;
        }
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
            default:
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    String newName = nameEditText.getText().toString().trim();
                    if (TextUtils.isEmpty(newName)) {
                        onNameChanged(oldAccName);
                    } else {
                        presenter.renameAcc(newName);
                    }
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    @OnClick(R.id.nameLayout)
    void onEditNameClick() {
        oldAccName = nameText.getText().toString();
        nameLayout.setVisibility(View.GONE);
        nameEditText.setVisibility(View.VISIBLE);
        nameEditText.getText().clear();
        nameEditText.requestFocus();
    }

    @Override
    public void onNameChanged(String username) {
        nameEditText.setVisibility(View.GONE);
        nameLayout.setVisibility(View.VISIBLE);
        nameText.setText(username);
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
        router.openChatListActivity();
    }

    @Override
    protected void onPause() {
        super.onPause();
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

            case AUTH_REQUIRED:
                AuthSession.getInstance().invalidate(this);
                break;
        }
    }

    @OnClick(R.id.backBtn)
    void onBackClick() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}

package com.socializer.vacuum.activities.main;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.socializer.vacuum.R;
import com.socializer.vacuum.VacuumApplication;
import com.socializer.vacuum.network.data.FailTypes;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto;
import com.socializer.vacuum.network.data.managers.ProfilesManager;
import com.socializer.vacuum.utils.DialogUtils;
import com.socializer.vacuum.utils.ImageUtils;
import com.socializer.vacuum.utils.StringPreference;
import com.socializer.vacuum.utils.ViewUtils;
import com.socializer.vacuum.views.custom.SpannedGridLayoutManager;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;
import timber.log.Timber;

import static com.socializer.vacuum.network.data.prefs.PrefsModule.NAMED_PREF_SOCIAL;
import static com.socializer.vacuum.utils.Consts.LOCATION_PERMISSION_CODE;

public class MainActivity extends DaggerAppCompatActivity implements
        MainContract.View,
        SwipeRefreshLayout.OnRefreshListener {

    public static final int LOGIN_REQUEST_CODE = 1;
    public static final int REQUEST_ENABLE_BT = 2;

    @Inject
    MainPresenter presenter;

    @Inject
    MainRouter router;

    @Inject
    @Named(NAMED_PREF_SOCIAL)
    StringPreference socialSP;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.singleItem)
    LinearLayout singleItem;

    @BindView(R.id.avatarImage)
    ImageView avatarImage;

    @BindView(R.id.nameText)
    TextView nameText;

    private boolean isBluetoothOn;
    private boolean isAdvertising;
    //private boolean testIsLoaded;
    private boolean isDialogShow;
    private ProfilePreviewDto singleItemProfileDto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        VacuumApplication.getInstance().initSocket();
        initViews();
        checkPermissions();
        presenter.setBtName();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.takeView(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        presenter.refresh();
        attemptStartScanAndAdvertising();

/*        if (isBluetoothOn && !testIsLoaded) {
            presenter.loadTestProfiles();
        }*/

    }

    private void attemptStartScanAndAdvertising() {
        if (presenter.isBlueEnable()) {
            isBluetoothOn = true;
            presenter.startScan();
            if (!isAdvertising) {
                //presenter.startAdvertising(/*advertisingCallback*/);
            }
        } else {
            Toast.makeText(this, R.string.bluetooth_canceled, Toast.LENGTH_SHORT).show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    /*AdvertiseCallback advertisingCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            isAdvertising = true;
            //Toast.makeText(getApplicationContext(), "Device share successful", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStartFailure(int errorCode) {
            Timber.e("Advertising onStartFailure: %s", errorCode);
            super.onStartFailure(errorCode);
            isAdvertising = false;
            Toast.makeText(getApplicationContext(), "Устройству не удалось раздать Bluetooth", Toast.LENGTH_SHORT).show();
        }
    };*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, R.string.bluetooth_on, Toast.LENGTH_SHORT).show();
                isBluetoothOn = true;
            } else {
                Toast.makeText(this, R.string.bluetooth_canceled, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onProfileSelected(ProfilePreviewDto previewDto) {
        router.openProfile(previewDto);
    }

    @OnClick(R.id.profileButton)
    void onProfileClick() {
        router.openAccountActivity();
    }

    @OnClick(R.id.chatListBtn)
    void onChatListBtnClick() {
        if (socialSP.get().equals("true")) {
            router.openChatListActivity();
        } else {
            DialogUtils.showErrorMessage(this, R.string.dialog_msg_social_error);
        }
    }

    private void initViews() {
        SpannedGridLayoutManager manager = new SpannedGridLayoutManager(
                new SpannedGridLayoutManager.GridSpanLookup() {
                    @Override
                    public SpannedGridLayoutManager.SpanInfo getSpanInfo(int position) {
                        // Conditions for 2x2 items
                        if (position % 10 == 3 || position % 10 == 7) {
                            return new SpannedGridLayoutManager.SpanInfo(2, 2);
                        } else {
                            return new SpannedGridLayoutManager.SpanInfo(1, 1);
                        }
                    }
                },
                3, // number of columns
                1f // how big is default item
        );

        Configuration configuration = getResources().getConfiguration();
        int screenWidthDp = configuration.screenWidthDp;
        int smallestScreenWidthDp = configuration.smallestScreenWidthDp;

        final float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (screenWidthDp * scale + 0.5f);
        pixels = pixels / 3;

        int dp = ViewUtils.convertPxToDp(pixels);
        int singleItemHeight = dp + 16;
        int singleItemHeightInPix = ViewUtils.convertDpToPx(singleItemHeight);
        ViewGroup.LayoutParams params = singleItem.getLayoutParams();
        params.width = pixels;
        params.height = singleItemHeightInPix;
        singleItem.setLayoutParams(params);
        singleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (singleItemProfileDto != null)
                    router.openProfile(singleItemProfileDto);
            }
        });

        recyclerView.setLayoutManager(manager);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        //recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        //recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void showSingleItem(ProfilePreviewDto profileDto) {
        singleItemProfileDto = profileDto;
        recyclerView.setVisibility(View.GONE);
        singleItem.setVisibility(View.VISIBLE);
        avatarImage.setClipToOutline(true);

        List<ProfilePreviewDto.ProfileImageDto> photos = profileDto.getImages();

        if (photos != null && !photos.isEmpty()) {
            ProfilePreviewDto.ProfileImageDto avatar = photos.get(0);
            setAvatar(avatar.getPreview(), avatar.getUrl());
        } else {
            setAvatarPlaceholder();
        }

        setName(profileDto.getUsername());
    }

    @Override
    public void hideSingleItem() {
        recyclerView.setVisibility(View.VISIBLE);
        singleItem.setVisibility(View.GONE);
    }

    private void setAvatarPlaceholder() {
        new ImageUtils().setImage(avatarImage, null, null,
                R.drawable.default_avatar);
    }

    private void setAvatar(String preview, String url) {
        new ImageUtils().setAuthImage(this, avatarImage, url, preview,
                R.drawable.default_avatar);
    }

    private void setName(String username) {
        nameText.setText(username);
    }

    void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                finish();
            }
        }
    }

    @Override
    public void onRefresh() {
        presenter.refresh();
    }

    @Override
    public void refreshed() {
        //testIsLoaded = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void showErrorNetworkDialog(FailTypes fail) {
        switch (fail) {
            case UNKNOWN_ERROR:
                //new NetworkUtils().logoutError(this);
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        //presenter.clearAdapter();
        presenter.dropView();
        //testIsLoaded = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

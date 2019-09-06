package com.karpov.vacuum.activities.main;

import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Handler;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.karpov.vacuum.VacuumApplication;
import com.karpov.vacuum.di.base.ActivityScoped;
import com.karpov.vacuum.network.data.DtoListCallback;
import com.karpov.vacuum.network.data.FailTypes;
import com.karpov.vacuum.network.data.dto.ProfilePreviewDto;
import com.karpov.vacuum.network.data.dto.ResponseDto;
import com.karpov.vacuum.network.data.managers.ProfilesManager;
import com.karpov.vacuum.services.BleManager;
import com.karpov.vacuum.views.adapters.ProfileAdapter;
import com.karpov.vacuum.views.adapters.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

import static android.os.Looper.getMainLooper;
import static com.karpov.vacuum.utils.Consts.BASE_DEVICE_NAME_PART;

@ActivityScoped
public class MainPresenter implements MainContract.Presenter, RecyclerItemClickListener {
    @Nullable
    MainContract.View view;

    @Inject
    public MainPresenter() {
        adapter = new ProfileAdapter(this);
        bleManager = VacuumApplication.getComponent().getBleManager();
    }

    @Inject
    ProfilesManager profilesManager;

    ProfileAdapter adapter;
    BleManager bleManager;
    ScanCallback scanCallback;
    List<String> devices = new ArrayList<>();

    @Override
    public void takeView(MainContract.View view) {
        this.view = view;
        this.view.setAdapter(adapter);

        initScanCallback();
    }

    @Override
    public void dropView() {
        this.view = null;
    }

    private void initScanCallback() {
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);

                String deviceName = result.getDevice().getName();

                if (!TextUtils.isEmpty(deviceName)) {
                    if (deviceName.contains(BASE_DEVICE_NAME_PART) && !devices.contains(deviceName)) {

                        devices.add(result.getDevice().getName());
                        deviceName = deviceName.split("@")[0];

                        profilesManager.getProfile(deviceName, new DtoListCallback<ResponseDto>() {
                            @Override
                            public void onSuccessful(@NonNull List<ProfilePreviewDto> response) {
                                Timber.d("moe devices.add %s", result.getDevice().getName());
                                adapter.onAdd(response);
                            }

                            @Override
                            public void onFailed(FailTypes fail) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        };
    }

    @Override
    public boolean isBlueEnable() {
        return bleManager.isBlueEnable();
    }

    @Override
    public void startAdvertising(AdvertiseCallback advertiseCallback) {
        bleManager.startAdvertising(advertiseCallback);
    }

    @Override
    public void startScan() {
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                bleManager.scan(scanCallback);
                Timber.d("moe start scan");
            }
        }, 1000);
    }

    @Override
    public void loadTestProfiles() {
        String[] test = {
                "kuiQxGn76OZ0",
                "kpqQ7tA4zvqh",
                "Tjq84ZY4To3k",
                "OhMAyj8Lc7dF",
                "7BSHDyyg5OSS",
                "Cua5P7eYQEnh",
                "NEXqmEx96zfG",
                "2-h0jnE-bqxJ",
                "u2SFrG3ccztK",
                "6i37zP34eO3m",
                "upOI5UZrRNfJ"};
        profilesManager.getUserProfiles(test, new DtoListCallback<ResponseDto>() {
            @Override
            public void onSuccessful(@NonNull List<ProfilePreviewDto> response) {
                adapter.onAddList(response);
                if (view != null)
                    view.refreshed();
            }

            @Override
            public void onFailed(FailTypes fail) {
                Timber.d("moe fail" + fail.name());
            }
        });
    }

    @Override
    public void onClick(int position) {
        if (view != null) {
            ProfilePreviewDto profileDto = adapter.getProfileByPosition(position);
            Timber.d("moe pos click " + position);
            view.onProfileSelected(profileDto);
        }
    }

    @Override
    public void refresh() {
        devices.clear();
        clearAdapter();
        loadTestProfiles();
    }

    @Override
    public void clearAdapter() {
        adapter.clear();
    }
}

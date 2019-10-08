package com.socializer.vacuum.activities.main;

import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Handler;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.socializer.vacuum.VacuumApplication;
import com.socializer.vacuum.di.base.ActivityScoped;
import com.socializer.vacuum.network.data.DtoListCallback;
import com.socializer.vacuum.network.data.FailTypes;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto;
import com.socializer.vacuum.network.data.dto.ResponseDto;
import com.socializer.vacuum.network.data.managers.ProfilesManager;
import com.socializer.vacuum.services.BleManager;
import com.socializer.vacuum.utils.StringPreference;
import com.socializer.vacuum.views.adapters.ProfileAdapter;
import com.socializer.vacuum.views.adapters.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import timber.log.Timber;

import static android.os.Looper.getMainLooper;
import static com.socializer.vacuum.network.data.prefs.PrefsModule.NAMED_PREF_DEVICE_NAME;
import static com.socializer.vacuum.utils.Consts.BASE_DEVICE_NAME_PART;

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

    @Inject
    @Named(NAMED_PREF_DEVICE_NAME)
    StringPreference deviceNameSP;

    ProfileAdapter adapter;
    BleManager bleManager;
    ScanCallback scanCallback;
    List<String> devices = new ArrayList<>();
    List<String> addedUsersId = new ArrayList<>();

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

                String mainDeviceName = result.getDevice().getName();
                String advertDataDeviceName = Objects.requireNonNull(result.getScanRecord()).getDeviceName();

                if (!TextUtils.isEmpty(mainDeviceName))
                    tryAddToDeviceList(mainDeviceName, result);

                if (!TextUtils.isEmpty(advertDataDeviceName) && !advertDataDeviceName.equals(mainDeviceName))
                    tryAddToDeviceList(advertDataDeviceName, result);
            }

            private void tryAddToDeviceList(String deviceName, ScanResult result) {
                if (deviceName.contains(BASE_DEVICE_NAME_PART) && !devices.contains(deviceName)) {
                    devices.add(result.getDevice().getName());

                    deviceName = deviceName.split("@")[0];

                    profilesManager.getProfile(deviceName, new DtoListCallback<ResponseDto>() {
                        @Override
                        public void onSuccessful(@NonNull List<ProfilePreviewDto> response) {
                            ProfilePreviewDto profileDto = response.get(0);
                            String userId = profileDto.getUserId();
                            if (!addedUsersId.contains(userId)) {
                                Timber.d("moe users.add %s", userId);
                                addedUsersId.add(userId);
                                adapter.onAdd(response);
                            }
                        }

                        @Override
                        public void onFailed(FailTypes fail) {
                            if (view != null)
                                view.showErrorNetworkDialog(fail);
                        }
                    });
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
            }

            @Override
            public void onScanFailed(int errorCode) {
                Timber.d("moe onScanFailed %s", errorCode);
                super.onScanFailed(errorCode);
                //bleManager.scan(scanCallback);//TODO настроить перезапуск при фейле
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
        new Handler(getMainLooper()).post(new Runnable() {
              @Override
              public void run() {
                  bleManager.scan(scanCallback);
                  Timber.d("moe start scan");
              }
          }
        );
        /*bleManager.scan(scanCallback);
        Timber.d("moe start scan");*/
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
                if (view != null)
                    view.showErrorNetworkDialog(fail);
            }
        });
    }

    @Override
    public void onClick(int position) {
        if (view != null) {
            ProfilePreviewDto profileDto = adapter.getProfileByPosition(position);
            view.onProfileSelected(profileDto);
        }
    }

    @Override
    public void refresh() {

        devices.clear();
        addedUsersId.clear();
        clearAdapter();
        loadTestProfiles();
    }

    @Override
    public void clearAdapter() {
        adapter.clear();
    }

    @Override
    public void setBtName() {
        String userID = "";
        if (deviceNameSP != null)
            userID = deviceNameSP.get();
        bleManager.setBluetoothAdapterName(userID);
    }
}

package com.karpov.vacuum.activities.main;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.karpov.vacuum.R;
import com.karpov.vacuum.VacuumApplication;
import com.karpov.vacuum.network.data.DtoListCallback;
import com.karpov.vacuum.network.data.FailTypes;
import com.karpov.vacuum.network.data.dto.ProfilePreviewDto;
import com.karpov.vacuum.network.data.dto.ResponseDto;
import com.karpov.vacuum.network.data.managers.ProfilesManager;
import com.karpov.vacuum.services.BleManager;
import com.karpov.vacuum.views.adapters.ProfileAdapter;
import com.karpov.vacuum.views.custom.SpannedGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;
import timber.log.Timber;

import static com.karpov.vacuum.utils.Consts.BASE_DEVICE_NAME_PART;
import static com.karpov.vacuum.utils.Consts.LOCATION_PERMISSION_CODE;

public class MainActivity extends DaggerAppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final int LOGIN_REQUEST_CODE = 1;
    public static final int REQUEST_ENABLE_BT = 2;

    @Inject
    ProfilesManager profilesManager;

    BleManager bleManager;
    ProfileAdapter profileAdapter;

    List<String> devices = new ArrayList<>();

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        SpannedGridLayoutManager manager = new SpannedGridLayoutManager(
                new SpannedGridLayoutManager.GridSpanLookup() {
                    @Override
                    public SpannedGridLayoutManager.SpanInfo getSpanInfo(int position) {
                        // Conditions for 2x2 items
                        if (position == 4) {
                            return new SpannedGridLayoutManager.SpanInfo(2, 2);
                        } else {
                            return new SpannedGridLayoutManager.SpanInfo(1, 1);
                        }
                    }
                },
                3, // number of columns
                1f // how big is default item
        );

        recyclerView.setLayoutManager(manager);
        profileAdapter = new ProfileAdapter();
        recyclerView.setAdapter(profileAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        checkPermissions();
        bleManager = VacuumApplication.getComponent().getBleManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //checkBluetooth();
        //scan();
    }

    private void checkBluetooth() {
        if (!bleManager.isBlueEnable()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, R.string.bluetooth_on, Toast.LENGTH_SHORT).show();
                //notifyListeners(BluetoothState.TURNED_ON);
            } else {
                Toast.makeText(this, R.string.bluetooth_canceled, Toast.LENGTH_SHORT).show();
                //notifyListeners(BluetoothState.CANCELED);
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////
    
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
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "permission granted", Toast.LENGTH_SHORT).show();
                onResume();
            }
        }
    }
    
    private void scan() {
        bleManager.scan(scanCallback);
        /*new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                sendResults();
            }
        }, 10000);*/
    }

    private void sendResults() {

    }

    ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            String deviceName = result.getDevice().getName();
            if (!TextUtils.isEmpty(deviceName)) {
                if (deviceName.contains(BASE_DEVICE_NAME_PART) && !devices.contains(deviceName))
                devices.add(result.getDevice().getName());
                Timber.d("devices.add %s", result.getDevice().getName());
            }
            //testAdapter.onAddAll(devices);
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

    @Override
    public void onRefresh() {
        devices.clear();
        profileAdapter.clear();
        onResume();
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 3000);
    }

    //////////////////////////////////////////////////////////////////////////////////////////

    @OnClick(R.id.adv)
    void onAdvClick() {
        //bleManager.startAdvertising(advertisingCallback);

        ////////load users
        String [] test = {
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
                        Timber.d("moe succ");
                        profileAdapter.onAddList(response, true);
                    }

                    @Override
                    public void onFailed(FailTypes fail) {
                        Timber.d("moe fail" + fail.name());
                    }
                });


                ////////load users

    }

    AdvertiseCallback advertisingCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            Toast.makeText(getApplicationContext(), "Device share successful", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStartFailure(int errorCode) {
            Timber.e("Advertising onStartFailure: %s", errorCode);
            super.onStartFailure(errorCode);
            Toast.makeText(getApplicationContext(), "Device share failed", Toast.LENGTH_SHORT).show();
        }
    };
}

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.karpov.vacuum.R;
import com.karpov.vacuum.VacuumApplication;
import com.karpov.vacuum.services.BleManager;
import com.karpov.vacuum.views.adapters.TestAdapter;

import java.util.ArrayList;
import java.util.List;

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

    BleManager bleManager;
    TestAdapter testAdapter;

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

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        swipeRefreshLayout.setOnRefreshListener(this);

        testAdapter = new TestAdapter();
        recyclerView.setAdapter(testAdapter);

        checkPermissions();
        bleManager = VacuumApplication.getComponent().getBleManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkBluetooth();
        scan();
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
            testAdapter.onAddAll(devices);
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
        testAdapter.clear();
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
        bleManager.startAdvertising(advertisingCallback);
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

package com.karpov.vacuum.activities.main;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.karpov.vacuum.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;
import timber.log.Timber;

public class MainActivity extends DaggerAppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 2;
    private static final int LOCATION_PERMISSION_CODE = 5;


    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private final List<String> deviceList = new ArrayList<>();
    private final List<String> deviceListBatch = new ArrayList<>();

    @BindView(R.id.deviceList)
    TextView deviceListText;

    @BindView(R.id.deviceListBatch)
    TextView deviceListBatchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Timber.d("moe create");


        checkPermissions();

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            mBluetoothAdapter = bluetoothManager.getAdapter();
            mBluetoothAdapter.setName("qq epta");
        }

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
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
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "perm granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ScanSettings buildScanSettings() {
        ScanSettings.Builder builder = new ScanSettings.Builder();
        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
        return builder.build();
    }

    @OnClick(R.id.scan)
    void onScanClick() {
        deviceList.clear();
        deviceListBatch.clear();
        scanLeDevice();
    }

    private void scanLeDevice() {
        final BluetoothLeScanner bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
//        BluetoothLeAdvertiser advertiser =
//                BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();


        if (!mScanning) {
            Timber.d("moe callback getMainLooper1");
            // Stops scanning after a pre-defined scan period.
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    Timber.d("moe callback getMainLooper");
                    bluetoothLeScanner.stopScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            bluetoothLeScanner.startScan(new ArrayList<>(), buildScanSettings(), mLeScanCallback);
            Toast.makeText(getApplicationContext(), "start", Toast.LENGTH_SHORT).show();
        } else {
            mScanning = false;
            bluetoothLeScanner.stopScan(mLeScanCallback);
            Toast.makeText(getApplicationContext(), "stop", Toast.LENGTH_SHORT).show();
        }
    }

    StringBuilder stringBuilder = new StringBuilder();

    private ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Timber.d("moe callback onScanResult");
            stringBuilder
                    .append("\n\n")
                    .append(result.toString());

            deviceListText.setText(stringBuilder);

/*            deviceList.add(String.valueOf(result));
            for (String device: deviceList) {
                deviceListText.append(device);
            }*/
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Timber.d("moe callback onBatchScanResult");

            for (String deviceBatch: deviceListBatch) {
                deviceListBatchText.append(deviceBatch);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Timber.d("moe callback fail");
            Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();
        }
    };


    @OnClick(R.id.adv)
    void onAdvClick() {
        deviceList.clear();
        deviceListBatch.clear();
        advLeDevice();
    }

    private void advLeDevice() {
        BluetoothLeAdvertiser advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();

        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode( AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY )
                .setTxPowerLevel( AdvertiseSettings.ADVERTISE_TX_POWER_HIGH )
                .setConnectable(true)
                .build();

        ParcelUuid pUuid = new ParcelUuid( UUID.fromString( "CDB7950D-73F1-4D4D-8E47-C090502DBD63" ));

        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName( true )
                //.addServiceUuid( pUuid )
                //.addServiceData( pUuid, "DIMAN LOH".getBytes(Charset.forName("UTF-8") ) )
                .build();

        AdvertiseCallback advertisingCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
                Toast.makeText(getApplicationContext(), "adv callback start succ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartFailure(int errorCode) {
                Timber.e("Advertising onStartFailure: %s", errorCode);
                super.onStartFailure(errorCode);
                Toast.makeText(getApplicationContext(), "adv callback start fail", Toast.LENGTH_SHORT).show();
            }
        };

        advertiser.startAdvertising( settings, data, advertisingCallback );
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        onCreate(null);
        Toast.makeText(this, "oncreate", Toast.LENGTH_SHORT).show();
    }
}

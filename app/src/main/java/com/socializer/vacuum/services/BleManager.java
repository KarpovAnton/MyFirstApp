package com.socializer.vacuum.services;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.widget.Toast;

import com.socializer.vacuum.R;

import java.util.List;

import timber.log.Timber;

import static android.os.Looper.getMainLooper;
import static com.socializer.vacuum.activities.main.MainActivity.REQUEST_ENABLE_BT;

public class BleManager {
    public static final int ADVERTISING_ERROR = 123;

    public enum BluetoothState {ENABLE, DISABLE, NOT_SUPPORT, TURNED_ON, CANCELED}

    private Application context;
    private BluetoothAdapter bluetoothAdapter;

    public static final int DEFAULT_SCAN_TIME = 10000;
    private static final int DEFAULT_MAX_MULTIPLE_DEVICE = 7;
    private static final int DEFAULT_OPERATE_TIME = 5000;
    private static final int DEFAULT_CONNECT_RETRY_COUNT = 0;
    private static final int DEFAULT_CONNECT_RETRY_INTERVAL = 5000;

    private int maxConnectCount = DEFAULT_MAX_MULTIPLE_DEVICE;
    private int operateTimeout = DEFAULT_OPERATE_TIME;
    private int reConnectCount = DEFAULT_CONNECT_RETRY_COUNT;
    private long reConnectInterval = DEFAULT_CONNECT_RETRY_INTERVAL;

    private boolean mScanning;

    public BleManager(Application app) {
        if (context == null && app != null) {
            context = app;
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
    }

    //TODO если онСканФейлит попробовать вкл и выкл авиа
    public void setBluetoothAdapterName(String name) {
        bluetoothAdapter.setName(name);
        Timber.d("moe bladapter set name %s", name);
    }

    public Context getContext() {
        return context;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public int getMaxConnectCount() {
        return maxConnectCount;
    }

    public BleManager setMaxConnectCount(int count) {
        if (count > DEFAULT_MAX_MULTIPLE_DEVICE)
            count = DEFAULT_MAX_MULTIPLE_DEVICE;
        this.maxConnectCount = count;
        return this;
    }

    public int getOperateTimeout() {
        return operateTimeout;
    }

    public BleManager setOperateTimeout(int count) {
        this.operateTimeout = count;
        return this;
    }

    public int getReConnectCount() {
        return reConnectCount;
    }

    public long getReConnectInterval() {
        return reConnectInterval;
    }

    public BleManager setReConnectCount(int count) {
        return setReConnectCount(count, DEFAULT_CONNECT_RETRY_INTERVAL);
    }

    public BleManager setReConnectCount(int count, long interval) {
        if (count > 10)
            count = 10;
        if (interval < 0)
            interval = 0;
        this.reConnectCount = count;
        this.reConnectInterval = interval;
        return this;
    }

    public boolean isSupportBle() {
        return context.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public void enableBluetooth() {
        if (bluetoothAdapter != null) {
            bluetoothAdapter.enable();
        }
    }

    public void disableBluetooth() {
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled())
                bluetoothAdapter.disable();
        }
    }

    public boolean isBlueEnable() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    public boolean isBlueEnable(Activity activity) {
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            return true;
        } else {
            Toast.makeText(activity, R.string.bluetooth_canceled, Toast.LENGTH_SHORT).show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return false;
        }
    }

    public List<String> getAllConnectedDevice() {
        return null;
    }

    private ScanSettings buildScanSettings() {
        ScanSettings.Builder builder = new ScanSettings.Builder();
        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
        return builder.build();
    }

    public void scan(ScanCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("ScanCallback can not be Null!");
        }

        final BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        List<ScanFilter> scanFilters = null;
        ScanSettings scanSettings = buildScanSettings();

        if (!mScanning) {
            mScanning = true;
            bluetoothLeScanner.startScan(scanFilters, scanSettings, callback);
        } else {
            mScanning = false;
            bluetoothLeScanner.stopScan(callback);
            Timber.d("moe stop scan");

            new Handler(getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    scan(callback);//TODO
                }
            }, 1000);

        }
    }

    public void cancelScan() {
        //BleScanner.getInstance().stopLeScan();
    }

    public void startAdvertising(AdvertiseCallback callback) {
        BluetoothLeAdvertiser advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();
        if (advertiser == null) {
            callback.onStartFailure(ADVERTISING_ERROR);
            return;
        }

        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(true)
                .build();

        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .build();

        advertiser.startAdvertising(settings, data, callback);
        Timber.d("moe startAdvertising ");
    }

    public void stopAdvertising(AdvertiseCallback callback) {
        BluetoothLeAdvertiser advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();
        advertiser.stopAdvertising(callback);
        Timber.d("moe stopAdvertising ");
    }
}

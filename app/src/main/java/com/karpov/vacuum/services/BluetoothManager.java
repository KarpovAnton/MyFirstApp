package com.karpov.vacuum.services;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

import com.karpov.vacuum.activities.main.MainActivity;

import java.util.List;

import javax.inject.Inject;

import static android.app.Activity.RESULT_OK;

public class BluetoothManager {

    @Inject
    MainActivity mainActivity;

    public BluetoothManager() {
    }//todo

    public enum BluetoothState {ENABLE, DISABLE, NOT_SUPPORT, TURNED_ON, CANCELED}
    public final static int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter bluetoothAdapter;
    private List<BluetoothStateChange> listeners;

    public void addBluetoothListener(BluetoothStateChange listener) {
        if (!listeners.contains(listener))
            this.listeners.add(listener);
    }

    public void removeBluetoothListner(BluetoothStateChange listener) {
        if (listeners.contains(listener))
            this.listeners.remove(listener);
    }

    public boolean isEnabled() {
        return bluetoothAdapter.isEnabled();
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public void enableBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    public void onActivityResult(int requestCode, int resultCode) {
        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode==RESULT_OK){
               //Toast.makeText(context, R.string.bluetooth_on, Toast.LENGTH_SHORT).show();
                notifyListeners(BluetoothState.TURNED_ON);
            } else {
                //Toast.makeText(context, R.string.bluetooth_canceled, Toast.LENGTH_SHORT).show();
                notifyListeners(BluetoothState.CANCELED);
            }
        }
    }

    void notifyListeners(BluetoothState type) {
        for (BluetoothStateChange listener : listeners) {
            listener.onStateChange(type);
        }
    }

    public interface BluetoothStateChange {
        void onStateChange(BluetoothState type);
    }
}

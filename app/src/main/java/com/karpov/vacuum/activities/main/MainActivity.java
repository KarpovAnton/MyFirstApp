package com.karpov.vacuum.activities.main;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.karpov.vacuum.R;
import com.karpov.vacuum.VacuumApplication;
import com.karpov.vacuum.network.data.DtoListCallback;
import com.karpov.vacuum.network.data.FailTypes;
import com.karpov.vacuum.network.data.dto.ProfileListResponseDto;
import com.karpov.vacuum.network.data.dto.ProfilePreviewDto;
import com.karpov.vacuum.network.data.dto.ProfilesRequestDto;
import com.karpov.vacuum.network.data.dto.ResponseDto;
import com.karpov.vacuum.network.data.managers.ProfilesManager;
import com.karpov.vacuum.services.BleManager;
import com.karpov.vacuum.views.adapters.ProfileAdapter;
import com.karpov.vacuum.views.adapters.RecyclerItemClickListener;
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

public class MainActivity extends DaggerAppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        RecyclerItemClickListener {

    public static final int LOGIN_REQUEST_CODE = 1;
    public static final int REQUEST_ENABLE_BT = 2;

    @Inject
    ProfilesManager profilesManager;

    @Inject
    ProfileFragment profileFragment;

    BleManager bleManager;
    ProfileAdapter profileAdapter;

    List<String> devices = new ArrayList<>();

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    private boolean profileFragShown = false;
    private boolean recyclerClickWork = true;

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
        profileAdapter = new ProfileAdapter(this);
        recyclerView.setAdapter(profileAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        checkPermissions();
        bleManager = VacuumApplication.getComponent().getBleManager();

        List<ProfilePreviewDto> test = new ArrayList();
        test.add(new ProfilePreviewDto());
        test.add(new ProfilePreviewDto());test.add(new ProfilePreviewDto());
        test.add(new ProfilePreviewDto());
        test.add(new ProfilePreviewDto());test.add(new ProfilePreviewDto());
        test.add(new ProfilePreviewDto());test.add(new ProfilePreviewDto());
        profileAdapter.onAddList(test, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //checkBluetooth();
        //scan();
        bleManager.startAdvertising(advertisingCallback);
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

        /*profilesManager.getUserProfiles(test, new DtoListCallback<ResponseDto>() {
                    @Override
                    public void onSuccessful(@NonNull List<ProfilePreviewDto> response) {
                        Timber.d("moe succ");
                        profileAdapter.onAddList(response, true);
                    }

                    @Override
                    public void onFailed(FailTypes fail) {
                        Timber.d("moe fail" + fail.name());
                    }
                });*///TODO

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

    //////////////////////////////profile fragment/////////////////////////

    @Override
    public void onClick(int position) {
        Timber.d( "moe onClick profileFragShown" + profileFragShown);
        if (!recyclerClickWork) {
            recyclerClickWork = true;
            return;
        }
        if (!profileFragShown) {

            profileFragShown = true;
            ProfilePreviewDto user = profileAdapter.getProfileByPosition(position);

            profileFragment.configure(swipeRefreshLayout, user);
            replaceFragment(profileFragment);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // I only care if the event is an UP action
        /*if ( event.getAction () == MotionEvent.ACTION_UP )
        {
            //and only is the ListFragment shown.
            //if (isListFragmentShown)
            //{
                // create a rect for storing the fragment window rect
                Rect r = new Rect ( 0, 0, 0, 0 );
                // retrieve the fragment's windows rect
                profileFragment.getView().getHitRect(r);
                // check if the event position is inside the window rect
                boolean intersects = r.contains ( (int) event.getX (), (int) event.getY () );
                // if the event is not inside then we can close the fragment
            Timber.d( "2 moe pressed outside the listFragment");
                if ( !intersects ) {
                    Timber.d( "moe pressed outside the listFragment");
                    FragmentTransaction fragmentTransaction;
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.remove(currentFragment).commit();
                    // notify that we consumed this event
                    return true;
                }
            //}
        }*/
        //let the system handle the event
        return super.onTouchEvent ( event );
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (profileFragShown) {
            if (ev.getAction() == MotionEvent.ACTION_UP) {
                // create a rect for storing the fragment window rect
                Rect r = new Rect(50, 100, 500, 600);
                Timber.d("bb0 "+ ev.getX() +" " + ev.getY());
                // retrieve the fragment's windows rect
                //profileFragment.getView().getHitRect(r);
                // check if the event position is inside the window rect
                boolean intersects = r.contains((int) ev.getX(), (int) ev.getY());
                // if the event is inside then we can close the fragment
                if (intersects) {
                    Timber.d("moe pressed outside 1");
                    Timber.d( "moe ontouch profileFragShown" + profileFragShown);
                    FragmentTransaction fragmentTransaction;
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.remove(profileFragment).commit();
                    //getSupportFragmentManager().executePendingTransactions();
                    profileFragShown = false;
                    recyclerClickWork = false;
                } else {
                    Timber.d("moe pressed outside 2");
                }
            }
        }
        /*View v = getCurrentFocus();
        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];
            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w
                    .getBottom())) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus()
                        .getWindowToken(), 0);
            }
        }*/
        return super.dispatchTouchEvent(ev);
    }

    void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.profileContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

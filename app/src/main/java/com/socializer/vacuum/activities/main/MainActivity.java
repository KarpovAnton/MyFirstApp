package com.socializer.vacuum.activities.main;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.socializer.vacuum.R;
import com.socializer.vacuum.fragments.ProfileFragment;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto;
import com.socializer.vacuum.network.data.managers.ProfilesManager;
import com.socializer.vacuum.views.custom.SpannedGridLayoutManager;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;
import timber.log.Timber;

import static com.socializer.vacuum.utils.Consts.LOCATION_PERMISSION_CODE;

public class MainActivity extends DaggerAppCompatActivity implements
        MainContract.View,
        SwipeRefreshLayout.OnRefreshListener {

    public static final int LOGIN_REQUEST_CODE = 1;
    public static final int REQUEST_ENABLE_BT = 2;

    @Inject
    ProfilesManager profilesManager;

    @Inject
    ProfileFragment profileFragment;

    @Inject
    MainPresenter presenter;

    @Inject
    MainRouter router;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private boolean profileFragShown = false;
    private boolean recyclerClickAvailable = true;
    private boolean blueOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initViews();
        checkPermissions();
        presenter.startAdvertising(advertisingCallback);
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

    @Override
    protected void onResume() {
        super.onResume();
        presenter.takeView(this);
        checkBluetooth();
        if (blueOn) {
            presenter.loadTestProfiles();
            presenter.startScan();
        }
    }

    private void checkBluetooth() {
        if (!presenter.isBlueEnable()) {
            blueOn = false;
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            blueOn = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, R.string.bluetooth_on, Toast.LENGTH_SHORT).show();
                blueOn = true;
                onResume();
                //notifyListeners(BluetoothState.TURNED_ON);
            } else {
                Toast.makeText(this, R.string.bluetooth_canceled, Toast.LENGTH_SHORT).show();
                //notifyListeners(BluetoothState.CANCELED);
            }
        }
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onProfileSelected(ProfilePreviewDto previewDto) {
        if (!profileFragShown) {
            Timber.d("moe profile click");
            router.openProfile(previewDto);
            profileFragShown = true;
            recyclerClickAvailable = false;
        } else {
            if (recyclerClickAvailable) {
                profileFragShown = false;
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (profileFragShown && (ev.getAction() == MotionEvent.ACTION_UP)) {
            // create a rect for storing the fragment window rect
            Rect r = new Rect(50, 100, 500, 600);
            Timber.d("bb0 "+ ev.getX() +" " + ev.getY());
            // retrieve the fragment's windows rect
            //profileFragment.getView().getHitRect(r);
            // check if the event position is inside the window rect
            boolean intersects = r.contains((int) ev.getX(), (int) ev.getY());
            // if the event is inside then we can close the fragment
            if (ev.getX() < 100 || ev.getX() > 980 || ev.getY() < 360 || ev.getY() > 1450 ) {
                //if (intersects) {
                Timber.d("moe pressed outside 1");
                Timber.d( "moe ontouch profileFragShown " + profileFragShown);

                router.removeFragment();
                recyclerClickAvailable = true;
            } else {
                Timber.d("moe pressed inside");
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

    @OnClick(R.id.profileButton)
    void onProfileClick() {
        router.openAccountActivity();
    }

    private void initViews() {
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
        swipeRefreshLayout.setOnRefreshListener(this);
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
                Toast.makeText(getApplicationContext(), "permission granted", Toast.LENGTH_SHORT).show();
                onResume();
            }
        }
    }

    @Override
    public void onRefresh() {
        presenter.refresh();
    }

    @Override
    public void refreshed() {
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
        presenter.dropView();
        presenter.clearAdapter();
    }
}

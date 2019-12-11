package com.socializer.vacuum.activities.photo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.socializer.vacuum.R;
import com.socializer.vacuum.VacuumApplication;
import com.socializer.vacuum.network.data.FailTypes;
import com.socializer.vacuum.network.data.prefs.AuthSession;
import com.socializer.vacuum.utils.DialogUtils;
import com.socializer.vacuum.utils.ImageUtils;
import com.socializer.vacuum.utils.MessageManager;
import com.socializer.vacuum.utils.StringPreference;
import com.socializer.vacuum.views.adapters.PhotoEditAdapter;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;
import timber.log.Timber;

import static com.socializer.vacuum.network.data.prefs.PrefsModule.NAMED_PREF_SOCIAL;
import static com.socializer.vacuum.network.data.prefs.PrefsModule.NAMED_PREF_UNREAD_MSG;

public class PhotoActivity extends DaggerAppCompatActivity implements PhotoContract.View {

    private static final int ADD_IMAGE_REQUEST_CODE = 1;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.newMsgImage)
    ImageView newMsgImage;

    @Inject
    @Named(NAMED_PREF_SOCIAL)
    StringPreference socialSP;

    @Inject
    @Named(NAMED_PREF_UNREAD_MSG)
    StringPreference unreadMsgSP;

    @Inject
    PhotoPresenter presenter;

    @Inject
    PhotoRouter router;

    ArrayList<String> photoList;
    private boolean isDialogShow;
    MessageManager messageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ButterKnife.bind(this);
        String[] photoArray = getIntent().getStringArrayExtra("imageArray");
        if (photoArray != null) {
            photoList = new ArrayList<>(Arrays.asList(photoArray));
        }

        initMessageManager();
        initViews();
        presenter.setPhotos(photoList);
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.dropView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.takeView(this);

        if (unreadMsgSP != null && unreadMsgSP.get().equals("true")) {
            newMsgImage.setVisibility(View.VISIBLE);
        } else {
            newMsgImage.setVisibility(View.GONE);
        }
    }

    private void initMessageManager() {
        messageManager = VacuumApplication.getInstance().getMessageManager();
        messageManager.subscribe(new MessageManager.NewMsgListener() {
            @Override
            public void update(boolean hasNewMsg) {
                Timber.d("zxc update " + hasNewMsg);
                messageManager.changeIconVisibility(hasNewMsg, newMsgImage);
            }
        });
    }

    void initViews() {
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(manager);
    }

    @OnClick({R.id.addPhotoButton})
    void onAddPhotoClick() {
        Intent intent = CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                .getIntent(this);
        startActivityForResult(intent, ADD_IMAGE_REQUEST_CODE);
    }

    @OnClick(R.id.chatListBtn)
    void onChatListBtnClick() {
        router.openChatListActivity();
    }

    @OnClick(R.id.profileButton)
    void onProfileButtonClick() {
        router.openAccountActivity();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri imageUri = result.getUri();

            File photoFile = new File(ImageUtils.getRealPathFromURIAndResize(this, imageUri));
            int size = (int) photoFile.length();
            byte[] bytes = new byte[size];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(photoFile));
                buf.read(bytes, 0, bytes.length);
                buf.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String stringPhoto = Base64.encodeToString(bytes, Base64.DEFAULT);

            presenter.sendPhotoImage(stringPhoto, imageUri.toString());
        }
    }

    @Override
    public void setAdapter(PhotoEditAdapter adapter) {
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRemoveImage(int pos) {
        photoList.remove(pos);
    }//TODO why photoList?

    @Override
    public void onPhotoUploaded() {

    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.dropView();
    }

    @Override
    public void showErrorNetworkDialog(FailTypes fail) {
        switch (fail) {
            case UNKNOWN_ERROR:
                //new NetworkUtils().logoutError(getApplicationContext());
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

            case AUTH_REQUIRED:
                AuthSession.getInstance().invalidate(this);
                break;
        }
    }

    @OnClick(R.id.backBtn)
    void onBackClick() {
        onBackPressed();
    }
}

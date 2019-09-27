package com.socializer.vacuum.activities.account;

import android.content.Intent;
import android.net.Uri;

import com.socializer.vacuum.activities.photo.PhotoActivity;

import javax.inject.Inject;

import static com.socializer.vacuum.activities.account.AccountPresenter.VK_BASE_URL;

public class AccountRouter implements AccountContract.Router {
    @Inject
    AccountActivity activity;

    @Inject
    public AccountRouter() {

    }

    public void openPhotoActivity(String[] photoArray) {
        Intent intent = new Intent(activity.getApplicationContext(), PhotoActivity.class);
        intent.putExtra("imageArray", photoArray);
        activity.startActivity(intent);
    }

    @Override
    public void openVKProfile(String profileId) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vkontakte://profile/".concat(profileId)));
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        } else {
            Intent webViewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(VK_BASE_URL.concat(profileId)));
            activity.startActivity(webViewIntent);
        }
    }
}

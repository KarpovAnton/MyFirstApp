package com.socializer.vacuum.activities.account;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.socializer.vacuum.activities.chatlist.ChatListActivity;
import com.socializer.vacuum.activities.photo.PhotoActivity;

import java.util.List;

import javax.inject.Inject;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static com.socializer.vacuum.activities.account.AccountPresenter.FB_BASE_URL;
import static com.socializer.vacuum.activities.account.AccountPresenter.INST_BASE_URL;
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
    public void openChatListActivity() {
        Intent intent = new Intent(activity.getApplicationContext(), ChatListActivity.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    public void openVKProfile(String profileId) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vkontakte://profile/" + profileId));
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        } else {
            Intent webViewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(VK_BASE_URL + profileId));
            activity.startActivity(webViewIntent);
        }
    }

    @Override
    public void openFBProfile(String profileId) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + profileId));
            activity.startActivity(intent);
        } catch (Exception e) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(FB_BASE_URL + profileId)));
        }
    }

    @Override
    public void openINSTProfile(String profileId) {
        Uri uri = Uri.parse(INST_BASE_URL + "_u/" + profileId);
        Intent inst = new Intent(Intent.ACTION_VIEW, uri);
        inst.setPackage("com.instagram.android");

        if (isIntentAvailable(inst)){
            activity.startActivity(inst);
        } else{
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(INST_BASE_URL + profileId)));
        }
    }

    private boolean isIntentAvailable(Intent intent) {
        final PackageManager packageManager = activity.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}

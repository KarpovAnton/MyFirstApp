package com.socializer.vacuum.fragments.Profile;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.socializer.vacuum.R;
import com.socializer.vacuum.activities.ChatActivity;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto;
import com.socializer.vacuum.network.data.dto.ProfilePreviewDto.ProfileImageDto;
import com.socializer.vacuum.utils.DialogUtils;
import com.socializer.vacuum.utils.StringPreference;
import com.socializer.vacuum.views.adapters.PhotosAdapter;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerFragment;

import static com.socializer.vacuum.activities.account.AccountActivity.FB;
import static com.socializer.vacuum.activities.account.AccountActivity.INST;
import static com.socializer.vacuum.activities.account.AccountActivity.VK;
import static com.socializer.vacuum.activities.account.AccountPresenter.FB_BASE_URL;
import static com.socializer.vacuum.activities.account.AccountPresenter.INST_BASE_URL;
import static com.socializer.vacuum.activities.account.AccountPresenter.VK_BASE_URL;
import static com.socializer.vacuum.network.data.prefs.PrefsModule.NAMED_PREF_SOCIAL;

public class ProfileFragment extends DaggerFragment {

    private ProfilePreviewDto profileDto;
    private boolean callFromChat;
    private String fbProfileId;
    private String vkProfileId;
    private String instProfileId;
    private boolean socialIsBinded;

    PhotosAdapter photosAdapter;

    @Inject
    @Named(NAMED_PREF_SOCIAL)
    StringPreference socialSP;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.vpPlaceholder)
    RelativeLayout vpPlaceholder;

    @BindView(R.id.nameText)
    TextView nameText;

    @BindView(R.id.vkButton)
    ImageView vkButton;

    @BindView(R.id.fbButton)
    ImageView fbButton;

    @BindView(R.id.instButton)
    ImageView instButton;

    @Inject
    public ProfileFragment() {}

    public void configure(ProfilePreviewDto profilePreviewDto) {
        profileDto = profilePreviewDto;
    }

    public void configure(ProfilePreviewDto profilePreviewDto, boolean fromChatActivity) {
        profileDto = profilePreviewDto;
        callFromChat = fromChatActivity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        initViews();
        return view;
    }

    void initViews() {
        photosAdapter = new PhotosAdapter(getContext());

        if (profileDto == null) return;
        List<ProfileImageDto> photos = profileDto.getImages();
        if (!photos.isEmpty()) {
            photosAdapter.setPhotos(photos);
        } else {
            vpPlaceholder.setVisibility(View.VISIBLE);
        }

        viewPager.setAdapter(photosAdapter);

        setName(profileDto.getUsername());
        if (socialSP != null)
            socialIsBinded = socialSP.get().equals("true");
        setSocials();
    }

    private void setName(String username) {
        nameText.setText(username);
    }

    private void setSocials() {
        List<ProfilePreviewDto.ProfileAccountDto> accounts = profileDto.getAccounts();
        for (int i = 0; i < accounts.size(); i++) {
            ProfilePreviewDto.ProfileAccountDto acc = accounts.get(i);
            int accKind = acc.getKind();
            switch (accKind) {
                case FB:
                    fbProfileId = acc.getOid();
                    fbButton.setVisibility(View.VISIBLE);
                    fbButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            /*PackageManager packageManager = getActivity().getPackageManager();
                            try {
                                int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
                                if (versionCode >= 3002850) { //newer versions of fb app
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=" + FB_HOST_URL + fbProfileId));
                                    getActivity().startActivity(intent);
                                } else { //older versions of fb app
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + fbProfileId));
                                    getActivity().startActivity(intent);
                                }
                            } catch (Exception e) {
                                getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(FB_BASE_URL + fbProfileId)));
                            }*/

                            if (socialIsBinded) {
                                try {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + fbProfileId));
                                    getActivity().startActivity(intent);
                                } catch (Exception e) {
                                    getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(FB_BASE_URL + fbProfileId)));
                                }
                            } else {
                                DialogUtils.showErrorMessage(getContext(), R.string.dialog_msg_social_error);
                            }
                        }
                    });
                    break;

                case VK:
                    vkProfileId = acc.getOid();
                    vkButton.setVisibility(View.VISIBLE);
                    vkButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (socialIsBinded) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vkontakte://profile/" + vkProfileId));
                                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                    getActivity().startActivity(intent);
                                } else {
                                    Intent webViewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(VK_BASE_URL + vkProfileId));
                                    getActivity().startActivity(webViewIntent);
                                }
                            } else {
                                DialogUtils.showErrorMessage(getContext(), R.string.dialog_msg_social_error);
                            }
                        }
                    });
                    break;

                case INST:
                    String url = acc.getUrl();
                    if (url != null)
                        instProfileId = url.split("com/")[1];
                    instButton.setVisibility(View.VISIBLE);
                    instButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (socialIsBinded) {
                                Uri uri = Uri.parse(INST_BASE_URL + "_u/" + instProfileId);
                                Intent inst = new Intent(Intent.ACTION_VIEW, uri);
                                inst.setPackage("com.instagram.android");

                                if (isIntentAvailable(inst)) {
                                    getActivity().startActivity(inst);
                                } else {
                                    getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(INST_BASE_URL + instProfileId)));
                                }
                            } else {
                                DialogUtils.showErrorMessage(getContext(), R.string.dialog_msg_social_error);
                            }
                        }

                        private boolean isIntentAvailable(Intent intent) {
                            final PackageManager packageManager = getActivity().getPackageManager();
                            List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                            return list.size() > 0;
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }

    @OnClick(R.id.chatBtn)
    void onChatBtnClick() {
        if (callFromChat) {
/*            for (Fragment fragment : getActivity().getSupportFragmentManager().getFragments()) {
                if (fragment != null)
                    getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }*/
            onBackBtnClick();
            return;
        }

        if (socialIsBinded && profileDto != null) {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            String deviceName = profileDto.getUserId();
            String username = profileDto.getUsername();
            if (profileDto.getImages().size() > 0) {
                String preview = profileDto.getImages().get(0).getPreview();
                intent.putExtra("photo", preview);
            }
            intent.putExtra("username", username);
            intent.putExtra("receiverId", deviceName);
            getActivity().startActivity(intent);
        } else {
            DialogUtils.showErrorMessage(getActivity(), R.string.dialog_msg_social_error);
        }
    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }
}

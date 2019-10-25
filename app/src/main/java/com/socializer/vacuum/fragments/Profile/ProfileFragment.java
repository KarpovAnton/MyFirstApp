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
import static com.socializer.vacuum.activities.account.AccountActivity.NOONE;
import static com.socializer.vacuum.activities.account.AccountActivity.VK;
import static com.socializer.vacuum.activities.account.AccountPresenter.FB_BASE_URL;
import static com.socializer.vacuum.activities.account.AccountPresenter.INST_BASE_URL;
import static com.socializer.vacuum.activities.account.AccountPresenter.VK_BASE_URL;
import static com.socializer.vacuum.network.data.prefs.PrefsModule.NAMED_PREF_SOCIAL;

public class ProfileFragment extends DaggerFragment {

    private ProfilePreviewDto profileDto;
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
        photosAdapter.setPhotos(photos);

        viewPager.setAdapter(photosAdapter);

        setName(profileDto.getUsername());
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
                case NOONE:
                    break;
                default:
                    throw new IllegalStateException("unknown type");
            }
        }
    }

    @OnClick(R.id.chatBtn)
    void onChatBtnClick() {
        if (socialIsBinded) {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            String deviceName = profileDto.getUserId();
            String username = profileDto.getUsername();
            intent.putExtra("receiverId", deviceName);
            intent.putExtra("username", username);
            getActivity().startActivity(intent);
        } else {
            DialogUtils.showErrorMessage(getActivity(), R.string.dialog_msg_social_error);
        }
    }

    @OnClick(R.id.fragmentLayout)
    void onLayoutClick() { }
}

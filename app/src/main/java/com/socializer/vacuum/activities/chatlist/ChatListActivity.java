package com.socializer.vacuum.activities.chatlist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.socializer.vacuum.R;
import com.socializer.vacuum.VacuumApplication;
import com.socializer.vacuum.activities.ChatActivity;
import com.socializer.vacuum.models.chat.Dialog;
import com.socializer.vacuum.models.chat.Message;
import com.socializer.vacuum.models.chat.MessageAuthor;
import com.socializer.vacuum.network.data.FailTypes;
import com.socializer.vacuum.network.data.dto.ResponseDto;
import com.socializer.vacuum.network.data.dto.socket.DialogsResponseDto;
import com.socializer.vacuum.network.data.managers.ChatManager;
import com.socializer.vacuum.network.data.managers.LoginManager;
import com.socializer.vacuum.network.data.prefs.AuthSession;
import com.socializer.vacuum.utils.DialogUtils;
import com.socializer.vacuum.utils.ImageUtils;
import com.socializer.vacuum.utils.StringPreference;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

import static com.socializer.vacuum.network.data.prefs.PrefsModule.NAMED_PREF_SOCIAL;

public class ChatListActivity extends DaggerAppCompatActivity implements ChatListContract.View, SwipeRefreshLayout.OnRefreshListener, DateFormatter.Formatter {

    @Inject
    ChatManager chatManager;

    @Inject
    LoginManager loginManager;

    @Inject
    ChatListRouter router;

    @Inject
    @Named(NAMED_PREF_SOCIAL)
    StringPreference socialSP;

    @BindView(R.id.dialogsList)
    DialogsList dialogsList;

    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    Activity mActivity;
    DialogsListAdapter<Dialog> dialogsListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        ButterKnife.bind(this);
        mActivity = this;

        initViews();
        loadChatList();
    }

    private void initViews() {
        dialogsListAdapter = new DialogsListAdapter<>(R.layout.item_dialog, new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
                new ImageUtils().setImagePreview(imageView, url, R.drawable.default_avatar);
            }
        });
        dialogsListAdapter.setOnDialogClickListener(new DialogsListAdapter.OnDialogClickListener<Dialog>() {
            @Override
            public void onDialogClick(Dialog dialog) {
                String dialogId = dialog.getId();
                String dialogName = dialog.getDialogName();
                String dialogPhoto = dialog.getDialogPhoto();
                Intent intent = new Intent(VacuumApplication.applicationContext, ChatActivity.class);
                /*String deviceName = profileDto.getUserId();
                String username = profileDto.getUsername();*/
                intent.putExtra("receiverId", dialogId);//TODO дилогайди здесь свонго акка, нужно вытаскивать ид собеседника
                intent.putExtra("username", dialogName);
                intent.putExtra("photo", dialogPhoto);
                startActivity(intent);
            }
        });
        dialogsListAdapter.setDatesFormatter(this);
        dialogsList.setAdapter(dialogsListAdapter);
        swipeRefreshLayout.setOnRefreshListener(this);

        /*FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Timber.d(task.getException(), "moe getInstanceId failed");
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        //String msg = getString(R.string.msg_token_fmt, token);
                        Timber.d("moe " + token);
                        //Toast.makeText(this, "gngn", Toast.LENGTH_SHORT).show();

                        loginManager.sendPushToken(token, new DtoCallback<ResponseDto>() {
                            @Override
                            public void onSuccessful(@NonNull ResponseDto response) {
                                Timber.d("moe chat act sendPushToken succ");
                            }

                            @Override
                            public void onFailed(FailTypes fail) {
                                Timber.d("moe chat act sendPushToken fail");
                            }
                        });
                    }
                });*/
    }

    @Override
    public void loadChatList() {
        chatManager.getChatList(new ChatManager.ChatListCallback<ResponseDto>() {
            @Override
            public void onSuccessful(@NonNull List<DialogsResponseDto> response) {
                ArrayList<Dialog> dialogs = new ArrayList<>();
                for (int i = 0; i < response.size(); i++) {
                    DialogsResponseDto dto = response.get(i);
                    String chatId = dto.getUid();
                    String username = dto.getUsername();
                    String lastMsg = dto.getMessage();
                    String preview = dto.getPreview();
                    long ts = dto.getTs();
                    Date date = new Date(ts * 1000L);

                    ArrayList<MessageAuthor> msgAutors = new ArrayList<>();
                    msgAutors.add(new MessageAuthor(chatId, preview, null, true));
                    Dialog dialog = new Dialog(
                            chatId,
                            username,
                            preview,
                            msgAutors,
                            new Message("0", new MessageAuthor(chatId, "", null, true), lastMsg, date),
                            0);

                    dialogs.add(dialog);
                }
                dialogsListAdapter.setItems(dialogs);
                refreshed();
            }

            @Override
            public void onFailed(FailTypes fail) {
                switch (fail) {
                    case UNKNOWN_ERROR:
                        //new NetworkUtils().logoutError(ChatListActivity.this);
                        break;
                    case CONNECTION_ERROR:
                        DialogUtils.showNetworkErrorMessage(ChatListActivity.this);
                        break;
                    case AUTH_REQUIRED:
                        AuthSession.getInstance().invalidate(mActivity);
                        break;
                }
                refreshed();
            }
        });
    }

    @Override
    public String format(Date date) {
        if (DateFormatter.isToday(date)) {
            return DateFormatter.format(date, DateFormatter.Template.TIME);
        } else if (DateFormatter.isYesterday(date)) {
            return getString(R.string.date_header_yesterday);
        } else {
            return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR);
        }
    }

    @Override
    public void onRefresh() {
        dialogsListAdapter.clear();
        loadChatList();
    }

    public void refreshed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @OnClick(R.id.profileButton)
    void onProfileButtonClick() {
        if (socialSP.get().equals("true")) {
            router.openAccountActivity();
        } else {
            DialogUtils.showErrorMessage(this, R.string.dialog_msg_social_error);
        }
    }

    @OnClick(R.id.backBtn)
    void onBackClick() {
        onBackPressed();
    }
}

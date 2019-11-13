package com.socializer.vacuum.activities.chatlist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import com.socializer.vacuum.utils.DialogUtils;
import com.socializer.vacuum.utils.StringPreference;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

import static com.socializer.vacuum.network.data.prefs.PrefsModule.NAMED_PREF_SOCIAL;

public class ChatListActivity extends DaggerAppCompatActivity implements ChatListContract.View {

    @Inject
    ChatManager chatManager;

    @Inject
    ChatListRouter router;

    @Inject
    @Named(NAMED_PREF_SOCIAL)
    StringPreference socialSP;

    @BindView(R.id.dialogsList)
    DialogsList dialogsList;

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
        dialogsListAdapter = new DialogsListAdapter<>(R.layout.item_dialog, null);
        dialogsListAdapter.setOnDialogClickListener(new DialogsListAdapter.OnDialogClickListener<Dialog>() {
            @Override
            public void onDialogClick(Dialog dialog) {
                String dialogId = dialog.getId();
                ArrayList<MessageAuthor> users = dialog.getUsers();
                Intent intent = new Intent(VacuumApplication.applicationContext, ChatActivity.class);
                /*String deviceName = profileDto.getUserId();
                String username = profileDto.getUsername();*/
                intent.putExtra("receiverId", dialogId);//TODO дилогайди здесь свонго акка, нужно вытаскивать ид собеседника
                intent.putExtra("username", dialogId);
                startActivity(intent);
            }
        });
        dialogsList.setAdapter(dialogsListAdapter);
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

                    ArrayList<MessageAuthor> msgAutors = new ArrayList<>();
                    msgAutors.add(new MessageAuthor(chatId, "", null, true));
                    Dialog dialog = new Dialog(chatId,
                            username,
                            "",
                            msgAutors,
                            new Message("0", new MessageAuthor(chatId, "", null, true), lastMsg),
                            0);

                    dialogs.add(dialog);
                }
                dialogsListAdapter.setItems(dialogs);
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
                }
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
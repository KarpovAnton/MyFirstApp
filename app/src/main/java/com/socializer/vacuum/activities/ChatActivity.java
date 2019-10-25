package com.socializer.vacuum.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.socializer.vacuum.R;
import com.socializer.vacuum.VacuumApplication;
import com.socializer.vacuum.models.chat.Message;
import com.socializer.vacuum.models.chat.MessageAuthor;
import com.socializer.vacuum.network.data.FailTypes;
import com.socializer.vacuum.network.data.dto.ResponseDto;
import com.socializer.vacuum.network.data.dto.socket.ChatCallback;
import com.socializer.vacuum.network.data.dto.socket.ChatMessageInDto;
import com.socializer.vacuum.network.data.dto.socket.ChatMessageOutDto;
import com.socializer.vacuum.network.data.dto.socket.LastMessagesResponseDto;
import com.socializer.vacuum.network.data.managers.ChatManager;
import com.socializer.vacuum.utils.DialogUtils;
import com.socializer.vacuum.utils.NetworkUtils;
import com.socializer.vacuum.utils.StringPreference;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import timber.log.Timber;

import static com.socializer.vacuum.network.data.prefs.PrefsModule.NAMED_PREF_DEVICE_NAME;

public class ChatActivity extends DaggerAppCompatActivity {

    private Socket mSocket;
    private String ownId;
    private String receiverId;
    MessagesListAdapter<Message> mAdapter;
    private Gson gson = new Gson();

    private Boolean isConnected = true;

    @Inject
    @Named(NAMED_PREF_DEVICE_NAME)
    StringPreference deviceNameSP;

    @Inject
    ChatManager chatManager;

    @BindView(R.id.nameReceiverText)
    TextView nameReceiverText;

    @BindView(R.id.typingNotifyText)
    TextView typingNotifyText;

    @BindView(R.id.messagesList)
    MessagesList messagesList;

    @BindView(R.id.input)
    MessageInput inputView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        if (deviceNameSP != null)
            ownId = deviceNameSP.get().split("@")[0];
        receiverId = getIntent().getStringExtra("receiverId");
        mSocket = VacuumApplication.getInstance().getSocket();
        initViews();
        loadLastMsgs();

        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on("notifyTyping", onNotifyTyping);
        mSocket.on("notifyStopTyping", onNotifyStopTyping);
        mSocket.on("notifyChatMessage", onNotifyChatMessage);
        mSocket.connect();
    }

    private void loadLastMsgs() {
        chatManager.getLastMsgs(receiverId, new ChatCallback<ResponseDto>() {
            @Override
            public void onSuccessful(@NonNull List<LastMessagesResponseDto> response) {
                ArrayList<Message> lastMessages = new ArrayList<>();
                for (int i = 0; i < response.size(); i++) {
                    LastMessagesResponseDto dto = response.get(i);
                    String textToSend = dto.getMessage().getText();
                    String sender = dto.getSender();

                    MessageAuthor author = new MessageAuthor(sender, "", null, true);
                    Message chatMsg = new Message("0", author, textToSend);
                    lastMessages.add(chatMsg);
                }
                mAdapter.addToEnd(lastMessages, false);
            }

            @Override
            public void onFailed(FailTypes fail) {
                switch (fail) {
                    case UNKNOWN_ERROR:
                        //new NetworkUtils().logoutError(ChatActivity.this);
                        break;
                    case CONNECTION_ERROR:
                        DialogUtils.showNetworkErrorMessage(ChatActivity.this);
                        break;
                }
            }
        });
    }

    private void initViews() {
        nameReceiverText.setText(getIntent().getStringExtra("username"));

        mAdapter = new MessagesListAdapter<>(ownId, null);
        messagesList.setAdapter(mAdapter);

        inputView.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                attemptSend();
                return true;
            }
        });

        inputView.setTypingListener(new MessageInput.TypingListener() {
            @Override
            public void onStartTyping() {
                emitTypeEvent("typing");
            }

            @Override
            public void onStopTyping() {
                emitTypeEvent("stopTyping");
            }
        });
    }

    private void emitTypeEvent(String event) {
        JSONObject json = new JSONObject();
        try {
            json.put("receiver", receiverId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit(event, json);
    }

    private void attemptSend() {
        Timber.d("moe socket %s", mSocket.connected());
        if (!mSocket.connected()) return;

        String textToSend = inputView.getInputEditText().getText().toString();
        ChatMessageOutDto messageDto = new ChatMessageOutDto("0", receiverId, textToSend, "");
        try {
            String messageDtoStr = gson.toJson(messageDto);
            JSONObject json = new JSONObject(messageDtoStr);
            mSocket.emit("chatMessage", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MessageAuthor author = new MessageAuthor(ownId, "", null, true);
        Message chatMsg = new Message("0", author, textToSend);
        mAdapter.addToStart(chatMsg, true);
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isConnected) {
                        Toast.makeText(getApplicationContext(),
                                "ehffewf", Toast.LENGTH_LONG).show();//TODO добавить на беке
                        isConnected = true;
                    }
                }
            });
        }
    };

    private Emitter.Listener onNotifyTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    if (!isValidChat(data)) return;

                    typingNotifyText.setVisibility(View.VISIBLE);
                }
            });
        }
    };

    private Emitter.Listener onNotifyStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            /*new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    if (!isValidChat(data)) return;

                    typingNotifyText.setVisibility(View.GONE);
                }
            });*///TODO приходит 0, трабл на беке
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    typingNotifyText.setVisibility(View.GONE);
                }
            });
        }
    };

    private Emitter.Listener onNotifyChatMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    JsonElement element = gson.fromJson(object.toString(), JsonElement.class);
                    ChatMessageInDto dto = gson.fromJson(element, ChatMessageInDto.class);
                    if (!isValidChat(dto.getSender())) return;

                    MessageAuthor author = new MessageAuthor(receiverId, "", null, true);
                    Message chatMsg = new Message("0", author, dto.getMessage().getText());
                    mAdapter.addToStart(chatMsg, true);
                }
            });
        }
    };

    private boolean isValidChat(JSONObject data) {
        String sender = null;
        String receiver = null;
        try {
            sender = data.getString("sender");
            receiver = data.getString("receiver");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String result;
        if (sender == null) {
            result = receiver;
        } else {
            result = sender;
        }
        return Objects.equals(result, receiverId);
    }

    private boolean isValidChat(String chatId) {
        return receiverId.equals(chatId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.off("notifyTyping", onNotifyTyping);
        mSocket.off("notifyStopTyping", onNotifyStopTyping);
        mSocket.off("notifyChatMessage", onNotifyChatMessage);
    }

    @OnClick({R.id.backImage, R.id.backText})
    void onBackClick() {
        onBackPressed();
    }
}

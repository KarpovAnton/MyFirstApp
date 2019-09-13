package com.socializer.vacuum.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.socializer.vacuum.R;
import com.socializer.vacuum.VacuumApplication;
import com.socializer.vacuum.network.data.dto.socket.ChatMessageDto;

import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;
import io.socket.client.Socket;

public class ChatActivity extends DaggerAppCompatActivity {

    private Socket mSocket;
    private String receiverId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        receiverId = getIntent().getStringExtra("receiverId");
        mSocket = VacuumApplication.getInstance().getSocket();
        mSocket.connect();

        ChatMessageDto messageDto = new ChatMessageDto(0, receiverId, "qq epta", null);
        Gson gson = new Gson();
        String msgStr = gson.toJson(messageDto);

        mSocket.emit("chatMessage", msgStr);
    }


}

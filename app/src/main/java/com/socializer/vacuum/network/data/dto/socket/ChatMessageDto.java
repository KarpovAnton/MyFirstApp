package com.socializer.vacuum.network.data.dto.socket;

import androidx.annotation.Nullable;

public class ChatMessageDto {

    public ChatMessageDto(int id, String receiver, String text, @Nullable String data) {
        this.id = id;
        this.receiver = receiver;
        message = new MessageData(text, data);
    }

    int id;

   String receiver;

   MessageData message;

   public class MessageData {

        public MessageData(String text, @Nullable String data) {
            this.text = text;
            this.data = data;
        }

        String text;

        @Nullable
        String data;
   }
}

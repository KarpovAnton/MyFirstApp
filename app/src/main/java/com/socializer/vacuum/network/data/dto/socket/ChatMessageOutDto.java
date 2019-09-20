package com.socializer.vacuum.network.data.dto.socket;

public class ChatMessageOutDto {

    public ChatMessageOutDto(String id, String receiver, String text, String data) {
        this.id = id;
        this.receiver = receiver;
        message = new MessageData(text, data);
    }

   String id;

   String receiver;

   MessageData message;

    public class MessageData {

        MessageData(String text, String data) {
            this.text = text;
            this.data = data;
        }

        String text;

        String data;

        public String getText() {
            return text;
        }

        public String getData() {
            return data;
        }
    }

    public MessageData getMessage() {
        return message;
    }
}

package com.socializer.vacuum.network.data.dto.socket;

public class ChatMessageInDto {

    int type;

    String sender;

    String receiver;

    Message message;

    public int getType() {
        return type;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public Message getMessage() {
        return message;
    }

    public class Message {

        String text;

        String url;

        String preview;

        public String getText() {
            return text;
        }

        public String getUrl() {
            return url;
        }

        public String getPreview() {
            return preview;
        }
    }
}

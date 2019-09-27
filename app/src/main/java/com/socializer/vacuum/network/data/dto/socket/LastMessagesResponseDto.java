package com.socializer.vacuum.network.data.dto.socket;

import com.socializer.vacuum.network.data.dto.ResponseDto;

public class LastMessagesResponseDto extends ResponseDto {

    String sender;

    public String getSender() {
        return sender;
    }

    public InnerLastMessagesResponseDto getMessage() {
        return message;
    }

    InnerLastMessagesResponseDto message;

    public class InnerLastMessagesResponseDto {

        private String text;

        private String url;

        private String preview;

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

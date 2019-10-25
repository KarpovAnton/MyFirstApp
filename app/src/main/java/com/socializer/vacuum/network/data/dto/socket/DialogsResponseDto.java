package com.socializer.vacuum.network.data.dto.socket;

import com.socializer.vacuum.network.data.dto.ResponseDto;

public class DialogsResponseDto extends ResponseDto {

    String uid;

    int ts;

    String message;

    String preview;

    String username;

    public String getUid() {
        return uid;
    }

    public int getTs() {
        return ts;
    }

    public String getMessage() {
        return message;
    }

    public String getPreview() {
        return preview;
    }

    public String getUsername() {
        return username;
    }
}

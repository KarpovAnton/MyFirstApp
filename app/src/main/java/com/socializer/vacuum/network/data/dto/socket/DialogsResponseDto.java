package com.socializer.vacuum.network.data.dto.socket;

import com.socializer.vacuum.network.data.dto.ResponseDto;

public class DialogsResponseDto extends ResponseDto {

    String uid;

    long ts;

    String message;

    boolean hasnew;

    String preview;

    String username;

    public String getUid() {
        return uid;
    }

    public long getTs() {
        return ts;
    }

    public String getMessage() {
        return message;
    }

    public boolean isHasnew() {
        return hasnew;
    }

    public String getPreview() {
        return preview;
    }

    public String getUsername() {
        return username;
    }
}

package com.socializer.vacuum.network.data.dto.socket;

import com.socializer.vacuum.network.data.dto.ResponseDto;

public class DialogsResponseDto extends ResponseDto {

    DialogsResponseInnerDto _id;

    public class DialogsResponseInnerDto {

        String u1;

        long ts;

        public String getU1() {
            return u1;
        }

        public long getTs() {
            return ts;
        }
    }

    public DialogsResponseInnerDto get_id() {
        return _id;
    }
}

package com.socializer.vacuum.network.data.dto;

import java.util.ArrayList;

public class SendPushTokenRequestDto {

    public SendPushTokenRequestDto(String access_token) {
        accounts.add(new InnerData(3, access_token));
    }

    ArrayList<InnerData> accounts = new ArrayList<>();

    public class InnerData {

        public InnerData(int kind, String access_token) {
            this.kind = kind;
            this.access_token = access_token;
        }

        int kind;

        String access_token;
    }
}

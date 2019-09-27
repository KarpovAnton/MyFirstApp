package com.socializer.vacuum.network.data.dto;

import java.util.ArrayList;

public class BindSocialRequestDto {

    public BindSocialRequestDto(int kind, String url, String oid, String access_token) {
        accounts.add(new InnerData(kind, url, oid, access_token));
    }

    ArrayList<InnerData> accounts = new ArrayList<>();

    public class InnerData {

        public InnerData(int kind, String url, String oid, String access_token) {
            this.kind = kind;
            this.url = url;
            this.oid = oid;
            this.access_token = access_token;
        }

        int kind;

        String url;

        String oid;

        String access_token;
    }
}

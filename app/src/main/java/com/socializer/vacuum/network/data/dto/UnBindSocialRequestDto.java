package com.socializer.vacuum.network.data.dto;

import java.util.ArrayList;

public class UnBindSocialRequestDto {

    public UnBindSocialRequestDto(int kind) {
        this.accounts.add(new InnerData(kind));
    }

    ArrayList<InnerData> accounts = new ArrayList<>();

    public class InnerData {

        int kind;

        String action;

        public InnerData(int kind) {
            this.kind = kind;
            this.action = "1";
        }
    }
}

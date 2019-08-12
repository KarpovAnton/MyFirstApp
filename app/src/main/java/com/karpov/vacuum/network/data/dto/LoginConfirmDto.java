package com.karpov.vacuum.network.data.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by andrej on 21.11.2018.
 */

public class LoginConfirmDto  {

    @SerializedName("sms_code")
    private String smsCode;

    public LoginConfirmDto(String code) {
        this.smsCode = code;
    }

    public String getCode() {
        return smsCode;
    }
}

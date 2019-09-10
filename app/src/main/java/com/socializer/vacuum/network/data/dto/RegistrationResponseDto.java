package com.socializer.vacuum.network.data.dto;

import com.google.gson.annotations.SerializedName;

public class RegistrationResponseDto extends ResponseDto {

    @SerializedName("user_id")
    private String userId;

    public String getUserId() {
        return userId;
    }
}






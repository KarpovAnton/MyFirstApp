package com.karpov.vacuum.network.data.dto;

import com.google.gson.annotations.SerializedName;

public class ProfilesRequestDto {

    @SerializedName("user_id")
    private String[] userId;

    public ProfilesRequestDto(String[] userId) {
        this.userId = userId;
    }
}

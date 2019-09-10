package com.socializer.vacuum.network.data.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by andrej on 28.11.2018.
 */

public class AccountChangeRequestDto {

    String name;

    String address;

    @SerializedName("avatar_preview")
    private String avatarPreview;

    public AccountChangeRequestDto(String name, String address, String preview) {
        this.name = name;
        this.address = address;
        this.avatarPreview = preview;
    }
}

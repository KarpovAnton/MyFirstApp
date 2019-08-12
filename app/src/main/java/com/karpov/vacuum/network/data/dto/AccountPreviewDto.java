package com.karpov.vacuum.network.data.dto;

import com.google.gson.annotations.SerializedName;

public class AccountPreviewDto {
    long id;

    @SerializedName("name")
    String name;

    String address;

    @SerializedName("avatar_preview")
    String avatarPreview;

    @SerializedName("avatar_url")
    String avatarUrl;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getAvatarPreview() {
        return avatarPreview;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
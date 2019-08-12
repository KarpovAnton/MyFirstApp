package com.karpov.vacuum.network.data.dto;

import com.google.gson.annotations.SerializedName;
import com.karpov.vacuum.utils.StringUtils;

public class AccountResponseDto extends ResponseDto {
    long id;

    String name;

    @SerializedName("avatar_preview")
    String avatarPreview;

    @SerializedName("avatar_url")
    String avatarUrl;

    @SerializedName("raw_balance")
    int rawBalance;

    @SerializedName("hold_balance")
    int holdBalance;

    String phone;

    String email;

    String address;

    int permissions;

    @SerializedName("created_at")
    long timestamp;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return StringUtils.formatPhoneNumber(phone);
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getBalance() {
        return rawBalance;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getAvatarPreview() {
        return avatarPreview;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "AccountResponseDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}

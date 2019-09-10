package com.karpov.vacuum.network.data.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PhotoResponseDto extends ResponseDto {

    @SerializedName("user_id")
    String userId;

    String username;

    int status;

    boolean isauth;

    List<PhotoImagesInnerDto> images;

    public class PhotoImagesInnerDto {

        private String url;

        private String preview;

        private int kind;

        public String getUrl() {
            return url;
        }

        public String getPreview() {
            return preview;
        }

        public int getKind() {
            return kind;
        }
    }

    List<PhotoAccountsInnerDto> accounts;

    public class PhotoAccountsInnerDto {
    }

    int weight;

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public int getStatus() {
        return status;
    }

    public boolean isIsauth() {
        return isauth;
    }

    public List<PhotoImagesInnerDto> getImages() {
        return images;
    }

    public List<PhotoAccountsInnerDto> getAccounts() {
        return accounts;
    }

    public int getWeight() {
        return weight;
    }
}

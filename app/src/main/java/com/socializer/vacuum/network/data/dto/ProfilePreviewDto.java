package com.socializer.vacuum.network.data.dto;

import com.google.gson.annotations.SerializedName;
import com.socializer.vacuum.commons.adapter.ViewType;
import com.socializer.vacuum.views.adapters.ProfileAdapter;

import java.util.List;

public class ProfilePreviewDto extends ResponseDto implements ViewType {

    @Override
    public int getViewType() {
        return ProfileAdapter.TYPE_PROFILE;
    }

    @SerializedName("user_id")
    private String userId;

    private int statusCode;

    private String error;

    private String username;

    private int status;

    private List<ProfileImageDto> images;

    private List<ProfileAccountDto> accounts;

    private int weight;

    public class ProfileImageDto {

        @SerializedName("_id")
        private String id;

        private String url;

        private String preview;

        private int kind;

        public String getId() {
            return id;
        }

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

    public class ProfileAccountDto {

        //private String url;

        //private String oid;

        private int kind;

        private int getKind() {
            return kind;
        }
    }

    public String getUserId() {
        return userId;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getError() {
        return error;
    }

    public String getUsername() {
        return username;
    }

    public int getStatus() {
        return status;
    }

    public List<ProfileImageDto> getImages() {
        return images;
    }

    public List<ProfileAccountDto> getAccounts() {
        return accounts;
    }

    public int getWeight() {
        return weight;
    }
}

package com.socializer.vacuum.network.data.dto;


public class LoginSocialRequestDto {

    public LoginSocialRequestDto(String oid, String access_token) {
        this.oid = oid;
        this.access_token = access_token;
    }

    private String oid;

    private String access_token;

}
package com.socializer.vacuum.network.data.dto;


public class LoginRequestDto {

    public LoginRequestDto(String userId, String password) {
        this.username = userId;
        this.password = password;
        grant_type = "password";
    }

    private String username;

    private String password;

    private String grant_type;

    private String client_id = "982C1C03-0F2E-49B4-8B4C-4BEE374545D3";

    private String client_secret = ")2{/oHTXSQxF@V^&I2-&25rW*G0QBH";
}
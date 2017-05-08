package com.example.nfctransfer.networking.ApiResponses;

public class AuthResponse implements IApiResponse{

    private int status;
    private String message;
    private String access_token;
    private String expires_in;
    private String userid;

    public int getStatus(){
        return status;
    }

    public String getMessage() { return message; }

    public String getExpiresIn() { return expires_in; }

    public String getAccessToken(){
        return access_token;
    }

    public String getUserId(){
        return userid;
    }

}

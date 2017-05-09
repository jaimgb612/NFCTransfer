package com.example.nfctransfer.networking.ApiResponses;

public class RegisterResponse implements IApiResponse {

    private int status;
    private String message;
    private String userid;

    public int getStatus(){
        return status;
    }

    public String getMessage() { return message; }


    public String getUserId(){
        return userid;
    }
}

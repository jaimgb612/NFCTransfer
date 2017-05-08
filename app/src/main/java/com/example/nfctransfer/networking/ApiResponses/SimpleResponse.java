package com.example.nfctransfer.networking.ApiResponses;

public class SimpleResponse implements IApiResponse {

    private int status;

    private String message;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}

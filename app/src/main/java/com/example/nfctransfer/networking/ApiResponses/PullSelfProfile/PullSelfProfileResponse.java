package com.example.nfctransfer.networking.ApiResponses.PullSelfProfile;

import com.example.nfctransfer.networking.ApiResponses.IApiResponse;

public class PullSelfProfileResponse implements IApiResponse {

    int status;
    SelfProfile user;

    public int getStatus() {
        return this.status;
    }

    public SelfProfile getUser() {
        return this.user;
    }
}

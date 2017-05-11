package com.example.nfctransfer.networking.ApiResponses.Profile;

import com.example.nfctransfer.networking.ApiResponses.IApiResponse;
import com.example.nfctransfer.networking.ApiResponses.Profile.Profile;

public class PullProfileResponse implements IApiResponse {

    int status;
    Profile user;

    public int getStatus() {
        return this.status;
    }

    public Profile getUser() {
        return this.user;
    }
}

package com.example.nfctransfer.networking;

import com.example.nfctransfer.networking.ApiResponses.AuthResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface NfcTransferService {

    String INDEX_CELLPHONE = "cellphone";
    String INDEX_PASSWORD = "password";

    // AUTHENTICATE USER
    @FormUrlEncoded
    @POST("auth/login/smart")
    Call<AuthResponse> authenticate(@Field(INDEX_CELLPHONE) String cellphone,
                                    @Field(INDEX_PASSWORD) String password);


}

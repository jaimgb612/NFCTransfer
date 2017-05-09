package com.example.nfctransfer.networking;

import com.example.nfctransfer.networking.ApiResponses.AuthResponse;
import com.example.nfctransfer.networking.ApiResponses.RegisterResponse;
import com.example.nfctransfer.networking.ApiResponses.SimpleResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface NfcTransferService {

    String INDEX_USERID = "userid";
    String INDEX_TOKEN = "token";
    String INDEX_CELLPHONE = "cellphone";
    String INDEX_PASSWORD = "password";
    String INDEX_FIRSTNAME = "firstname";
    String INDEX_LASTNAME = "lastname";

    // AUTHENTICATE USER
    @FormUrlEncoded
    @POST("auth/token")
    Call<AuthResponse> authenticate(@Field(INDEX_CELLPHONE) String cellphone,
                                    @Field(INDEX_PASSWORD) String password);

    // REGISTER USER
    @FormUrlEncoded
    @POST("/register")
    Call<RegisterResponse> register(@Field(INDEX_CELLPHONE) String cellphone,
                                    @Field(INDEX_PASSWORD) String password,
                                    @Field(INDEX_FIRSTNAME) String firstname,
                                    @Field(INDEX_LASTNAME) String lastname);

    // SEND ACTIVATION
    @FormUrlEncoded
    @POST("/send_activation")
    Call<SimpleResponse> sendActivationCode(@Field(INDEX_USERID) String userId);

    // VERIFY ACTIVATION CODE
    @FormUrlEncoded
    @POST("/verify")
    Call<SimpleResponse> verifyAccount(@Field(INDEX_USERID) String userId,
                                       @Field(INDEX_TOKEN) String token);
}


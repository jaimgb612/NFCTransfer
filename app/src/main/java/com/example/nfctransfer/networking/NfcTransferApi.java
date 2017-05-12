package com.example.nfctransfer.networking;

import android.content.Context;

import com.example.nfctransfer.networking.ApiResponses.AuthResponse;
import com.example.nfctransfer.networking.ApiResponses.Profile.PullProfileResponse;
import com.example.nfctransfer.networking.ApiResponses.RegisterResponse;
import com.example.nfctransfer.networking.ApiResponses.SimpleResponse;
import com.example.nfctransfer.sharedPreferences.Preferences;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class NfcTransferApi {

    private Context context;
    private Retrofit retrofit;
    private NfcTransferService  service;
    private String  userId;
    private String  userToken;

    private NfcTransferApi() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://francoisseminerio.me")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.service = retrofit.create(NfcTransferService.class);
    }

    private static NfcTransferApi INSTANCE = new NfcTransferApi();

    public static NfcTransferApi getInstance()
    {	return INSTANCE;
    }

    public Retrofit getRetrofitInstance() {
        return retrofit;
    }

    private void refreshCredentials() {
        this.userId = Preferences.getInstance().getSavedUserId(context);
        this.userToken = Preferences.getInstance().getUserAccessToken(context);
    }

    public Call<AuthResponse> authenticate(String cellphone, String password) {
        return (service.authenticate(cellphone, password));
    }

    public Call<RegisterResponse> register(String cellphone, String password, String firstname, String lastname) {
        return (service.register(cellphone, password, firstname, lastname));
    }

    public Call<SimpleResponse> sendActivation(String userId) {
        return (service.sendActivationCode(userId));
    }

    public Call<SimpleResponse> verify(String userId, String token) {
        return (service.verifyAccount(userId, token));
    }

    public Call<PullProfileResponse> getSelfProfileData(String accessToken) {
        return (service.pullProfileData(accessToken));
    }

    public Call<PullProfileResponse> getTargetProfileData(String accessToken, String targetId) {
        return (service.pullTargetProfileData(targetId, accessToken));
    }

    public Call<SimpleResponse> addProfileField(String accessToken, String fieldName, String textValue, String socialId) {
        return (service.addProfileField(fieldName, accessToken, textValue, socialId));
    }

    public Call<SimpleResponse> editProfileField(String accessToken, String fieldName, String textValue, String socialId, boolean sharedStatus) {
        return (service.editProfileField(fieldName, accessToken, textValue, socialId, sharedStatus));
    }

    public Call<SimpleResponse> deleteProfileField(String accessToken, String fieldName) {
        return (service.deleteProfileField(fieldName, accessToken));
    }

    public Call<SimpleResponse> notifyBeamedUser(String accessToken, String targetId) {
        return (service.notifyBeamedUser(targetId, accessToken));
    }
}

package com.example.nfctransfer.networking;

import android.content.Context;

import com.example.nfctransfer.networking.ApiResponses.AuthResponse;
import com.example.nfctransfer.sharedPreferences.Preferences;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class NfcTransferApi {

    private Context context;
    private NfcTransferService  service;
    private String  userId;
    private String  userToken;

    private NfcTransferApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("localhost:3000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.service = retrofit.create(NfcTransferService.class);
    }

    private static NfcTransferApi INSTANCE = new NfcTransferApi();

    public static NfcTransferApi getInstance()
    {	return INSTANCE;
    }

    public NfcTransferApi(Context _context) {
        this.context = _context;
    }

    private void refreshCredentials() {
        this.userId = Preferences.getInstance().getSavedUserId(context);
        //this.userToken = Preferences.getInstance().getSavedUserToken(context);
    }

    public Call<AuthResponse> authenticate(String cellphone, String password) {
        return (service.authenticate(cellphone, password));
    }
}

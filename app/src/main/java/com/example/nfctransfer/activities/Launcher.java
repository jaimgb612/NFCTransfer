package com.example.nfctransfer.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.nfctransfer.networking.NfcTransferApi;
import com.example.nfctransfer.networking.Session;
import com.example.nfctransfer.sharedPreferences.Preferences;

public class Launcher extends Activity {

    private static final String TWITTER_KEY = "WzUWAjqUgPFtEu59YzH5LDvDL";
    private static final String TWITTER_SECRET = "dq610ftAN4l69z9Vmdzb1k3Og2O9NYDiqhY9Bz1FgpnddcAnuP";

    public void init(){
        //FacebookSdk.sdkInitialize(getApplicationContext());
        //TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        //Fabric.with(this, new Twitter(authConfig));
        NfcTransferApi.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = getApplicationContext();

        init();

        String userId = Preferences.getInstance().getSavedUserId(context);
        String accessToken = Preferences.getInstance().getUserAccessToken(context);

        if (accessToken == null || userId == null) {
            startActivity(new Intent(context, LoginActivity.class));
            finish();
        }
        else {
            Session.userId = userId;
            Session.accessToken = accessToken;
            startActivity(new Intent(context, MainActivity.class));
            finish();
        }
    }
}
package com.example.nfctransfer.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.nfctransfer.networking.NfcTransferApi;
import com.example.nfctransfer.networking.Session;
import com.example.nfctransfer.sharedPreferences.Preferences;
import com.example.nfctransfer.socialAPIs.FacebookAPI;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

public class Launcher extends Activity {

    private static final String TWITTER_KEY = "9yC2j5hJUaO1GtLcRZZPZHOLI";
    private static final String TWITTER_SECRET = "YY72XRSNnnUmMobwTExCnylqBicuDoUNahqVADLI7NV1ljPisb";

    public void init(){
        FacebookSdk.sdkInitialize(getApplicationContext());
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
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

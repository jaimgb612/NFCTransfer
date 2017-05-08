package com.example.nfctransfer.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.nfctransfer.networking.NfcTransferApi;
import com.example.nfctransfer.sharedPreferences.Preferences;

public class Launcher extends Activity {

    private static final String TWITTER_KEY = "WzUWAjqUgPFtEu59YzH5LDvDL";
    private static final String TWITTER_SECRET = "dq610ftAN4l69z9Vmdzb1k3Og2O9NYDiqhY9Bz1FgpnddcAnuP";

    private void manageAuth(boolean hasAccessToken){

        Intent intent;

        if (!hasAccessToken) {
            intent = new Intent(this, LoginActivity.class);
        }
        else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }


    public void init(){
        //FacebookSdk.sdkInitialize(getApplicationContext());
        //TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        //Fabric.with(this, new Twitter(authConfig));
        NfcTransferApi.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean hasStoredAccessToken;

        init();

        if (Preferences.getInstance().getFirstLaunch(this)){
            Preferences.getInstance().updateFirstLaunch(false, this);
            // launch demo
        }

        if (Preferences.getInstance().getSavedUserId(this) != null &&
                Preferences.getInstance().getUserAccessToken(this) != null) {
            hasStoredAccessToken = false;
        }
        else {
            hasStoredAccessToken = true;
        }
        manageAuth(hasStoredAccessToken);
    }
}

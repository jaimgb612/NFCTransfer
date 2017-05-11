package com.example.nfctransfer.socialAPIs;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public abstract class ASocialAPI {

    public interface ApiSyncCallBack {
        void synchronizationResult(boolean status);
    }

    public interface ApiProfileSyncCallBack {
       void onProfileSynchronized(SynchronizableElement element);
    }

    protected boolean connected;
    protected static final String PREFS_NAME = "yoki_preferences";
    protected static String API_ACCESS_TOKEN = "access_token";
    protected static String API_ACCESS_TOKEN_EXPIRATION = "access_token_expiration";
    protected static String API_AUTHENTICATION_STATUS = "authentication_status";


    public ASocialAPI(String apiName){
        connected = false;
        API_ACCESS_TOKEN = apiName + "_" + API_ACCESS_TOKEN;
        API_ACCESS_TOKEN_EXPIRATION = apiName + "_" + API_ACCESS_TOKEN_EXPIRATION;
        API_AUTHENTICATION_STATUS = apiName + "_" + API_AUTHENTICATION_STATUS;
    }

    public boolean isConnected() {
        return (connected);
    }

    private SharedPreferences getSharedPreferences(Context context){
        return (context.getSharedPreferences(PREFS_NAME, 0));
    }

    public void clearSavedApiData(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(API_ACCESS_TOKEN);
        editor.remove(API_ACCESS_TOKEN_EXPIRATION);
        editor.apply();
    }

//    public void saveGeneratedAccessToken(Context context, String authToken) {
//        getSharedPreferences(context).edit().putString(API_ACCESS_TOKEN, authToken).apply();
//    }
//
//    public String getGeneratedAccessToken(Context context) {
//        return (getSharedPreferences(context).getString(API_ACCESS_TOKEN, null));
//    }
//
//    public void saveGeneratedAccessTokenExpiration(Context context, Long authTokenExpiration) {
//        getSharedPreferences(context).edit().putLong(API_ACCESS_TOKEN_EXPIRATION, authTokenExpiration).apply();
//    }
//
//    public Long getGeneratedAccessTokenExpiration(Context context) {
//        return (getSharedPreferences(context).getLong(API_ACCESS_TOKEN_EXPIRATION, 0));
//    }
//
//    public void saveAuthenticationStatus(Context context, boolean status) {
//        getSharedPreferences(context).edit().putBoolean(API_AUTHENTICATION_STATUS, status).apply();
//    }
//
//    public boolean getAuthenticationStatus(Context context) {
//        return (getSharedPreferences(context).getBoolean(API_AUTHENTICATION_STATUS, false));
//    }

    public abstract boolean isApplicationInstalled(Context context);
    public abstract void synchronizeWithSocialApi(Context context, Activity activity, ApiSyncCallBack callBack);
    public abstract void getProfileData(Context context, Activity activity, ApiProfileSyncCallBack callBack);
}
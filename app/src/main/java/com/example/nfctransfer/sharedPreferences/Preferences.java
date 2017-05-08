package com.example.nfctransfer.sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    public static final String PREFS_NAME = "nfc_transfer_app_preferences";

    private Preferences() {}

    private static Preferences INSTANCE = new Preferences();

    public static Preferences getInstance()
    {	return INSTANCE;
    }

    public SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(PREFS_NAME, 0);
    }

    public void saveUserId(String _userId, Context context){
        SharedPreferences userData = context.getSharedPreferences(PREFS_NAME, 0);
        userData.edit().putString("user_id", _userId).apply();
    }

    public String getSavedUserId(Context context){
        SharedPreferences userData = context.getSharedPreferences(PREFS_NAME, 0);
        return (userData.getString("user_id", null));
    }

    public void saveAccessToken(String _token, Context context){
        SharedPreferences userData = context.getSharedPreferences(PREFS_NAME, 0);
        userData.edit().putString("access_token", _token).apply();
    }

    public String getUserAccessToken(Context context){
        SharedPreferences userData = context.getSharedPreferences(PREFS_NAME, 0);
        return (userData.getString("access_token", null));
    }

    public void saveUserPhoneNumber(String _userPhoneNumber, Context context){
        SharedPreferences userData = context.getSharedPreferences(PREFS_NAME, 0);
        userData.edit().putString("user_phone_number", _userPhoneNumber).apply();
    }

    public String getSavedUserPhoneNumber(Context context){
        SharedPreferences userData = context.getSharedPreferences(PREFS_NAME, 0);
        return (userData.getString("user_phone_number", null));
    }

    public void deleteSavedUserPhoneNumber(Context context){
        SharedPreferences.Editor Editor = context.getSharedPreferences(PREFS_NAME, 0).edit();
        Editor.remove("user_phone_number").apply();
    }

    public void updateFirstLaunch(boolean _updatedValue, Context context){
        SharedPreferences userData = context.getSharedPreferences(PREFS_NAME, 0);
        userData.edit().putBoolean("first_app_launch", _updatedValue).commit();
    }

    public boolean getFirstLaunch(Context context){
        SharedPreferences userData = context.getSharedPreferences(PREFS_NAME, 0);
        return (userData.getBoolean("first_app_launch", true));
    }

    public void deleteSavedCredentials(Context context) {
        SharedPreferences.Editor Editor = context.getSharedPreferences(PREFS_NAME, 0).edit();
        Editor.remove("user_id");
        Editor.remove("access_token");
        Editor.apply();
    }
}

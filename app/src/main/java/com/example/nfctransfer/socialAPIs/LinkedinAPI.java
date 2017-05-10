package com.example.nfctransfer.socialAPIs;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import org.json.JSONException;
import org.json.JSONObject;

public class LinkedinAPI extends ASocialAPI {

    private static final String API_NAME = "linkedin";
    private static final String ACCESS_TOKEN_VALUE = "accessTokenValue";
    private static final String ACCESS_TOKEN_EXPIRATION_VALUE = "expiresOn";

    public LinkedinAPI(){
        super(API_NAME);
    }

    @Override
    public boolean isApplicationInstalled(Context context) {
        try {
            context.getPackageManager().getPackageInfo("com.linkedin.android", 0);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    private void authenticateWithoutAccessToken(final Context context, final Activity activity, final ApiSyncCallBack callBack) {
//        LISessionManager.getInstance(context).init(activity, buildLinkedinScope(), new AuthListener() {
//            @Override
//            public void onAuthSuccess() {
//                JSONObject obj;
//                try {
//                    obj = new JSONObject(LISessionManager.getInstance(context).getSession().getAccessToken().toString());
//                    saveGeneratedAccessToken(context, obj.getString(ACCESS_TOKEN_VALUE));
//                    saveGeneratedAccessTokenExpiration(context, obj.getLong(ACCESS_TOKEN_EXPIRATION_VALUE));
//                    callBack.synchronizationResult(true);
//                    saveAuthenticationStatus(context, true);
//                    connected = true;
//
//                } catch (JSONException e) {
//                    callBack.synchronizationResult(false);
//                    saveAuthenticationStatus(context, false);
//                    connected = false;
//                    e.printStackTrace();
//                }
//            }
//            @Override
//            public void onAuthError(LIAuthError error) {
//                callBack.synchronizationResult(false);
//                saveAuthenticationStatus(context, false);
//                connected = false;
//            }
//        }, true);
    }

    private boolean authenticateWithAccessToken(Context context, String accessTokenValue) {
//        Long accessTokenExpirationTime = getGeneratedAccessTokenExpiration(context);
//
//        AccessToken accessToken = new AccessToken(accessTokenValue, accessTokenExpirationTime);
//        LISessionManager.getInstance(context).init(accessToken);
//        LISessionManager sessionManager = LISessionManager.getInstance(context);
//        LISession session = sessionManager.getSession();
//
//        if (session.isValid()) {
//            return (true);
//        }
//
//        else {
//            clearSavedApiData(context);
//            return (false);
//        }
        return false;
    }

    @Override
    public void synchronizeWithSocialApi(Context context, Activity activity, ApiSyncCallBack callBack) {
        //boolean hasSync = getAuthenticationStatus(context);
        authenticateWithoutAccessToken(context, activity, callBack);
    }

    @Override
    public void getProfileData(final Context context, Activity activity, final ApiProfileSyncCallBack callBack) {
        /*
        final String host = "api.linkedin.com";
        final String topCardUrl = "https://" + host + "/v1/people/~?format=json";

        LISessionManager sessionManager = LISessionManager.getInstance(context);
        LISession session = sessionManager.getSession();

        if (session.isValid()) {
            APIHelper apiHelper = APIHelper.getInstance(context);
            apiHelper.getRequest(activity, topCardUrl, new ApiListener() {
                @Override
                public void onApiSuccess(ApiResponse s) {

                    try {
                        JSONObject profileData = s.getResponseDataAsJson();
                        String userId = profileData.getString("id");
                        String firstName = profileData.getString("firstName");
                        String lastName = profileData.getString("lastName");
                        callBack.onProfileSynchronized(new SynchronizableElement(userId, firstName + " " + lastName));
                    }
                    catch (JSONException e) {
                        callBack.onProfileSynchronized(null);
                        saveAuthenticationStatus(context, false);
                    }
                }
                @Override
                public void onApiError(LIApiError error) {
                    callBack.onProfileSynchronized(null);
                    saveAuthenticationStatus(context, false);
                }
            });
        } else {
            callBack.onProfileSynchronized(null);
            saveAuthenticationStatus(context, false);
        }
        */
    }

    @Override
    public void clearSavedApiData(Context context) {
        super.clearSavedApiData(context);
    }

    @Override
    public void saveGeneratedAccessToken(Context context, String authToken) {
        super.saveGeneratedAccessToken(context, authToken);
    }

    @Override
    public String getGeneratedAccessToken(Context context) {
        return (super.getGeneratedAccessToken(context));
    }

    public void saveGeneratedAccessTokenExpiration(Context context, Long authTokenExpiration) {
        super.saveGeneratedAccessTokenExpiration(context, authTokenExpiration);
    }

    public Long getGeneratedAccessTokenExpiration(Context context) {
        return (super.getGeneratedAccessTokenExpiration(context));
    }

    @Override
    public void saveAuthenticationStatus(Context context, boolean status) {
        super.saveAuthenticationStatus(context, status);
    }

    @Override
    public boolean getAuthenticationStatus(Context context) {
        return (super.getAuthenticationStatus(context));
    }

//    private static Scope buildLinkedinScope() {
//        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE);
//    }
}
package com.example.nfctransfer.socialAPIs;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;

public class FacebookAPI extends ASocialAPI {

    private static final String API_NAME = "facebook";
    private CallbackManager callbackManager;
    private ProfileTracker mProfileTracker;
    private Profile fbProfile = null;

    private FacebookAPI() {
        super(API_NAME);
    }

    private static FacebookAPI INSTANCE = new FacebookAPI();

    public static FacebookAPI getInstance()
    {	return INSTANCE;
    }

    @Override
    public boolean isApplicationInstalled(Context context) {
        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    public void synchronizeWithSocialApi(Context context, final Activity activity, final ApiSyncCallBack callBack) {
        if (callbackManager == null) {
            return ;
        }

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                if (Profile.getCurrentProfile() == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                            fbProfile = profile2;
                            mProfileTracker.stopTracking();
                            connected = true;
                            callBack.synchronizationResult(true);
                        }
                    };
                    mProfileTracker.startTracking();
                }
                else {
                    fbProfile = Profile.getCurrentProfile();
                    connected = true;
                    callBack.synchronizationResult(true);
                }
            }

            @Override
            public void onCancel() {
                connected = false;
                callBack.synchronizationResult(false);
            }

            @Override
            public void onError(FacebookException exception) {
                connected = false;
                callBack.synchronizationResult(false);
            }
        });
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile"));
    }

    @Override
    public void getProfileData(Context context, Activity activity, ApiProfileSyncCallBack callBack) {
        String userId;
        String fullName;

        try {
            if (fbProfile == null) {
                throw new Exception();
            }
            if ((userId = fbProfile.getId()) == null){
                throw new Exception();
            }
            if ((fullName = fbProfile.getFirstName() + " " + fbProfile.getLastName()) == null) {
                throw new Exception();
            }
        } catch (Exception e) {
            callBack.onProfileSynchronized(null);
            return ;
        }
        callBack.onProfileSynchronized(new SynchronizableElement(userId, fullName));
    }

    public void setFacebookCallbackManager(CallbackManager _callbackManager){
        this.callbackManager = _callbackManager;
    }

    public CallbackManager getFacebookCallbackManager() {
        return this.callbackManager;
    }
}

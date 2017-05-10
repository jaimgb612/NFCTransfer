package com.example.nfctransfer.socialAPIs;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

public class TwitterAPI extends ASocialAPI {
    private static final String API_NAME = "twitter";
    private TwitterAuthClient twitterAuthClient;

    public TwitterAPI() {
        super(API_NAME);
        twitterAuthClient = new TwitterAuthClient();
    }

    @Override
    public boolean isApplicationInstalled(Context context) {
        try {
            context.getPackageManager().getPackageInfo("com.twitter.android", 0);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    public void synchronizeWithSocialApi(final Context context, Activity activity, final ApiSyncCallBack callBack) {
        if (!isApplicationInstalled(context)) {
            callBack.synchronizationResult(false);
            return;
        }
        twitterAuthClient.authorize(activity, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                connected = true;
                callBack.synchronizationResult(true);
            }
            @Override
            public void failure(TwitterException e) {
                connected = false;
                callBack.synchronizationResult(false);
            }
        });
    }

    @Override
    public void getProfileData(Context context, Activity activity, ApiProfileSyncCallBack callBack) {
        TwitterSession session;
        String userId;
        String userName;

        try {
            if ((session = Twitter.getSessionManager().getActiveSession()) == null){
                throw new Exception();
            }
            if ((userId = session.getUserId() + "") == null){
                throw new Exception();
            }
            if ((userName = session.getUserName()) == null){
                throw new Exception();
            }

        } catch (Exception e) {
            callBack.onProfileSynchronized(null);
            return ;
        }
        callBack.onProfileSynchronized(new SynchronizableElement(userId, userName));
    }
}

package com.example.nfctransfer.socialAPIs.SocialApiProfileViewer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.example.nfctransfer.R;
import com.example.nfctransfer.socialAPIs.ASocialAPI;
import com.example.nfctransfer.socialAPIs.LinkedinAPI;
import com.example.nfctransfer.socialAPIs.TwitterAPI;

public class LinkedinProfileViewer extends ProfileViewer {

    public LinkedinProfileViewer(Context _context, Activity _activity, String _memberID) {
        super(_context, _activity, _memberID);
    }

    @Override
    public void openProfileInApp() {
        if (!LinkedinAPI.getInstance().isApplicationInstalled(context)){
            openProfileInBrowser();
        }
        if (!LinkedinAPI.getInstance().isConnected()){
            LinkedinAPI.getInstance().synchronizeWithSocialApi(context, activity, new ASocialAPI.ApiSyncCallBack() {
                @Override
                public void synchronizationResult(boolean status) {
                    if (!status){
                        onError(context.getResources().getString(R.string.linkedin_fail_connect));
                        openProfileInBrowser();
                    }
                    else {
                        loadLinkedinProfilePage(userID);
                    }
                }
            });
        }
        else {
            loadLinkedinProfilePage(userID);
        }
    }

    @Override
    public void openProfileInBrowser() {
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.linkedin.com/profile/view?id=" + userID));
        webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(webIntent);
    }

    private void loadLinkedinProfilePage(String memberID){
//        DeepLinkHelper deepLinkHelper = DeepLinkHelper.getInstance();
//        deepLinkHelper.openOtherProfile(activity, memberID, new DeepLinkListener() {
//            @Override
//            public void onDeepLinkSuccess() {}
//
//            @Override
//            public void onDeepLinkError(LIDeepLinkError error) {
//                onError("Erreur deep link");
//            }
//        });
    }
}

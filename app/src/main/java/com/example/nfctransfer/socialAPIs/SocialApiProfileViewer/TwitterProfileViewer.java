//package com.example.nfctransfer.socialAPIs.SocialApiProfileViewer;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//
//import teamyoki.yoki_client.Classes.Globals.YokiGlobals;
//
//public class TwitterProfileViewer extends ProfileViewer {
//
//    public TwitterProfileViewer(Context _context, Activity _activity, String _userID) {
//        super(_context, _activity, _userID);
//    }
//
//    @Override
//    public void openProfileInApp() {
//        Intent appIntent;
//
//        if (!YokiGlobals.twitterAPI.isApplicationInstalled(context)){
//            openProfileInBrowser();
//        }
//        try {
//            appIntent = new Intent(Intent.ACTION_VIEW);
//            appIntent.setClassName("com.twitter.android", "com.twitter.android.ProfileActivity");
//            appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(appIntent);
//        }
//        catch (Exception e) {
//            openProfileInBrowser();
//        }
//    }
//
//    @Override
//    public void openProfileInBrowser() {
//        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/intent/user?user_id=" + userID));
//        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(browserIntent);
//    }
//}

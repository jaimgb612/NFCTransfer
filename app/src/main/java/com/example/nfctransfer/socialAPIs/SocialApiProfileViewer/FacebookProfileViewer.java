//package com.example.nfctransfer.socialAPIs.SocialApiProfileViewer;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//
//public class FacebookProfileViewer extends ProfileViewer {
//
//    public String facebookUrl;
//
//    public FacebookProfileViewer(Context _context, Activity _activity, String _userID) {
//        super(_context, _activity, _userID);
//        facebookUrl = "https://www.facebook.com/" + userID;
//    }
//
//    @Override
//    public void openProfileInApp() {
//        Intent appIntent;
//        Uri facebookAppUri;
//
//        if (!YokiGlobals.facebookAPI.isApplicationInstalled(context)){
//            openProfileInBrowser();
//        }
//        try {
//            int versionCode = context.getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
//            if (versionCode >= 3002850) {
//                facebookAppUri = Uri.parse("fb://facewebmodal/f?href=" + facebookUrl);
//            } else {
//                facebookAppUri = Uri.parse("fb://page/" + userID);
//            }
//            appIntent = new Intent(Intent.ACTION_VIEW, facebookAppUri);
//            appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(appIntent);
//        } catch (Exception e) {
//            openProfileInBrowser();
//        }
//    }
//
//    @Override
//    public void openProfileInBrowser() {
//        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl));
//        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(browserIntent);
//    }
//}
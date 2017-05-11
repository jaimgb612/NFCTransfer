package com.example.nfctransfer.socialAPIs.SocialApiProfileViewer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

import com.example.nfctransfer.R;

public abstract class ProfileViewer {

    protected Context context;
    protected Activity activity;
    protected String userID;

    public ProfileViewer(Context _context, Activity _activity, String _userID){
        context = _context;
        activity = _activity;
        userID = _userID;
    }

    public void onError(final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

                alertDialogBuilder.setTitle(R.string.oops);
                alertDialogBuilder
                        .setCancelable(false)
                        .setMessage(message)
                        .setPositiveButton(R.string.OK, null);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    public abstract void openProfileInApp();
    public abstract void openProfileInBrowser();
}

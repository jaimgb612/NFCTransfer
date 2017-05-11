package com.example.nfctransfer.socialAPIs.SocialApiProfileViewer;

import android.app.Activity;
import android.content.Context;

import com.example.nfctransfer.data.AProfileDataField;

public class SyncProfileViewer {

    private Context context;
    private Activity activity;

    public SyncProfileViewer(Context _context, Activity _activity) {
        this.context = _context;
        this.activity = _activity;
    }

    public void viewProfile(AProfileDataField field) {
        String userId = field.getSocialId();

        ProfileViewer profileViewer;

        switch (field.getFieldType()){
            case FACEBOOK:
                profileViewer = new FacebookProfileViewer(context, activity, userId);
                break;
            case LINKEDIN:
                profileViewer = new LinkedinProfileViewer(context, activity, userId);
                break;
            case TWITTER:
                profileViewer = new TwitterProfileViewer(context, activity, userId);
                break;
            default:
                profileViewer = null;
        }
        if (profileViewer != null){
            profileViewer.openProfileInApp();
        }
    }
}

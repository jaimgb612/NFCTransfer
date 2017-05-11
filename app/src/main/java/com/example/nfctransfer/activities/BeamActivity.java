package com.example.nfctransfer.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.nfctransfer.R;
import com.example.nfctransfer.ndeftools.Message;
import com.example.nfctransfer.ndeftools.MimeRecord;
import com.example.nfctransfer.ndeftools.util.activity.NfcBeamWriterActivity;
import com.example.nfctransfer.networking.Session;

import java.io.UnsupportedEncodingException;

public class BeamActivity extends NfcBeamWriterActivity {

    private final static String NFCTRANSFER_TAG = "NFCTRANSFER_NDEF_RECORD";
    protected Message message;
    private Context context;
    //private UserData MatchedUser;
    private LinearLayout mainLayout;
    private RelativeLayout errConnectionLayout;
    private ProgressBar progressBar;
    private String targetRegId;


    public void activateProgressBar(final boolean refreshing) {
        if (refreshing) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    public void onRetrieveUserDataFail(final String targetUserId){
        activateProgressBar(false);
        mainLayout.setVisibility(View.GONE);
        errConnectionLayout.setVisibility(View.VISIBLE);
        Button retryConnection = (Button) findViewById(R.id.beam_activity_retry_connection_button);
        retryConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errConnectionLayout.setVisibility(View.GONE);
                retrieveTargetData(targetUserId);
            }
        });
    }
    
    public void onReceivedTagCorrupted(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(R.string.oops);
        alertDialogBuilder
                .setMessage(R.string.err_beam_corrupted_tag)
                .setPositiveButton(R.string.OK, null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private void retrieveTargetData(final String targetUserId){

//        Call<ApiUserData> call;
//
//        activateProgressBar(true);
//        call = apiActions.retrieveTargetUserContent(targetUserId);
//        call.enqueue(new Callback<ApiUserData>() {
//            @Override
//            public void onResponse(Response<ApiUserData> response, Retrofit retrofit) {
//                Integer statusCode = response.code();
//
//                if (statusCode == ApiStatusCode.HTTP_OK) {
//                    ApiUserData apiUserData = response.body();
//                    UserData userData = apiUserData.getUserInfo();
//                    activateProgressBar(false);
//                    goToProfileShowCase(userData);
//                } else if (statusCode == ApiStatusCode.HTTP_FORBIDDEN) {
//                    startActivity(new Intent(context, UserLoginActivity.class));
//                    finish();
//                } else {
//                    onRetrieveUserDataFail(targetUserId);
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                onRetrieveUserDataFail(targetUserId);
//            }
//        });

        Intent intent = new Intent(context, ProfileDisplayer.class);
        intent.putExtra("PROFILE", profile);
        startActivity(intent);
    }

    private void goToProfileShowCase(String userData) {
//        Intent intent = new Intent(this, ProfileShowCase.class);
//        intent.putExtra(YokiGlobals.Keys.INTENT_USERDATA_KEY, userData);
//        intent.putExtra(YokiGlobals.Keys.INTENT_USERDATA_NEW, true);
//        startActivity(intent);
//        finish();
    }

    private void notificateBeamedUser(final String targetRegId){

//        Call<ApiBasicResponse> call;
//
//        call = apiActions.replyToBeamWithGcm(targetRegId);
//        call.enqueue(new Callback<ApiBasicResponse>() {
//            @Override
//            public void onResponse(Response<ApiBasicResponse> response, Retrofit retrofit) {
//                Integer statusCode = response.code();
//
//                if (statusCode == ApiStatusCode.HTTP_FORBIDDEN) {
//                    startActivity(new Intent(context, UserLoginActivity.class));
//                    finish();
//                } else if (statusCode != ApiStatusCode.HTTP_OK) {
//                    onGcmReplyFail(targetRegId);
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                onGcmReplyFail(targetRegId);
//            }
//        });
    }

    private void init(){
        context = getApplicationContext();
        //MatchedUser = new UserData();
        mainLayout = (LinearLayout) findViewById(R.id.beam_activity_mainlayout);
        errConnectionLayout = (RelativeLayout) findViewById(R.id.beam_activity_retry_connection_layout);
        progressBar = (ProgressBar) findViewById(R.id.beam_activity_progress_bar);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beam);
        init();
        setDetecting(true);
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event){
        Message highLevelMsg = new Message();

        MimeRecord identityRecord = new MimeRecord();
        MimeRecord userDataRecord = new MimeRecord();

        identityRecord.setMimeType("text/plain");
        userDataRecord.setMimeType("text/plain");

        try {
            identityRecord.setData(NFCTRANSFER_TAG.getBytes("UTF-8"));
            userDataRecord.setData(Session.userId.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        highLevelMsg.add(0, identityRecord);
        highLevelMsg.add(1, userDataRecord);

        return (highLevelMsg.getNdefMessage());
    }

    @Override
    protected void readNdefMessage(Message message) {
        String identity;
        String targetId;

        identity = new String(message.get(0).getNdefRecord().getPayload());
        if (identity.compareTo(NFCTRANSFER_TAG) != 0) {
            onReceivedTagCorrupted();
            return;
        }
        targetId = new String(message.get(1).getNdefRecord().getPayload());
        notificateBeamedUser(targetRegId);
        retrieveTargetData(targetId);
    }

    @Override
    protected void onNdefPushCompleted() {
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
        vibe.vibrate(500);
        finish();
    }

    @Override
    protected void onNfcPushStateEnabled() {}

    @Override
    protected void onNfcPushStateDisabled() {}

    @Override
    protected void onNfcPushStateChange(boolean enabled) {}

    @Override
    protected void onNfcFeatureNotFound() {}

    @Override
    protected void readEmptyNdefMessage() {}

    @Override
    protected void readNonNdefMessage(){}

    @Override
    protected void onNfcFeatureFound() {
        super.onNfcFeatureFound();
        startPushing();
    }

    @Override
    protected void onNfcStateEnabled() {}

    @Override
    protected void onNfcStateDisabled() {}

    @Override
    protected void onNfcStateChange(boolean enabled) {}

    @Override
    protected void onTagLost() {}

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        retrieveTargetData("");
    }
}

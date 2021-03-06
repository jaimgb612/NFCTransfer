package com.example.nfctransfer.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcEvent;
import android.os.Bundle;
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
import com.example.nfctransfer.networking.ApiResponses.Profile.Profile;
import com.example.nfctransfer.networking.ApiResponses.Profile.PullProfileResponse;
import com.example.nfctransfer.networking.ApiResponses.SimpleResponse;
import com.example.nfctransfer.networking.HttpCodes;
import com.example.nfctransfer.networking.NfcTransferApi;
import com.example.nfctransfer.networking.Session;

import java.io.UnsupportedEncodingException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BeamActivity extends NfcBeamWriterActivity {

    private final static String NFCTRANSFER_TAG = "NFCTRANSFER_NDEF_RECORD";
    protected Message message;
    private Context context;
    private LinearLayout mainLayout;
    private RelativeLayout errConnectionLayout;
    private ProgressBar progressBar;

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


    private void displayTargetProfile(Profile profile) {
        Intent intent = new Intent(context, ProfileDisplayer.class);
        intent.putExtra(ProfileDisplayer.EXTRA_KEY_PROFILE, profile);
        intent.putExtra(ProfileDisplayer.EXTRA_KEY_INSERT_IN_DATABASE, true);
        startActivity(intent);
        finish();
    }

    private void retrieveTargetData(final String targetUserId){

        Call<PullProfileResponse> call;

        call = NfcTransferApi.getInstance().getTargetProfileData(Session.accessToken, targetUserId);
        call.enqueue(new Callback<PullProfileResponse>() {
            @Override
            public void onResponse(Call<PullProfileResponse> call, Response<PullProfileResponse> response) {
                int code = response.code();
                PullProfileResponse result;

                if (code != HttpCodes.OK) {
                    onRetrieveUserDataFail(targetUserId);
                    return;
                }
                result = response.body();
                Profile profile = result.getUser();
                displayTargetProfile(profile);
            }

            @Override
            public void onFailure(Call<PullProfileResponse> call, Throwable t) {
                t.printStackTrace();
                onRetrieveUserDataFail(targetUserId);
            }
        });
    }

    private void notifyBeamedUser(final String targetId){

        Call<SimpleResponse> call;

        call = NfcTransferApi.getInstance().notifyBeamedUser(Session.accessToken, targetId);
        call.enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {}

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {}
        });
    }

    private void init(){
        context = getApplicationContext();
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
        notifyBeamedUser(targetId);
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
}

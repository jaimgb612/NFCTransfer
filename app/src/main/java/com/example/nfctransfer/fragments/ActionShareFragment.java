package com.example.nfctransfer.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nfctransfer.R;
import com.example.nfctransfer.activities.BeamActivity;

import at.markushi.ui.CircleButton;

public class ActionShareFragment extends Fragment {

    private Context mContext;
    private CircleButton mShareButton;

    private void alertForUserSettingsInteraction(Integer titleRes, Integer msgRes, final Intent intent) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder.setTitle(titleRes);
        alertDialogBuilder
                .setMessage(msgRes)
                .setNegativeButton(R.string.user_edit_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (intent != null) {
                            getActivity().startActivity(intent);
                        }
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }

    private void initComponents() {
        mContext = getContext();

        mShareButton = (CircleButton) getActivity().findViewById(R.id.action_share_button);

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (proceedNfcChecks()) {
                    goToBeamActivity();
                }
            }
        });
    }

    private boolean proceedNfcChecks() {

        NfcManager manager = (NfcManager) getActivity().getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter == null) {
            alertForUserSettingsInteraction(R.string.oops,
                    R.string.err_no_nfc_detected, null);
            return false;
        }
        if (!adapter.isEnabled()) {
            alertForUserSettingsInteraction(R.string.oops,
                    R.string.err_no_nfc_enabled,
                    new Intent(Settings.ACTION_NFC_SETTINGS));
            return false;
        }
        if (!adapter.isNdefPushEnabled()) {
            alertForUserSettingsInteraction(R.string.oops,
                    R.string.err_no_android_beam_enabled,
                    new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
            return false;
        }
        return true;
    }

    private void goToBeamActivity() {
        Intent intent = new Intent(mContext, BeamActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_action_share, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initComponents();
    }
}

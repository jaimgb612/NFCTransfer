package com.example.nfctransfer.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.nfctransfer.R;
import com.example.nfctransfer.Utils.IsStringNumeric;
import com.example.nfctransfer.networking.ApiResponses.AuthResponse;
import com.example.nfctransfer.networking.ApiResponses.SimpleResponse;
import com.example.nfctransfer.networking.HttpCodes;
import com.example.nfctransfer.networking.NfcTransferApi;
import com.example.nfctransfer.networking.Session;
import com.example.nfctransfer.sharedPreferences.Preferences;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmAccountActivity extends AppCompatActivity {

    private static final int CONFIRM_CODE_SIZE = 6;

    private Context _context;
    private Button _confirmButton;
    private EditText _codeInput;
    private ProgressBar _actionProgressBar;
    private boolean _lastCodeInputState;
    private String userCellphone;
    private String userPassword;

    private interface AlertMessageShownListener {
        void alertMessageClosed();
    }

    private void showAlertForMessage(int msgRes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmAccountActivity.this);

        builder.setCancelable(false);
        builder.setPositiveButton(R.string.alert_dialog_ok_button, null);
        builder.setMessage(msgRes);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAlertForMessageWithCallback(int msgRes, final AlertMessageShownListener callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmAccountActivity.this);

        builder.setCancelable(false);
        builder.setPositiveButton(R.string.alert_dialog_ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.alertMessageClosed();
            }
        });
        builder.setMessage(msgRes);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void goBackToLogin() {
        Preferences.getInstance().deleteSavedCredentials(this);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finishAffinity();
        startActivity(intent);
    }

    private void onRequestStart() {
        _confirmButton.setVisibility(View.GONE);
        _actionProgressBar.setVisibility(View.VISIBLE);
    }

    private void onRequestFailed() {
        _confirmButton.setVisibility(View.VISIBLE);
        _actionProgressBar.setVisibility(View.INVISIBLE);
    }

    private void onRequestLoginSuccess(AuthResponse data) {
        Intent mainActivityIntent = new Intent(_context, MainActivity.class);

        _actionProgressBar.setVisibility(View.INVISIBLE);

        String accessToken = data.getAccessToken();

        Preferences.getInstance().saveAccessToken(accessToken, getApplicationContext());

        Session.accessToken = accessToken;

        startActivity(mainActivityIntent);
    }

    private void requestConfirmationNumber(String userId) {
        Call<SimpleResponse> call;

        call = NfcTransferApi.getInstance().sendActivation(userId);
        call.enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                int code = response.code();

                if (code != HttpCodes.OK) {
                    onRequestFailed();

                    if (code == HttpCodes.FORBIDDEN) {
                        authenticate(userCellphone, userPassword);
                    }
                    else {
                        showAlertForMessageWithCallback(R.string.user_err_send_activation_error, new AlertMessageShownListener() {
                            @Override
                            public void alertMessageClosed() {
                                goBackToLogin();
                            }
                        });
                    }
                    return;
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                t.printStackTrace();
                showAlertForMessageWithCallback(R.string.user_err_send_activation_error, new AlertMessageShownListener() {
                    @Override
                    public void alertMessageClosed() {
                        goBackToLogin();
                    }
                });
            }
        });
    }

    private void confirmCode(String userId, String token) {
        Call<SimpleResponse> call;

        call = NfcTransferApi.getInstance().verify(userId, token);
        onRequestStart();
        call.enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                int code = response.code();

                if (code != HttpCodes.OK) {
                    onRequestFailed();

                    if (code == HttpCodes.UNAUTHORIZED) {
                        showAlertForMessage(R.string.user_err_confirm_account_invalid_code);
                    }
                    else if (code == HttpCodes.FORBIDDEN) {
                        authenticate(userCellphone, userPassword);
                    }
                    else {
                        showAlertForMessage(R.string.user_err_confirm_account_error);
                    }
                    return;
                }

                authenticate(userCellphone, userPassword);
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                t.printStackTrace();
                onRequestFailed();
                showAlertForMessage(R.string.user_err_confirm_account_error);
            }
        });
    }

    private void authenticate(String cellphone, String password) {
        Call<AuthResponse> call;
        call = NfcTransferApi.getInstance().authenticate(cellphone, password);
        onRequestStart();

        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                int code = response.code();
                AuthResponse result;

                if (code != HttpCodes.OK) {
                    onRequestFailed();
                    showAlertForMessageWithCallback(R.string.login_error, new AlertMessageShownListener() {
                        @Override
                        public void alertMessageClosed() {
                            goBackToLogin();
                        }
                    });
                }
                result = response.body();
                onRequestLoginSuccess(result);
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                String msg = t.getMessage();
                t.printStackTrace();
                onRequestFailed();
                if (msg != null && !msg.equals("Canceled") && !msg.equals("Socket closed")) {
                    Log.e("Login Failure", msg);
                    showAlertForMessageWithCallback(R.string.login_error, new AlertMessageShownListener() {
                        @Override
                        public void alertMessageClosed() {
                            goBackToLogin();
                        }
                    });
                }
            }
        });
    }

    private void initComponents() {
        Intent intent = getIntent();

        userCellphone = intent.getStringExtra(LoginActivity.INTENT_EXTRA_CELLPHONE_KEY);
        userPassword = intent.getStringExtra(LoginActivity.INTENT_EXTRA_PASSWORD_KEY);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        _context = getApplicationContext();
        _confirmButton = (Button) findViewById(R.id.button_confirm);
        _codeInput = (EditText) findViewById(R.id.confirm_code_input);
        _actionProgressBar = (ProgressBar) findViewById(R.id.action_progress_bar);
        _lastCodeInputState = false;

        _codeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                final Drawable backgroundDefault = ContextCompat.getDrawable(_context, R.drawable.login_round_corner_view_inverted);
                final Drawable backgroundOk = ContextCompat.getDrawable(_context, R.drawable.login_round_corner_view);
                final String currentCode = _codeInput.getText().toString();

                if (_codeInput.length() == CONFIRM_CODE_SIZE && IsStringNumeric.isNumeric(currentCode)) {
                    if (!_lastCodeInputState) {
                        _lastCodeInputState = true;
                        _confirmButton.setEnabled(true);
                        _confirmButton.setBackground(backgroundOk);
                    }
                }
                else {
                    if (_lastCodeInputState) {
                        _lastCodeInputState = false;
                        _confirmButton.setEnabled(false);
                        _confirmButton.setBackground(backgroundDefault);
                    }
                }
            }
        });

        _confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = _codeInput.getText().toString();
                confirmCode(Session.userId, token);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_account);

        initComponents();

        requestConfirmationNumber(Session.userId);
    }
}

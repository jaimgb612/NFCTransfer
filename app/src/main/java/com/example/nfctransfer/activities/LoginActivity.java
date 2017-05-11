package com.example.nfctransfer.activities;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.nfctransfer.R;
import com.example.nfctransfer.intlphoneinput.IntlPhoneInput;
import com.example.nfctransfer.networking.ApiResponses.AuthResponse;
import com.example.nfctransfer.networking.ApiResponses.RegisterResponse;
import com.example.nfctransfer.networking.HttpCodes;
import com.example.nfctransfer.networking.NfcTransferApi;
import com.example.nfctransfer.networking.Session;
import com.example.nfctransfer.sharedPreferences.Preferences;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

public class LoginActivity extends Activity {

    public static final String INTENT_EXTRA_CELLPHONE_KEY = "extra_cellphone";
    public static final String INTENT_EXTRA_PASSWORD_KEY = "extra_password";

    private enum LoginActivityMode {
        AUTH,
        REGISTER
    }

    private Context _context;
    private Call _currentCall = null;
    private LoginActivityMode _currentMode;

    private IntlPhoneInput _phoneInput;
    private EditText _passwordInput;
    private EditText _firstName;
    private EditText _lastName;

    private LinearLayout _registerPart;
    private LinearLayout _loginPart;
    private LinearLayout _connectLayout;
    private RelativeLayout _registerActionLayout;
    private RelativeLayout _registerCancelLayout;
    private RelativeLayout _allSecondaryActionsLayout;
    private RelativeLayout _cancelAuthActionLayout;

    private Button _connectButton;
    private Button _registerButton;
    private Button _cancelRegisterButton;
    private Button _cancelAuthActionButton;
    private ProgressBar _actionProgressBar;

    private boolean _lastFormValidState = false;

    private void showAlertForMessage(int msgRes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

        builder.setCancelable(false);
        builder.setPositiveButton(R.string.alert_dialog_ok_button, null);
        builder.setMessage(msgRes);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showRegisterPart(boolean show) {
        final Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
        final Animation animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout);

        if (show) {
            _registerPart.startAnimation(animationFadeIn);
            _registerPart.setVisibility(View.VISIBLE);
        }
        else {
            _registerPart.startAnimation(animationFadeOut);
            _registerPart.setVisibility(View.GONE);
        }
    }

    private void storeLoggedUserData(String userId) {
        Session.userId = userId;
        Preferences.getInstance().saveUserId(userId, _context);
    }

    private void proceedAccountVerification(String cellphone, String password) {
        Intent confirmAccountActivityIntent = new Intent(_context, ConfirmAccountActivity.class);

        _actionProgressBar.setVisibility(View.INVISIBLE);
        _cancelAuthActionLayout.setVisibility(View.GONE);

        confirmAccountActivityIntent.putExtra(INTENT_EXTRA_CELLPHONE_KEY, cellphone);
        confirmAccountActivityIntent.putExtra(INTENT_EXTRA_PASSWORD_KEY, password);

        startActivity(confirmAccountActivityIntent);
    }

    private void onRequestStart() {
        _connectButton.setVisibility(View.GONE);
        _actionProgressBar.setVisibility(View.VISIBLE);
        _cancelAuthActionLayout.setVisibility(View.VISIBLE);
        _allSecondaryActionsLayout.setVisibility(View.GONE);
    }

    private void onRequestLoginSuccess(AuthResponse data) {
        Intent mainActivityIntent = new Intent(_context, MainActivity.class);

        _actionProgressBar.setVisibility(View.INVISIBLE);
        _cancelAuthActionLayout.setVisibility(View.GONE);

        String userId = data.getUserId();
        String accessToken = data.getAccessToken();

        Preferences.getInstance().saveAccessToken(accessToken, _context);
        Preferences.getInstance().saveUserId(userId, _context);

        Session.userId = userId;
        Session.accessToken = accessToken;

        startActivity(mainActivityIntent);
        finish();
    }

    private void onRequestRegisterSuccess(RegisterResponse data, String cellphone, String password) {
        String userId = data.getUserId();
        storeLoggedUserData(userId);
        proceedAccountVerification(cellphone, password);
    }

    private void onRequestFailed() {
        _connectButton.setVisibility(View.VISIBLE);
        _actionProgressBar.setVisibility(View.INVISIBLE);
        _cancelAuthActionLayout.setVisibility(View.GONE);
        _allSecondaryActionsLayout.setVisibility(View.VISIBLE);
    }

    private void attemptSignin(final String cellphone, final String password) {
        Call<AuthResponse> call;
        call = NfcTransferApi.getInstance().authenticate(cellphone, password);
        _currentCall = call;
        onRequestStart();

        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                _currentCall = null;
                int code = response.code();
                AuthResponse result;

                if (code != HttpCodes.OK) {
                    onRequestFailed();

                    if (code == HttpCodes.UNAUTHORIZED) {
                        showAlertForMessage(R.string.login_invalid_credentials);
                    }
                    else if (code == HttpCodes.FORBIDDEN) {

                        Converter<ResponseBody, AuthResponse> converter = NfcTransferApi.getInstance().getRetrofitInstance()
                                .responseBodyConverter(AuthResponse.class, new Annotation[0]);

                        try {
                            result = converter.convert(response.errorBody());
                        } catch(IOException e) {
                            showAlertForMessage(R.string.login_auth_error);
                            return;
                        }
                        storeLoggedUserData(result.getUserId());
                        proceedAccountVerification(cellphone, password);
                    }
                    else if (code == HttpCodes.NOT_FOUND) {
                        showAlertForMessage(R.string.login_account_not_found);
                    }
                    else {
                        showAlertForMessage(R.string.login_auth_error);
                    }
                    return;
                }
                result = response.body();
                onRequestLoginSuccess(result);
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                String msg = t.getMessage();
                _currentCall = null;
                t.printStackTrace();
                onRequestFailed();
                if (msg != null && !msg.equals("Canceled") && !msg.equals("Socket closed")) {
                    Log.e("Login Failure", msg);
                    showAlertForMessage(R.string.login_error);
                }
            }
        });
    }

    private void attemptRegister(final String cellphone, final String password, String firstname, String lastname) {
        Call<RegisterResponse> call;
        call = NfcTransferApi.getInstance().register(cellphone, password, firstname, lastname);
        _currentCall = call;
        onRequestStart();

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                _currentCall = null;
                int code = response.code();
                RegisterResponse result;

                if (code != HttpCodes.CREATED) {
                    onRequestFailed();

                    if (code == HttpCodes.CONFLICT) {
                        showAlertForMessage(R.string.login_register_conflict);
                    }
                    else if (code == HttpCodes.UNPROCESSABLE_ENTITY) {
                        showAlertForMessage(R.string.login_register_invalid_cellphone);
                    }
                    else {
                        showAlertForMessage(R.string.login_register_error);
                    }
                    return;
                }
                result = response.body();
                onRequestRegisterSuccess(result, cellphone, password);
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                String msg = t.getMessage();
                _currentCall = null;
                t.printStackTrace();
                onRequestFailed();
                if (msg != null && !msg.equals("Canceled") && !msg.equals("Socket closed")) {
                    Log.e("Register Failure", msg);
                    showAlertForMessage(R.string.login_register_error);
                }
            }
        });
    }

    /**
     * Initializes all UI components
     * Adds transitions to Layouts that would move
     * Adds text-watchers to inputs
     * Adds click event listeners to fire requests
     */
    private void initComponents() {
        LayoutTransition transition;

        _context = getApplicationContext();
        _currentMode = LoginActivityMode.AUTH;

        _phoneInput = (IntlPhoneInput) findViewById(R.id.phone_input);
        _passwordInput = (EditText) findViewById(R.id.password_input);
        _firstName = (EditText) findViewById(R.id.firstname_input);
        _lastName = (EditText) findViewById(R.id.lastname_input);

        _connectButton = (Button) findViewById(R.id.button_connection);
        _registerButton = (Button) findViewById(R.id.button_register);
        _cancelRegisterButton = (Button) findViewById(R.id.button_cancel_register);
        _cancelAuthActionButton = (Button) findViewById(R.id.button_cancel_auth_action);
        _actionProgressBar = (ProgressBar) findViewById(R.id.action_progress_bar);

        _registerPart = (LinearLayout) findViewById(R.id.register_part);
        _loginPart = (LinearLayout) findViewById(R.id.login_part);
        _connectLayout = (LinearLayout) findViewById(R.id.layout_connect_action);
        _registerActionLayout = (RelativeLayout) findViewById(R.id.layout_register_action);
        _registerCancelLayout = (RelativeLayout) findViewById(R.id.layout_cancel_register_action);
        _allSecondaryActionsLayout = (RelativeLayout) findViewById(R.id.layout_secondary_actions);
        _cancelAuthActionLayout = (RelativeLayout) findViewById(R.id.layout_cancel_auth_action);

        transition = _registerPart.getLayoutTransition();
        transition.enableTransitionType(LayoutTransition.CHANGING);

        transition = _loginPart.getLayoutTransition();
        transition.enableTransitionType(LayoutTransition.CHANGING);

        transition = _connectLayout.getLayoutTransition();
        transition.enableTransitionType(LayoutTransition.CHANGING);

        setInputTextWatchers();

        _connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String cellphone = _phoneInput.getNumber();
                String password = _passwordInput.getText().toString();

                hideKeyboard();

                if (_currentMode == LoginActivityMode.AUTH) {
                    attemptSignin(cellphone, password);
                }
                else if (_currentMode == LoginActivityMode.REGISTER) {
                    String firstname = _firstName.getText().toString();
                    String lastname = _lastName.getText().toString();
                    attemptRegister(cellphone, password, firstname, lastname);
                }
            }
        });

        _registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkIfRegisterFormIsValid()) {
                    _lastFormValidState = false;
                    switchConnectionButtonBackground(false);
                }
                showRegisterPart(true);
                _firstName.requestFocus();
                _currentMode = LoginActivityMode.REGISTER;
                _connectButton.setText(R.string.login_button_register);
                _registerActionLayout.setVisibility(View.GONE);
                _registerCancelLayout.setVisibility(View.VISIBLE);
            }
        });

        _cancelRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkIfConnectionFormIsValid()) {
                    _lastFormValidState = false;
                    switchConnectionButtonBackground(false);
                }
                else {
                    if (!_lastFormValidState) {
                        _lastFormValidState = true;
                        switchConnectionButtonBackground(true);
                    }
                }
                showRegisterPart(false);
                _phoneInput.requestFocus();
                _currentMode = LoginActivityMode.AUTH;
                _connectButton.setText(R.string.login_button_connection);
                _registerActionLayout.setVisibility(View.VISIBLE);
                _registerCancelLayout.setVisibility(View.GONE);
            }
        });

        _cancelAuthActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_currentCall != null && _currentCall.isExecuted()) {
                    _currentCall.cancel();
                }
            }
        });
    }

    /**
     * Adds text-watchers to inputs
     * Prevents user to hit connect/register button
     * when form isn't valid (missing parameters)
     * Checks for all inputs if whole active form is valid,
     * then switches the connect button state to be
     * clickable or not, depending on form state
     */
    private void setInputTextWatchers() {

        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (_currentMode == LoginActivityMode.REGISTER) {
                    if (checkIfRegisterFormIsValid()) {
                        if (!_lastFormValidState) {
                            _lastFormValidState = true;
                            switchConnectionButtonBackground(true);
                        }
                    }
                    else {
                        if (_lastFormValidState) {
                            _lastFormValidState = false;
                            switchConnectionButtonBackground(false);
                        }
                    }
                }
                else {
                    if (checkIfConnectionFormIsValid()) {
                        if (!_lastFormValidState) {
                            _lastFormValidState = true;
                            switchConnectionButtonBackground(true);

                        }
                    }
                    else {
                        if (_lastFormValidState) {
                            _lastFormValidState = false;
                            switchConnectionButtonBackground(false);
                        }
                    }
                }
            }
        };

        _phoneInput.addTextChangedListener(tw);
        _passwordInput.addTextChangedListener(tw);
        _firstName.addTextChangedListener(tw);
        _lastName.addTextChangedListener(tw);
    }

    private boolean checkIfRegisterFormIsValid() {
        return  (_phoneInput.isValid() && _firstName.getText().length() > 0 &&
                _lastName.getText().length() > 0 && _passwordInput.getText().length() > 0);
    }

    private boolean checkIfConnectionFormIsValid() {
        return (_phoneInput.isValid() && _passwordInput.getText().length() > 0);
    }

    private void switchConnectionButtonBackground(boolean enabled) {
        final Drawable backgroundDefault = ContextCompat.getDrawable(_context, R.drawable.login_round_corner_view_inverted);
        final Drawable backgroundOk = ContextCompat.getDrawable(_context, R.drawable.login_round_corner_view);

        if (enabled) {
            _connectButton.setBackground(backgroundOk);
            _connectButton.setEnabled(true);
        }
        else {
            _connectButton.setBackground(backgroundDefault);
            _connectButton.setEnabled(false);
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initComponents();
    }
}


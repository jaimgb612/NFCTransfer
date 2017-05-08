package com.example.nfctransfer.activities;

import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nfctransfer.R;
import com.example.nfctransfer.intlphoneinput.IntlPhoneInput;
import com.example.nfctransfer.networking.ApiResponses.AuthResponse;
import com.example.nfctransfer.networking.ApiResponses.SignupResponse;
import com.example.nfctransfer.networking.HttpCodes;
import com.example.nfctransfer.networking.NfcTransferApi;
import com.example.nfctransfer.sharedPreferences.Preferences;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

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
    private Spinner _ageInput;

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

        Toast.makeText(_context, "AUTH SUCCESS", Toast.LENGTH_LONG).show();

        String userToken = data.getAccessToken();
        String userId = data.getUserId();

//        Globals.userPreferences.saveUserToken(userToken, _context);
//        Globals.userPreferences.saveUserId(userId, _context);
//        Globals.userToken = userToken;
//        Globals.userId = userId;

        startActivity(mainActivityIntent);
        finish();
    }

    private void onRequestRegisterSuccess(SignupResponse data, String cellphone, String password) {
        //Intent confirmAccountActivityIntent = new Intent(_context, ConfirmAccountActivity.class);

        _actionProgressBar.setVisibility(View.INVISIBLE);
        _cancelAuthActionLayout.setVisibility(View.GONE);

        Toast.makeText(_context, "REGISTER SUCCESS", Toast.LENGTH_LONG).show();

//        confirmAccountActivityIntent.putExtra(INTENT_EXTRA_CELLPHONE_KEY, cellphone);
//        confirmAccountActivityIntent.putExtra(INTENT_EXTRA_PASSWORD_KEY, password);

        //startActivity(confirmAccountActivityIntent);
    }

    private void onRequestFailed() {
        _connectButton.setVisibility(View.VISIBLE);
        _actionProgressBar.setVisibility(View.INVISIBLE);
        _cancelAuthActionLayout.setVisibility(View.GONE);
        _allSecondaryActionsLayout.setVisibility(View.VISIBLE);
    }

    private void attemptSignin(String cellphone, String password) {
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
                    else if (code == HttpCodes.FORBIDDEN){
                        showAlertForMessage(R.string.login_auth_error);
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

    private void attemptRegister(final String cellphone, final String password, String firstname, String lastname, String age) {
//        Call<BasicResponse> call;
//        call = Globals.API.register(cellphone, password, firstname, lastname, age);
//        _currentCall = call;
//        onRequestStart();
//        call.enqueue(new Callback<BasicResponse>() {
//            @Override
//            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
//                _currentCall = null;
//                int code = response.code();
//                BasicResponse result;
//
//                if (code != HttpCodes.CREATED) {
//                    onRequestFailed();
//
//                    if (code == HttpCodes.CONFLICT) {
//                        showAlertForMessage(R.string.login_register_conflict);
//                    }
//                    else if (code == HttpCodes.UNPROCESSABLE_ENTITY) {
//                        showAlertForMessage(R.string.login_register_invalid_cellphone);
//                    }
//                    else {
//                        showAlertForMessage(R.string.login_register_error);
//                    }
//                    return;
//                }
//                result = response.body();
//                onRequestRegisterSuccess(result, cellphone, password);
//            }
//
//            @Override
//            public void onFailure(Call<BasicResponse> call, Throwable t) {
//                String msg = t.getMessage();
//                _currentCall = null;
//                t.printStackTrace();
//                onRequestFailed();
//                if (msg != null && !msg.equals("Canceled") && !msg.equals("Socket closed")) {
//                    Log.e("Register Failure", msg);
//                    showAlertForMessage(R.string.login_register_error);
//                }
//            }
//        });
    }


    /**
     * Initializes the age input Spinner by inserting
     * ages between 18 and 99.
     * Creates a default adapter and sets it.
     */
    private void initAgeInput() {
        String yearsSuffix = getResources().getString(R.string.login_spinner_years_suffix);
        ArrayList<String> ages = new ArrayList<>();
        ages.add("Age");
        for (int i = 18; i <= 99; i++) {
            ages.add(Integer.toString(i) + " " + yearsSuffix);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _ageInput.setAdapter(adapter);
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

        _ageInput = (Spinner) findViewById(R.id.age_input);

        transition = _registerPart.getLayoutTransition();
        transition.enableTransitionType(LayoutTransition.CHANGING);

        transition = _loginPart.getLayoutTransition();
        transition.enableTransitionType(LayoutTransition.CHANGING);

        transition = _connectLayout.getLayoutTransition();
        transition.enableTransitionType(LayoutTransition.CHANGING);

        initAgeInput();
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
                    String age = Integer.toString(_ageInput.getSelectedItemPosition());
                    attemptRegister(cellphone, password, firstname, lastname, age);
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
        _ageInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

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
                _lastName.getText().length() > 0 && _passwordInput.getText().length() > 0 &&
                _ageInput.getSelectedItemPosition() != 0);
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


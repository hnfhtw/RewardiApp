/********************************************************************************************
 * Project    : Rewardi
 * Created on : 12/2018 - 01/2019
 * Author     : Harald Netzer
 * Version    : 001
 *
 * File       : LoginActivity.java
 * Purpose    : Send login or registration request to server; get session token from server;
 ********************************************************************************************/

package me.rewardi;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    // UI references.
    private AutoCompleteTextView mUserNameView;
    private AutoCompleteTextView mMailAddressView;
    private EditText mPasswordView;
    private EditText mPassword2View;
    private View mProgressView;
    private View mLoginFormView;
    private Button mLoginRegisterButton;
    private TextInputLayout textInputLayoutMailAddress;
    private TextInputLayout textInputLayoutPassword2;
    private TextView textViewRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {    // this activity is started either at the start of the app, or if the user presses the Logout button in the menu (in this case an Intent with boolean extra isLogout = true is passed to LoginActivity)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        IonSetSelfSignedSSL selfSignedSSL = new IonSetSelfSignedSSL();
        selfSignedSSL.setSelfSignedSSL(this,null);  // ensure that self signed server certificate is trusted (it is stored in assets/rewardi.cer)

        // Set up the login form.
        mUserNameView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewUserName);
        mUserNameView.setText("");
        mPasswordView = (EditText) findViewById(R.id.editTextPassword);
        mPasswordView.setText("");

        // Set up the registration form.
        mMailAddressView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewMailAddress);
        mMailAddressView.setText("");
        mPassword2View = (EditText) findViewById(R.id.editTextPassword2);
        mPassword2View.setText("");

        mLoginRegisterButton = (Button) findViewById(R.id.login_register_button);
        mLoginRegisterButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(mLoginRegisterButton.getText().equals("Sign in")){
                            attemptLogin("","",false);
                        }
                        else{
                            attemptRegister();
                        }

                    }
                });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        SharedPreferences sharedPref = LoginActivity.this.getPreferences(Context.MODE_PRIVATE); // login data of last user is stored in SharedPreferences (to avoid manual login at each app start)
        String userName = "";
        String password = "";

        Intent intent = getIntent();
        boolean isLogout = intent.getBooleanExtra("logout", false);

        if(isLogout){   // delete stored login data if LoginActivity is started after press on Logout button
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("userName", "");   // delete stored value
            editor.putString("password", "");   // delete stored value
            editor.commit();
        }
        else{
            userName = sharedPref.getString("userName", userName);
            password = sharedPref.getString("password", password);
        }

        if(userName.length() > 0 && password.length() > 0){
            attemptLogin(userName, password, true);       // use stored credentials to login
        }

        // hide the input elements used for registration of new user
        textInputLayoutMailAddress = (TextInputLayout) findViewById(R.id.textInputLayoutMailAddress);
        textInputLayoutMailAddress.setVisibility(View.GONE);

        textInputLayoutPassword2 = (TextInputLayout) findViewById(R.id.textInputLayoutPassword2);
        textInputLayoutPassword2.setVisibility(View.GONE);

        textViewRegister = (TextView) findViewById(R.id.textViewRegister);
        textViewRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textViewRegister.getText().equals("Register New Rewardi Account")){
                    textInputLayoutMailAddress.setVisibility(View.VISIBLE);
                    textInputLayoutPassword2.setVisibility(View.VISIBLE);
                    mLoginRegisterButton.setText("Register");
                    textViewRegister.setText("Back to Login");
                }
                else{
                    textInputLayoutMailAddress.setVisibility(View.GONE);
                    textInputLayoutPassword2.setVisibility(View.GONE);
                    mLoginRegisterButton.setText("Sign in");
                    textViewRegister.setText("Register New Rewardi Account");
                }
            }
        });
    }

    private void attemptLogin(String user, String pw, boolean silentLogin) {    // send login request to server, silentLogin = true if the login data loaded from SharedPreferences is used (= auto login)
        if (!mLoginRegisterButton.isEnabled()) {
            return;
        }

        // Reset errors.
        mUserNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String userName = "";
        String password = "";
        if(silentLogin){
            userName = user;
            password = pw;
        }
        else{
            userName = mUserNameView.getText().toString();
            password = mPasswordView.getText().toString();

            SharedPreferences sharedPref = LoginActivity.this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("userName", userName);   // store value
            editor.putString("password", password);   // store value
            editor.commit();
        }

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid user name
        if (TextUtils.isEmpty(userName)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            JsonObject loginObject = new JsonObject();
            loginObject.addProperty("userName", userName);
            loginObject.addProperty("password", password);

            Ion.with(this)  // this sets the parameters for the login request (method, endpoint, body data format, desired result format and callback function that is called when server response is received -> asynchronous) and then sends the request
                    .load("POST", "https://37.60.168.102:443/api/auth/authenticate")
                    .setJsonObjectBody(loginObject)
                    .asString()
                    .withResponse()

                    .setCallback(new FutureCallback<Response<String>>() {
                        @Override
                        public void onCompleted(Exception e, Response<String> result) {
                            Log.d("Login", "onCompleted called");
                            Log.d("Login", "Result = " + result.getHeaders().code() + ", Body = " + result.getResult());
                            Log.e("Login", "Error: ", e);
                            if (e == null && result.getHeaders().code() == 200) {
                                JsonElement element = new JsonParser().parse(result.getResult());
                                Log.d("Login", "Server Response = " + element.toString());
                                JsonObject object = element.getAsJsonObject();

                                String token =  object.get("token").getAsString();
                                Globals appState = ((Globals)getApplicationContext());      // save sessionToken to global object - it will be accessible from all Activities
                                appState.setSessionToken(token);
                                Log.d("Login", "Server Response Session Token = " + token);

                                Intent intent = new Intent(getApplicationContext(), Home.class);
                                startActivity(intent);

                            } else {
                                Snackbar.make(mUserNameView,
                                        "ERROR",
                                        Snackbar.LENGTH_SHORT)
                                        .show();
                            }
                            showProgress(false);
                        }
                    });
        }
    }

    private void attemptRegister() {    // send register new account request -> if password is valid and user not already existing (user name, mail address) the server sends a confirmation email to the specified mail address -> the user needs to confirm the registration in the received mail -> then the new account can be used (login with user name + password)
        // Reset errors.
        mUserNameView.setError(null);
        mMailAddressView.setError(null);
        mPasswordView.setError(null);
        mPassword2View.setError(null);

        // Store values at the time of the login attempt.
        String userName = mUserNameView.getText().toString();
        String mailAddress = mMailAddressView.getText().toString();
        String password = mPasswordView.getText().toString();
        String password2 = mPassword2View.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one. error_password_match
        if(!password.equals(password2)){
            mPasswordView.setError(getString(R.string.error_password_match));
            focusView = mPasswordView;
            cancel = true;
        }
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid user name
        if (TextUtils.isEmpty(userName)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        }

        // Check for a valid mail address
        if (TextUtils.isEmpty(mailAddress) || !mailAddress.contains("@") ) {
            mMailAddressView.setError(getString(R.string.error_invalid_email));
            focusView = mUserNameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            JsonObject registerObject = new JsonObject();
            registerObject.addProperty("userName", userName);
            registerObject.addProperty("emailAddress", mailAddress);
            registerObject.addProperty("password", password);

            Ion.with(this)  // send registration request to server -> analog to login request described above
                    .load("POST", "https://37.60.168.102:443/api/auth/register")
                    .setJsonObjectBody(registerObject)
                    .asString()
                    .withResponse()

                    .setCallback(new FutureCallback<Response<String>>() {
                        @Override
                        public void onCompleted(Exception e, Response<String> result) {
                            Log.d("Register Account", "onCompleted called");
                            Log.d("Register Account", "Result = " + result.getHeaders().code() + ", Body = " + result.getResult());
                            Log.e("Register Account", "Error: ", e);
                            if (e == null && result.getHeaders().code() == 200) {
                                JsonElement element = new JsonParser().parse(result.getResult());
                                Log.d("Register Account", "Element = " + element.toString());
                                JsonObject object = element.getAsJsonObject();
                                Log.d("Register Account", "Object = " + object.toString());
                                Snackbar.make(mUserNameView,
                                        "Please check yor mailbox to verify the mail address!",
                                        Snackbar.LENGTH_LONG)
                                        .show();

                            } else {
                                Snackbar.make(mUserNameView,
                                        "ERROR",
                                        Snackbar.LENGTH_SHORT)
                                        .show();
                            }
                            textInputLayoutMailAddress.setVisibility(View.GONE);
                            textInputLayoutPassword2.setVisibility(View.GONE);
                            mLoginRegisterButton.setText("Sign in");
                            textViewRegister.setText("Register New Rewardi Account");

                            showProgress(false);
                        }
                    });
        }
    }

    private boolean isPasswordValid(String password) {
        boolean valid = true;
        if(password.length() < 6){
            valid = false;
        }
        return valid;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView
                    .animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
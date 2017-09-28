package com.apps.dawahcast;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


public class FBLogin extends Activity {

    CallbackManager callbackManager;
    LoginButton loginButton;
    private TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(FBLogin.this);
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_fblogin);
        info = (TextView) findViewById(R.id.info);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile");


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                info.setText("Login success.");
                Intent intmain = new Intent(FBLogin.this, MainActivity.class);
                startActivity(intmain);
                finish();
            }

            @Override
            public void onCancel() {
                // App code
                info.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                info.setText("Login attempt failed.");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}

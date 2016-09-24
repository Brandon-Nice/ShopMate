package com.shopmate.shopmate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    public static LoginButton loginButton;
    public static CallbackManager callbackManager;
    public AccessTokenTracker accessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Facebook initialization
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);


        if(isLoggedIn()) {
            loginUser(AccessToken.getCurrentAccessToken().getUserId());
        }

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile");
        loginButton.setReadPermissions("user_friends");

        //Creates a callback function to handle the results of the login attempts
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loginUser(loginResult.getAccessToken().getUserId());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }

        });

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                if (currentAccessToken == null){
                    Intent init = new Intent(LoginActivity.this, LoginActivity.class);
                    init.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(init);
                    finish();
                }
            }
        };

    }

    public void loginUser(final String userID)
    {
        /*
        User currentUser = new User();
        currentUser.setId(userID);
        currentStudent.setSchedule(new ArrayList<Classes>());
        ((MyApplication) getApplication()).setStudent(currentStudent);
        */
        Intent init = new Intent(LoginActivity.this, MainActivity.class);
        init.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(init);
        finish();
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    //LoginButton getter
    static LoginButton getLoginButton() {
        return loginButton;
    }
    //CallbackManager getter
    static CallbackManager getCallbackManager() {
        return callbackManager;
    }
}

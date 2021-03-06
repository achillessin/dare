
package com.team.dare;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AppEventsLogger;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.team.dare.model.FacebookInfo;

public class LoginActivity extends Activity {

    private static final String TAG = "MainActivity";

    private Button mLoginButton;
    private Dialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoginButton = (Button) findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginButtonClicked();
            }
        });

        if (isLoggedIn()) {
            mLoginButton.setText(R.string.login);
            // Go to the user info activity
            goToDareActivity();
        }
    }

    private boolean isLoggedIn() {
        // Check if there is a currently logged in user
        // and they are linked to a Facebook account.
        ParseUser currentUser = ParseUser.getCurrentUser();
        return (currentUser != null)
                && ParseFacebookUtils.isLinked(currentUser);
    }

    private void logout() {
        mLoginButton.setText(R.string.login);
        ParseUser.logOut();
        Session session = Session.getActiveSession();
        if (session == null) {
            session = new Session(this);
            Session.setActiveSession(session);
        }
        session.closeAndClearTokenInformation();
        return;
    }

    private void login() {
        LoginActivity.this.mProgressDialog = ProgressDialog.show(
                LoginActivity.this, "", "Logging in...", true);
        List<String> permissions = Arrays.asList("public_profile",
                "user_about_me", "user_relationships", "user_birthday",
                "user_location", "user_friends");
        ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
            @Override
            public void done(final ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d(DareApplication.TAG,
                            "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew() || user.get("facebookInfo") == null) {
                    Log.d(DareApplication.TAG,
                            "User signed up and logged in through Facebook!");
                    Request request = Request.newMeRequest(
                            ParseFacebookUtils.getSession(),
                            new Request.GraphUserCallback() {
                                @Override
                                public void onCompleted(GraphUser guser,
                                        Response gresponse) {
                                    LoginActivity.this.mProgressDialog
                                            .dismiss();
                                    user.put("facebookID", guser.getId());
                                    FacebookInfo fbInfo = new FacebookInfo();
                                    fbInfo.initialize(guser);
                                    user.put("facebookInfo", fbInfo);
                                    user.saveEventually();
                                    goToDareActivity();
                                }
                            });
                    request.executeAsync();
                } else {
                    Log.d(DareApplication.TAG,
                            "User logged in through Facebook!");
                    mLoginButton.setText(R.string.logout);
                    goToDareActivity();
                }
            }
        });
    }

    private void onLoginButtonClicked() {
        if (isLoggedIn()) {
            logout();
        } else {
            login();
        }

    }

    private void goToDareActivity() {
        Intent intent = new Intent(this, DareActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Call the 'activateApp' method to log an app event for use in
        // analytics and advertising reporting. Do so in
        // the onResume methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Call the 'deactivateApp' method to log an app event for use in
        // analytics and advertising
        // reporting. Do so in the onPause methods of the primary Activities
        // that an app may be launched into.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

}

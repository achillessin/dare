
package com.dare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public class LoginActivity extends Activity {

    private static final String TAG = "MainActivity";

    private boolean isResumed = false;
    private UiLifecycleHelper uiHelper;

    private final Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        isResumed = true;

        // Call the 'activateApp' method to log an app event for use in
        // analytics and advertising reporting. Do so in
        // the onResume methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
        isResumed = false;

        // Call the 'deactivateApp' method to log an app event for use in
        // analytics and advertising
        // reporting. Do so in the onPause methods of the primary Activities
        // that an app may be launched into.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);

    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (isResumed) {
            // check for the OPENED state instead of session.isOpened() since
            // for the
            // OPENED_TOKEN_UPDATED state, the selection fragment should already
            // be showing.
            if (state.equals(SessionState.OPENED)) {
                startActivity(new Intent(this, MainActivity.class));
            }
        }
    }
}

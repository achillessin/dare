package com.dare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class CreateActivity extends Activity {

    private static final String PARAM_MESSAGE_KEY = "message";

    private static final String PARAM_MESSAGE = "You've been challenged!";

    private static final String PARAM_TITLE_KEY = "title";

    private static final String PARAM_CONTENT_KEY = "content";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        setupButtons();

    }

    private void sendRequestDialog() {
        Bundle params = getChallengeParams();
        if (params == null) {
            Toast.makeText(this, "Please fill in challenge title and details",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        WebDialog requestsDialog = (new WebDialog.RequestsDialogBuilder(this,
                Session.getActiveSession(), params)).setOnCompleteListener(
                new OnCompleteListener() {
                    @Override
                    public void onComplete(Bundle values,
                            FacebookException error) {
                        if (error != null) {
                            if (error instanceof FacebookOperationCanceledException) {
                                Toast.makeText(getApplicationContext(),
                                        "Request cancelled", Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Network Error", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } else {
                            final String requestId = values
                                    .getString("request");
                            if (requestId != null) {
                                Toast.makeText(getApplicationContext(),
                                        "Request sent", Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Request cancelled", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    }
                }).build();
        requestsDialog.show();
    }

    private Bundle getChallengeParams() {
        String challengeTitle = ((EditText) findViewById(R.id.challenge_title))
                .getText().toString();
        String challengeContent = ((EditText) findViewById(R.id.challenge_content))
                .getText().toString();
        if (challengeTitle.isEmpty() || challengeContent.isEmpty()) {
            return null;
        }
        Bundle params = new Bundle();
        params.putString(PARAM_MESSAGE_KEY, PARAM_MESSAGE);
        params.putString(PARAM_TITLE_KEY, challengeTitle);
        params.putString(PARAM_CONTENT_KEY, challengeContent);
        return params;
    }

    private void setupButtons() {
        findViewById(R.id.send_request_button).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        sendRequestDialog();
                    }
                });
        findViewById(R.id.btn_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finishCreate();
            }
        });
    }

    private void finishCreate() {
        startActivity(new Intent(CreateActivity.this, DareActivity.class));
    }
}

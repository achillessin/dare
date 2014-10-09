package com.dare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class CreateActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        setupButtons();

    }

    private void sendRequestDialog() {
        Bundle params = new Bundle();
        params.putString(
                "message",
                "This is a request that should prompt you to get the app and provide a send param");
        params.putString("send", "a challenge!");
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
        findViewById(R.id.btn_save).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChallenge();
            }
        });
    }

    private void saveChallenge() {
        finishCreate();
    }

    private void finishCreate() {
        startActivity(new Intent(CreateActivity.this, DareActivity.class));
    }
}

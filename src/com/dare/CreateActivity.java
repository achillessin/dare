
package com.dare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class CreateActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        setupButtons();
    }

    private void setupButtons() {
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


package com.dare;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

public class DareActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupNavButtons();
        switchContentFragment(new TimelineFragment());
    }

    private void setupNavButtons() {
        findViewById(R.id.btn_timeline).setOnClickListener(
                getNavOnClickListener(new TimelineFragment()));
        findViewById(R.id.btn_sent).setOnClickListener(
                getNavOnClickListener(new SentFragment()));
        findViewById(R.id.btn_received).setOnClickListener(
                getNavOnClickListener(new ReceivedFragment()));
        findViewById(R.id.btn_settings).setOnClickListener(
                getNavOnClickListener(new SettingsFragment()));

    }

    private OnClickListener getNavOnClickListener(final Fragment fragment) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchContentFragment(fragment);
            }
        };
    }

    private void switchContentFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_container, fragment);
        transaction.commit();
    }

}

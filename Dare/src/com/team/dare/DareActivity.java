package com.team.dare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class DareActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupNavButtons();
        switchToFragment(new TimelineAllFragment());
    }

    private void setupNavButtons() {
        ImageView createButton = (ImageView) findViewById(R.id.btn_create);
        ImageView homeButton = (ImageView) findViewById(R.id.btn_home);
        ImageView profileButton = (ImageView) findViewById(R.id.btn_profile);
        createButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DareActivity.this,
                        CreateActivity.class));
            }
        });

        homeButton
                .setOnClickListener(getNavOnClickListener(new TimelineAllFragment()));
        profileButton
                .setOnClickListener(getNavOnClickListener(new ProfileFragment()));
    }

    private OnClickListener getNavOnClickListener(final Fragment fragment) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToFragment(fragment);
            }
        };
    }

    private void switchToFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_container, fragment);
        transaction.commit();
    }
}

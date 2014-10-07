
package com.dare;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.dare.utils.FacebookUtils;
import com.facebook.Request.GraphUserListCallback;
import com.facebook.Response;
import com.facebook.model.GraphUser;

public class CreateActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        setupButtons();

        loadFriends();
    }

    private void loadFriends() {
        final TextView friendsView = (TextView) findViewById(R.id.friends);
        friendsView.setText("Loading Friends...");
        FacebookUtils.getFriendList(new GraphUserListCallback() {
            @Override
            public void onCompleted(List<GraphUser> users, Response response) {
                StringBuilder friendsBuilder = new StringBuilder();
                for (GraphUser user : users) {
                    friendsBuilder.append(user.getName() + "\n");
                }
                friendsView.setText(friendsBuilder.toString());
            }
        });
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

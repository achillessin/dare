package com.team.dare;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.team.dare.model.Challenge;

public class CreateActivity extends Activity {

    private static final String TAG = "CreateActivity";

    private ParseUser mCurrentUser;
    private ParseUser mSelectedFriendUser;
    private ProfilePictureView mFriendProfilePicture;
    private Dialog mFriendListDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        setupButtons();

        mFriendProfilePicture = (ProfilePictureView) findViewById(R.id.imageUserTo);
    }

    @Override
    public void onResume() {
        super.onResume();
        // load the parse user
        mCurrentUser = ParseUser.getCurrentUser();
        // set up user profile picture
        ProfilePictureView userProfilePic = (ProfilePictureView) findViewById(R.id.imageUserFrom);
        userProfilePic.setProfileId(mCurrentUser.get("facebookID").toString());
    }

    // function to add a friend to the dare
    public void onAddFriend(View v) {
        // get friends
        Request req = Request.newMyFriendsRequest(
                ParseFacebookUtils.getSession(),
                new Request.GraphUserListCallback() {

                    @Override
                    public void onCompleted(List<GraphUser> users,
                            Response response) {
                        // we have the friends
                        createFriendListDialog(users);
                    }
                });
        req.executeAsync();
    }

    private void createFriendListDialog(final List<GraphUser> friends) {
        if (friends == null) {
            Toast.makeText(this, "No FB friends found. Invite some.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        // create a dialog
        if (mFriendListDialog != null) {
            mFriendListDialog.dismiss();
            mFriendListDialog = null;
        }
        mFriendListDialog = new Dialog(this);

        View view = getLayoutInflater().inflate(R.layout.layout_friend_list,
                null);

        ListView lv = (ListView) view.findViewById(R.id.listviewFriendList);
        // create a custom friend list adapter
        CustomFriendListAdapter listAdapter = new CustomFriendListAdapter(
                CreateActivity.this, friends);

        lv.setAdapter(listAdapter);

        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                GraphUser selectedUser = (GraphUser) parent
                        .getItemAtPosition(position);
                mFriendProfilePicture.setProfileId(selectedUser.getId());
                // save the selected user to mFriendUser
                ParseQuery<ParseUser> friendQuery = ParseQuery.getUserQuery();
                friendQuery.whereEqualTo("facebookID", selectedUser.getId());
                try {
                    mSelectedFriendUser = friendQuery.find().get(0);
                } catch (ParseException e) {
                    Log.e(TAG,
                            "unexpected error. Friend exists in FB Friend list but not on parse.");
                    finishCreate();
                }
                // after selecting the friend, dismiss the dialog.
                mFriendListDialog.dismiss();
                mFriendListDialog = null;
            }

        });

        mFriendListDialog.setContentView(view);

        mFriendListDialog.show();
    }

    private boolean validateDareForm() {
        // TODO: add validation
        return true;
    }

    private void saveDare() {
        if (validateDareForm()) {
            Challenge newChallenge = new Challenge();
            // from User
            newChallenge.setUserFrom(mCurrentUser);
            // to user
            newChallenge.setUserTo(mSelectedFriendUser);
            // title
            EditText editTextTitle = (EditText) findViewById(R.id.textviewTitle);
            String title = editTextTitle.getText().toString();
            newChallenge.setChallengeTitle(title);
            // challenge text
            EditText editTextChallengeText = (EditText) findViewById(R.id.textviewChallengeText);
            String challengeText = editTextChallengeText.getText().toString();
            newChallenge.setChallengeText(challengeText);
            // set completed to false
            newChallenge.setCompleted(false);
            // create acl
            ParseACL acl = new ParseACL();
            acl.setPublicReadAccess(true);
            acl.setWriteAccess(mCurrentUser, true);
            acl.setWriteAccess(mSelectedFriendUser, true);
            newChallenge.setACL(acl);
            // save in background
            newChallenge.saveInBackground();
            // saved, so go back to timeline activity
            finishCreate();
        } else {
            Toast.makeText(this, "Complete all the Dare details..",
                    Toast.LENGTH_LONG).show();
        }
    }

    /*
     * private void sendRequestDialog() { WebDialog requestsDialog = (new
     * WebDialog.RequestsDialogBuilder(this, Session.getActiveSession(),
     * params)).setOnCompleteListener( new OnCompleteListener() {
     * 
     * @Override public void onComplete(Bundle values, FacebookException error)
     * { if (error != null) { if (error instanceof
     * FacebookOperationCanceledException) {
     * Toast.makeText(getApplicationContext(), "Request cancelled",
     * Toast.LENGTH_SHORT) .show(); } else {
     * Toast.makeText(getApplicationContext(), "Network Error",
     * Toast.LENGTH_SHORT) .show(); } } else { final String requestId = values
     * .getString("request"); if (requestId != null) {
     * Toast.makeText(getApplicationContext(), "Request sent",
     * Toast.LENGTH_SHORT) .show(); } else {
     * Toast.makeText(getApplicationContext(), "Request cancelled",
     * Toast.LENGTH_SHORT) .show(); } } } }).build(); requestsDialog.show(); }
     */

    private void setupButtons() {
        findViewById(R.id.send_request_button).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        saveDare();
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

package com.team.dare;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.team.dare.model.Challenge;
import com.team.dare.model.Comment;
import com.team.dare.model.Like;

public class ChallengeExpandedActivity extends Activity {

    private static final String TAG = "ChallengeExpandedActivity";
    public static final String KEY_CHALLENGE_ID = "key.challenge.id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_expanded);

        Intent intent = getIntent();
        String challengeID = intent.getStringExtra(KEY_CHALLENGE_ID);
        if (challengeID == null) {
            Log.e(TAG, "No challenge id provided in the intent.");
            finish();
        }
        Challenge.getChallenge(challengeID, new FindCallback<Challenge>() {

            @Override
            public void done(List<Challenge> objects, ParseException e) {
                if (objects == null || objects.size() == 0) {
                    Log.e(TAG, "No challeng found for given ID.");
                    finish();
                }
                // objectID is unique, so there should be only one object.
                Challenge c = objects.get(0);
                loadChallengeCardContent(c);
                loadChallengeCardFooter(c);
                loadChallengeCardResponse(c);

            }

        });
    }

    // load the challenge card content
    private void loadChallengeCardContent(Challenge c) {
        // get userfrom imageview
        ProfilePictureView userFromImage = (ProfilePictureView) findViewById(R.id.imageUserFrom);
        String userFromFBID = c.getUserFrom().getString("facebookID");
        userFromImage.setProfileId(userFromFBID);
        // get userTo imageView
        ProfilePictureView userToImage = (ProfilePictureView) findViewById(R.id.imageUserTo);
        String userToFBID = c.getUserTo().getString("facebookID");
        userToImage.setProfileId(userToFBID);
        // get challenge text view
        TextView challengeTextView = (TextView) findViewById(R.id.textviewChallengeText);
        challengeTextView.setText(c.getChallengeText().toString());
        // get title
        TextView challengeTitleView = (TextView) findViewById(R.id.textviewTitle);
        challengeTitleView.setText(c.getChallengeTitle().toString());
    }

    // load the challenge card footer - comments, likes,
    private void loadChallengeCardFooter(Challenge c) {
        showLikes(c);
        showComments(c);
        setupLikeButton(c);
        setupCommentButton(c);
    }

    // load the challenge card response - whether accepted,declined, media if
    // uploaded, or option to respond
    private void loadChallengeCardResponse(final Challenge c) {
        // if userTo == currentuser
        if (c.getUserTo().getObjectId() == ParseUser.getCurrentUser()
                .getObjectId()) {
            // if challenge not accepted yet
            if (c.getResponseStatus() == Challenge.RESPONSE_STATUS.UNKNOWN) {
                // show accept/decline buttons
                enableLayout(R.id.linearlayoutAcceptDecline);
                findViewById(R.id.buttonAccept).setOnClickListener(
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                c.setResponseStatus(Challenge.RESPONSE_STATUS.ACCEPTED);
                                c.saveEventually();
                                loadChallengeCardResponse(c);
                            }
                        });
                findViewById(R.id.buttonDecline).setOnClickListener(
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                c.setResponseStatus(Challenge.RESPONSE_STATUS.DECLINED);
                                c.saveEventually();
                                loadChallengeCardResponse(c);
                            }
                        });
            } else if (c.getResponseStatus() == Challenge.RESPONSE_STATUS.ACCEPTED) {
                // show accepted sign, and content if any
                enableLayout(R.id.linearlayoutResponse);
                ((TextView) findViewById(R.id.textviewResponseMessage))
                        .setText("Accepted");
            } else if (c.getResponseStatus() == Challenge.RESPONSE_STATUS.DECLINED) {
                // show declined message
                enableLayout(R.id.linearlayoutResponse);
                ((TextView) findViewById(R.id.textviewResponseMessage))
                        .setText("Declined");
            }
        } else {
            // current user cannot respond
            if (c.getResponseStatus() == Challenge.RESPONSE_STATUS.UNKNOWN) {
                // show awaiting reponse
                enableLayout(R.id.linearlayoutResponse);
                ((TextView) findViewById(R.id.textviewResponseMessage))
                        .setText("Awaiting Response");
            } else if (c.getResponseStatus() == Challenge.RESPONSE_STATUS.ACCEPTED) {
                // show accepted response
                enableLayout(R.id.linearlayoutResponse);
                ((TextView) findViewById(R.id.textviewResponseMessage))
                        .setText("Accepted");
            } else if (c.getResponseStatus() == Challenge.RESPONSE_STATUS.DECLINED) {
                // show declined message
                enableLayout(R.id.linearlayoutResponse);
                ((TextView) findViewById(R.id.textviewResponseMessage))
                        .setText("Declined");
            }
        }
    }

    // helper function to enable one layout in layout_response and disable all
    // others
    private void enableLayout(int layoutID) {
        // disable all layouts
        findViewById(R.id.linearlayoutAcceptDecline).setVisibility(View.GONE);
        findViewById(R.id.linearlayoutResponse).setVisibility(View.GONE);
        findViewById(layoutID).setVisibility(View.VISIBLE);
    }

    private void showLikes(final Challenge challenge) {
        // get all likes for this challenge
        Like.getChallengeLikes(challenge, new FindCallback<Like>() {
            @Override
            public void done(List<Like> likes, ParseException e) {
                Button likeButton = (Button) findViewById(R.id.button_like);
                TextView likesView = (TextView) findViewById(R.id.num_likes);
                // show number of likes
                showNumFeedback(likesView, likes.size(), "like");
                // set like button action
                likeButton.setText(R.string.like);
                for (Like like : likes) {
                    if (like.getUser().hasSameId(ParseUser.getCurrentUser())) {
                        likeButton.setText(R.string.unlike);
                        break;
                    }
                }
            }
        });
    }

    private void showComments(final Challenge challenge) {
        // get all comments for this challenge
        Comment.getNumChallengeComments(challenge, new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                TextView numCommentsView = (TextView) findViewById(R.id.num_comments);
                showNumFeedback(numCommentsView, count, "comment");
            }
        });
    }

    private void setupLikeButton(final Challenge challenge) {
        final Button likeButton = (Button) findViewById(R.id.button_like);
        final TextView likesView = (TextView) findViewById(R.id.num_likes);
        likeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View button) {
                // state could possibly get messy, but likes are non-destructive
                // anyway, so just perform action displayed on button to be user
                // friendly.
                if (likeButton.getText().equals(
                        likeButton.getContext().getString(R.string.like))) {
                    Like.likeChallenge(challenge);
                    likeButton.setText(R.string.unlike);
                    stepNumFeedback(likesView, "like", true);
                } else {
                    Like.unlikeChallenge(challenge);
                    likeButton.setText(R.string.like);
                    stepNumFeedback(likesView, "like", false);
                }
            }
        });
    }

    private void setupCommentButton(final Challenge challenge) {
        final Button commentButton = (Button) findViewById(R.id.button_comment);
        commentButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View button) {
                promptComment(challenge);
            }
        });
    }

    private void promptComment(final Challenge challenge) {
        final TextView commentView = (TextView) findViewById(R.id.num_comments);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Add a Comment");
        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String commentText = input.getText().toString();
                if (commentText.isEmpty()) {
                    return;
                }
                Comment.addComment(challenge, commentText);
                stepNumFeedback(commentView, "comment", true);
            }
        });
        alert.setNegativeButton("Cancel", null);
        alert.show();
    }

    private void showNumFeedback(TextView text, int num, String feedback) {
        if (num > 0) {
            text.setText(Integer.toString(num) + " " + feedback
                    + (num > 1 ? "s" : ""));
        } else {
            text.setText("");
        }
    }

    private void stepNumFeedback(TextView text, String feedback, boolean up) {
        String currString = text.getText().toString();
        int curr = 0;
        int startFeedback = currString.indexOf(feedback);
        if (startFeedback > 0) {
            curr = Integer.parseInt(currString.substring(0, startFeedback - 1));
        }
        int newVal = curr + (up ? 1 : -1);
        newVal = Math.max(newVal, 0);
        showNumFeedback(text, newVal, feedback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.challenge_expanded, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

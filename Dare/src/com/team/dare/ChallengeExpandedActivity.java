package com.team.dare;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.team.dare.model.Challenge;
import com.team.dare.model.Comment;
import com.team.dare.model.FileLoadSaveListener;
import com.team.dare.model.Like;
import com.team.dare.model.ResponseMedia;

public class ChallengeExpandedActivity extends Activity {

    private static final String TAG = "ChallengeExpandedActivity";
    public static final String KEY_CHALLENGE_ID = "key.challenge.id";
    ViewHolder viewHolder;

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
                viewHolder = new ViewHolder();
                viewHolder.userFromImage = (ProfilePictureView) findViewById(R.id.imageUserFrom);
                viewHolder.userToImage = (ProfilePictureView) findViewById(R.id.imageUserTo);
                viewHolder.challengeTitleView = (TextView) findViewById(R.id.textviewTitle);
                viewHolder.challengeTextView = (TextView) findViewById(R.id.textviewChallengeText);
                viewHolder.numLikes = (TextView) findViewById(R.id.num_likes);
                viewHolder.numComments = (TextView) findViewById(R.id.num_comments);
                viewHolder.likeButton = (Button) findViewById(R.id.button_like);
                viewHolder.commentButton = (Button) findViewById(R.id.button_comment);
                viewHolder.shareButton = (Button) findViewById(R.id.button_share);
                Challenge c = objects.get(0);
                loadChallengeCardContent(viewHolder, c);
                loadChallengeCardFooter(viewHolder, c);
                loadChallengeCardResponse(c);
                loadChallengeComments(c);
            }

        });
    }

    // load the challenge card content
    private void loadChallengeCardContent(ViewHolder viewHolder, Challenge c) {
        // get userfrom imageview
        String userFromFBID = c.getUserFrom().getString("facebookID");
        viewHolder.userFromImage.setProfileId(userFromFBID);
        // get userTo imageView
        String userToFBID = c.getUserTo().getString("facebookID");
        viewHolder.userToImage.setProfileId(userToFBID);
        // get challenge text view
        viewHolder.challengeTextView.setText(c.getChallengeText().toString());
        // get title
        viewHolder.challengeTitleView.setText(c.getChallengeTitle().toString());
    }

    // load the challenge card footer - comments, likes,
    private void loadChallengeCardFooter(ViewHolder viewHolder, Challenge c) {
        showNumFeedback(viewHolder.numLikes, c.getNumLikes(), "like");
        showNumFeedback(viewHolder.numComments, c.getNumComments(), "comment");
        setupLikeButton(viewHolder, c);
        setupCommentButton(viewHolder, c);
    }

    // load the challenge card response - whether accepted,declined, media if
    // uploaded, or option to respond
    private void loadChallengeCardResponse(final Challenge c) {
        // if userTo == currentuser
        if (c.getUserTo().getObjectId()
                .equals(ParseUser.getCurrentUser().getObjectId())) {
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
                // load media if any
                loadChallengeResponseMedia(c);
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

    // function to load the response media
    private void loadChallengeResponseMedia(final Challenge c) {
        final FrameLayout containerLayout = (FrameLayout) findViewById(R.id.framelayoutResponseContent);
        // add the challenge response text if any
        TextView tv = new TextView(ChallengeExpandedActivity.this);
        String challengeResponseText = c.getResponseText();
        tv.setText(challengeResponseText);
        // add to framelayout
        containerLayout.addView(tv);

        ParseQuery<ResponseMedia> query = new ParseQuery<ResponseMedia>(
                ResponseMedia.class);
        query.whereEqualTo("Challenge", c);
        query.findInBackground(new FindCallback<ResponseMedia>() {

            @Override
            public void done(List<ResponseMedia> objects, ParseException e) {
                for (int i = 0; i < objects.size(); i++) {
                    // get the thumbnails if any
                    ParseFile thumbnail = objects.get(i).getFileThumbnail();
                    ImageView iv = new ImageView(ChallengeExpandedActivity.this);
                    // display
                    displayImage(thumbnail, iv);
                    // add to framelayout
                    containerLayout.addView(iv);
                }
            }
        });
    }

    private void loadChallengeComments(Challenge challenge) {
        final ListView commentsView = (ListView) findViewById(R.id.comments_list);
        Comment.getChallengeComments(challenge, new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, ParseException e) {
                CommentAdapter adapter = new CommentAdapter(
                        ChallengeExpandedActivity.this, 0, comments);
                commentsView.setAdapter(adapter);
            }
        });

    }

    private void displayImage(final ParseFile f, final ImageView v) {
        ResponseMedia.getFileFromServerHelper(f, new FileLoadSaveListener() {

            @Override
            public void onLoadDone(byte[] data) {
                Bitmap bmp = BitmapFactory
                        .decodeByteArray(data, 0, data.length);
                if (bmp != null) {
                    Log.e(TAG, "Thumbnail downloaded and set to imageview");
                    v.setImageBitmap(bmp);
                }
            }

            @Override
            public void onProgress(int percentageDone) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(ParseException e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onSaveDone() {
                // DO NOTHING

            }

        });
    }

    // helper function to enable one layout in layout_response and disable all
    // others
    private void enableLayout(int layoutID) {
        // disable all layouts
        findViewById(R.id.linearlayoutAcceptDecline).setVisibility(View.GONE);
        findViewById(R.id.linearlayoutResponse).setVisibility(View.GONE);
        findViewById(layoutID).setVisibility(View.VISIBLE);
    }

    private void setupLikeButton(ViewHolder viewHolder,
            final Challenge challenge) {
        final Button likeButton = viewHolder.likeButton;
        final TextView likesView = viewHolder.numLikes;
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

    private void setupCommentButton(final ViewHolder viewHolder,
            final Challenge challenge) {
        final Button commentButton = viewHolder.commentButton;
        commentButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View button) {
                promptComment(viewHolder, challenge);
            }
        });
    }

    private void promptComment(ViewHolder viewHolder, final Challenge challenge) {
        final TextView commentView = viewHolder.numComments;
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

    static class ViewHolder {
        ProfilePictureView userFromImage;
        ProfilePictureView userToImage;
        TextView challengeTitleView;
        TextView challengeTextView;
        TextView numLikes;
        TextView numComments;
        Button likeButton;
        Button commentButton;
        Button shareButton;

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

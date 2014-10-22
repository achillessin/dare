package com.team.dare;

import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.parse.CountCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.team.dare.model.Challenge;
import com.team.dare.model.Comment;
import com.team.dare.model.Like;

public class CustomTimelineAdapter extends ParseQueryAdapter<Challenge> {

    private ParseQuery<Challenge> mQuery;

    private final Map<Challenge, View> challengeLayouts = new HashMap<Challenge, View>();
    private final Map<View, Challenge> viewToChallenge = new HashMap<View, Challenge>();

    public CustomTimelineAdapter(Context context, final ParseQuery<Challenge> q) {
        super(context, new ParseQueryAdapter.QueryFactory<Challenge>() {
            @Override
            public ParseQuery<Challenge> create() {
                return q;
            }
        });
        mQuery = q;
    }

    public ParseQuery<Challenge> getQuery() {
        return mQuery;
    }

    @Override
    public View getItemView(Challenge challenge, View layout, ViewGroup parent) {
        // if (layout == null) {
        layout = View.inflate(getContext(),
                R.layout.layout_challenge_card_content_footer, null);
        // }
        Challenge prevChallenge = viewToChallenge.get(layout);
        if (prevChallenge != null) {
            challengeLayouts.remove(prevChallenge);
        }
        viewToChallenge.put(layout, challenge);
        challengeLayouts.put(challenge, layout);
        try {
            challenge.fetchIfNeeded();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // get userfrom imageview
        ProfilePictureView userFromImage = (ProfilePictureView) layout
                .findViewById(R.id.imageUserFrom);
        String userFromFBID = challenge.getUserFrom().getString("facebookID");
        userFromImage.setProfileId(userFromFBID);
        // get userTo imageView
        ProfilePictureView userToImage = (ProfilePictureView) layout
                .findViewById(R.id.imageUserTo);
        String userToFBID = challenge.getUserTo().getString("facebookID");
        userToImage.setProfileId(userToFBID);
        // get challenge text view
        TextView challengeTextView = (TextView) layout
                .findViewById(R.id.textviewChallengeText);
        challengeTextView.setText(challenge.getChallengeText().toString());
        // get title
        TextView challengeTitleView = (TextView) layout
                .findViewById(R.id.textviewTitle);
        challengeTitleView.setText(challenge.getChallengeTitle().toString());

        // showLikes(challenge);
        // showComments(challenge);
        // setupLikeButton(challenge);
        // setupCommentButton(challenge);
        return layout;
    }

    private void showLikes(final Challenge challenge) {
        // get num likes for this challenge
        Like.getNumChallengeLikes(challenge, new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                View layout = challengeLayouts.get(challenge);
                if (layout == null) {
                    return;
                }
                TextView likesView = (TextView) layout
                        .findViewById(R.id.num_likes);
                showNumFeedback(likesView, count, "like");
            }
        });
        // figure out if user liked this challenge
        Like.getUserLikedChallenge(challenge, new GetCallback<Like>() {
            @Override
            public void done(Like like, ParseException e) {
                View layout = challengeLayouts.get(challenge);
                if (layout == null) {
                    return;
                }
                Button likeButton = (Button) layout
                        .findViewById(R.id.button_like);
                if (like == null) {
                    likeButton.setText(R.string.like);
                } else {
                    likeButton.setText(R.string.unlike);
                }
            }
        });
    }

    private void showComments(final Challenge challenge) {
        // get all comments for this challenge
        Comment.getNumChallengeComments(challenge, new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                View layout = challengeLayouts.get(challenge);
                if (layout == null) {
                    return;
                }
                TextView numCommentsView = (TextView) layout
                        .findViewById(R.id.num_comments);
                showNumFeedback(numCommentsView, count, "comment");
            }
        });
    }

    private void setupLikeButton(final Challenge challenge) {
        View layout = challengeLayouts.get(challenge);
        final Button likeButton = (Button) layout
                .findViewById(R.id.button_like);
        final TextView likesView = (TextView) layout
                .findViewById(R.id.num_likes);
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
        View layout = challengeLayouts.get(challenge);
        final Button commentButton = (Button) layout
                .findViewById(R.id.button_comment);
        commentButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View button) {
                promptComment(challenge);
            }
        });
    }

    private void promptComment(final Challenge challenge) {
        View layout = challengeLayouts.get(challenge);
        final TextView commentView = (TextView) layout
                .findViewById(R.id.num_comments);
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Add a Comment");
        // Set an EditText view to get user input
        final EditText input = new EditText(getContext());
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
}

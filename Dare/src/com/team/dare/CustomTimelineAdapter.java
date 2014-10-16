package com.team.dare;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.team.dare.model.Challenge;
import com.team.dare.model.Like;

public class CustomTimelineAdapter extends ParseQueryAdapter<Challenge> {

    public CustomTimelineAdapter(Context context, final ParseQuery<Challenge> q) {
        super(context, new ParseQueryAdapter.QueryFactory<Challenge>() {

            @Override
            public ParseQuery<Challenge> create() {
                ParseQuery<Challenge> query = q;
                return query;
            }
        });
    }

    @Override
    public View getItemView(Challenge challenge, View layout, ViewGroup parent) {
        if (layout == null) {
            layout = View.inflate(getContext(),
                    R.layout.layout_challenge_card_content_footer, null);
        }
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

        setLikesAndComments(challenge, layout);
        setupLikeAndCommentButtons(challenge, layout);
        return layout;
    }

    private void setLikesAndComments(Challenge challenge, final View layout) {
        // TODO only does likes right now
        final Button likeButton = (Button) layout
                .findViewById(R.id.button_like);
        // get all likes for this challenge
        Like.getChallengeLikes(challenge, new FindCallback<Like>() {
            @Override
            public void done(List<Like> likes, ParseException e) {
                // show number of likes
                showNumLikes((TextView) layout.findViewById(R.id.num_likes),
                        likes.size());
                // set like button action
                likeButton.setText(R.string.like);
                for (Like like : likes) {
                    if (like.getUser().equals(ParseUser.getCurrentUser())) {
                        likeButton.setText(R.string.unlike);
                        break;
                    }
                }
            }
        });
    }

    private void setupLikeAndCommentButtons(final Challenge challenge,
            final View layout) {
        // TODO only does likes right now
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
                    stepLike(likesView, true);
                } else {
                    Like.unlikeChallenge(challenge);
                    likeButton.setText(R.string.like);
                    stepLike(likesView, false);
                }
            }
        });
    }

    private void showNumLikes(TextView numLikesText, int numLikes) {
        if (numLikes > 0) {
            numLikesText.setText(Integer.toString(numLikes) + " like"
                    + (numLikes > 1 ? "s" : ""));
        } else {
            numLikesText.setText("");
        }
    }

    private void stepLike(TextView numLikesText, boolean up) {
        String currString = numLikesText.getText().toString();
        int currLikes = 0;
        int startLike = currString.indexOf("like");
        if (startLike > 0) {
            currLikes = Integer
                    .parseInt(currString.substring(0, startLike - 1));
        }
        int newLikes = currLikes + (up ? 1 : -1);
        newLikes = Math.max(newLikes, 0);
        showNumLikes(numLikesText, newLikes);
    }
}


package com.team.dare;

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
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.team.dare.model.Challenge;
import com.team.dare.model.Comment;
import com.team.dare.model.Like;

public class CustomTimelineAdapter extends ParseQueryAdapter<Challenge> {

    private ParseQuery<Challenge> mQuery;

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
    public View getItemView(Challenge challenge, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(getContext(),
                    R.layout.layout_challenge_card_content_footer, null);
            viewHolder = new ViewHolder();
            viewHolder.userFromImage = (ProfilePictureView) convertView
                    .findViewById(R.id.imageUserFrom);
            viewHolder.userToImage = (ProfilePictureView) convertView
                    .findViewById(R.id.imageUserTo);
            viewHolder.challengeTitleView = (TextView) convertView.findViewById(R.id.textviewTitle);
            viewHolder.challengeTextView = (TextView) convertView
                    .findViewById(R.id.textviewChallengeText);
            viewHolder.numLikes = (TextView) convertView.findViewById(R.id.num_likes);
            viewHolder.numComments = (TextView) convertView.findViewById(R.id.num_comments);
            viewHolder.likeButton = (Button) convertView.findViewById(R.id.button_like);
            viewHolder.commentButton = (Button) convertView.findViewById(R.id.button_comment);
            viewHolder.shareButton = (Button) convertView.findViewById(R.id.button_share);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        try {
            challenge.fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // get userfrom imageview
        String userFromFBID = challenge.getUserFrom().getString("facebookID");
        String userToFBID = challenge.getUserTo().getString("facebookID");
        viewHolder.userFromImage.setProfileId(userFromFBID);
        viewHolder.userToImage.setProfileId(userToFBID);
        viewHolder.challengeTitleView.setText(challenge.getChallengeTitle().toString());
        viewHolder.challengeTextView.setText(challenge.getChallengeText().toString());
        showNumFeedback(viewHolder.numLikes, challenge.getNumLikes(), "like");
        showNumFeedback(viewHolder.numComments, challenge.getNumComments(), "comment");

        setupLikeButton(viewHolder, challenge);
        setupCommentButton(viewHolder, challenge);
        return convertView;
    }

    private void setupLikeButton(ViewHolder viewHolder, final Challenge challenge) {
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

    private void setupCommentButton(final ViewHolder viewHolder, final Challenge challenge) {
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
}

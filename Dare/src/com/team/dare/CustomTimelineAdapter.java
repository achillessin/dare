package com.team.dare;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.team.dare.model.Challenge;

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
    public View getItemView(Challenge object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(),
                    R.layout.layout_challenge_card_content_footer, null);
        }
        try {
            object.fetchIfNeeded();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // get userfrom imageview
        ProfilePictureView userFromImage = (ProfilePictureView) v
                .findViewById(R.id.imageUserFrom);
        String userFromFBID = object.getUserFrom().getString("facebookID");
        userFromImage.setProfileId(userFromFBID);
        // get userTo imageView
        ProfilePictureView userToImage = (ProfilePictureView) v
                .findViewById(R.id.imageUserTo);
        String userToFBID = object.getUserTo().getString("facebookID");
        userToImage.setProfileId(userToFBID);
        // get challenge text view
        TextView challengeTextView = (TextView) v
                .findViewById(R.id.textviewChallengeText);
        challengeTextView.setText(object.getChallengeText().toString());
        // get title
        TextView challengeTitleView = (TextView) v
                .findViewById(R.id.textviewTitle);
        challengeTitleView.setText(object.getChallengeTitle().toString());

        return v;
    }
}

package com.team.dare;

import android.graphics.Color;

import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.team.dare.model.Challenge;

public class TimelineReceivedFragment extends TimelineFragment {

    @Override
    protected void initializeState() {
        mReceivedText.setTextColor(Color.RED);

    }

    @Override
    protected CustomTimelineAdapter getAdapter() {
        ParseQuery<Challenge> receivedQuery = new ParseQuery<Challenge>(
                Challenge.class);
        receivedQuery.include("UserTo");
        receivedQuery.include("UserFrom");
        receivedQuery.whereEqualTo("UserTo", ParseUser.getCurrentUser());
        return new CustomTimelineAdapter(getActivity(), receivedQuery);
    }

}

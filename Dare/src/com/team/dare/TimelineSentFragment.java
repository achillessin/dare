package com.team.dare;

import android.graphics.Color;

import com.parse.ParseQuery;
import com.team.dare.model.Challenge;

public class TimelineSentFragment extends TimelineFragment {

    @Override
    protected void initializeState() {
        mSentText.setTextColor(Color.RED);

    }

    @Override
    protected CustomTimelineAdapter getAdapter() {
        ParseQuery<Challenge> sentQuery = new ParseQuery<Challenge>(
                Challenge.class);
        sentQuery.include("UserTo");
        sentQuery.include("UserFrom");
        sentQuery.whereEqualTo("UserFrom", mCurrentuser);
        return new CustomTimelineAdapter(getActivity(), sentQuery);
    }

}

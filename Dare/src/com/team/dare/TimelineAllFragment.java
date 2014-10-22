package com.team.dare;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.team.dare.model.Challenge;
import com.team.dare.utils.FacebookUtils;

public class TimelineAllFragment extends TimelineFragment {

    private List<ParseUser> mUsers;

    @Override
    protected void initializeState() {
        mAllText.setTextColor(Color.RED);
        loadFriends();
    }

    @Override
    protected CustomTimelineAdapter getAdapter() {
        if (mUsers == null) {
            // friends not loaded yet
            return null;
        }
        ParseQuery<Challenge> queryUserFrom = new ParseQuery<Challenge>(
                "Challenge");
        queryUserFrom.whereContainedIn("UserFrom", mUsers);
        // query Userto
        ParseQuery<Challenge> queryUserTo = new ParseQuery<Challenge>(
                "Challenge");
        queryUserTo.whereContainedIn("UserTo", mUsers);
        // main query
        List<ParseQuery<Challenge>> queries = new ArrayList<ParseQuery<Challenge>>();
        queries.add(queryUserTo);
        queries.add(queryUserFrom);
        ParseQuery<Challenge> allQuery = ParseQuery.or(queries);
        allQuery.include("UserFrom");
        allQuery.include("UserTo");
        return new CustomTimelineAdapter(getActivity(), allQuery);
    }

    @Override
    protected boolean waitForAdapter() {
        return true;
    }

    private void loadFriends() {
        FacebookUtils.getFriends(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                friends.add(ParseUser.getCurrentUser());
                mUsers = new ArrayList<ParseUser>(friends);
                setupAdapter();
            }
        });
    }

}

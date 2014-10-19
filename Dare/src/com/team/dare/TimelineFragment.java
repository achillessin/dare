package com.team.dare;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter.OnQueryLoadListener;
import com.parse.ParseUser;
import com.team.dare.model.Challenge;
import com.team.dare.utils.FacebookUtils;

public class TimelineFragment extends Fragment {

    private static final String TAG = "SentFragment";

    private static enum Filter {
        ALL, RECEIVED, SENT
    }

    private Filter mFilterState = Filter.ALL;

    private ParseUser mCurrentuser;

    private List<ParseUser> mAllUsers;

    private FrameLayout mListFrame;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_timeline, null);
        mListFrame = (FrameLayout) view.findViewById(R.id.list_frame);

        mCurrentuser = ParseUser.getCurrentUser();

        setupFilterButtons(view);
        loadFriends();
        updateChallenges();
        return view;
    }

    private void setupFilterButtons(View view) {
        final TextView all = (TextView) view
                .findViewById(R.id.timeline_showall);
        final TextView received = (TextView) view
                .findViewById(R.id.timeline_showreceived);
        final TextView sent = (TextView) view
                .findViewById(R.id.timeline_showsent);
        OnClickListener filterClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                all.setTextColor(Color.BLACK);
                received.setTextColor(Color.BLACK);
                sent.setTextColor(Color.BLACK);
                Filter before = mFilterState;
                if (view.equals(all)) {
                    mFilterState = Filter.ALL;
                    all.setTextColor(Color.RED);
                } else if (view.equals(received)) {
                    mFilterState = Filter.RECEIVED;
                    received.setTextColor(Color.RED);
                } else if (view.equals(sent)) {
                    mFilterState = Filter.SENT;
                    sent.setTextColor(Color.RED);
                }
                if (mFilterState != before) {
                    updateChallenges();
                }
            }
        };
        all.setOnClickListener(filterClickListener);
        received.setOnClickListener(filterClickListener);
        sent.setOnClickListener(filterClickListener);
        all.setTextColor(Color.RED);
    }

    private void loadFriends() {
        Log.e("androidruntime", "loadfriends");
        FacebookUtils.getFriends(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                friends.add(ParseUser.getCurrentUser());
                mAllUsers = new ArrayList<ParseUser>(friends);
                if (mFilterState == Filter.ALL) {
                    updateChallenges();
                }
            }
        });
    }

    private CustomTimelineAdapter getAllAdapter() {
        ParseQuery<Challenge> queryUserTo = new ParseQuery<Challenge>(
                "Challenge");
        queryUserTo.whereEqualTo("UserTo", mAllUsers.get(2));
        queryUserTo.include("UserFrom");
        queryUserTo.include("UserTo");
        return new CustomTimelineAdapter(getActivity(), queryUserTo);

        // ParseQuery<Challenge> queryUserFrom = new ParseQuery<Challenge>(
        // "Challenge");
        // queryUserFrom.whereContainedIn("UserFrom",
        // mAllUsers);
        // // query Userto
        // ParseQuery<Challenge> queryUserTo = new ParseQuery<Challenge>(
        // "Challenge");
        // queryUserTo.whereContainedIn("UserTo",
        // mAllUsers);
        // // main query
        // List<ParseQuery<Challenge>> queries = new
        // ArrayList<ParseQuery<Challenge>>();
        // queries.add(queryUserTo);
        // queries.add(queryUserFrom);
        // ParseQuery<Challenge> allQuery = ParseQuery.or(queries);
        // allQuery.include("UserFrom");
        // allQuery.include("UserTo");
        // return new CustomTimelineAdapter(getActivity(), allQuery);

    }

    private CustomTimelineAdapter getReceivedAdapter() {
        ParseQuery<Challenge> receivedQuery = new ParseQuery<Challenge>(
                Challenge.class);
        receivedQuery.include("UserTo");
        receivedQuery.include("UserFrom");
        receivedQuery.whereEqualTo("UserTo", mCurrentuser);
        return new CustomTimelineAdapter(getActivity(), receivedQuery);
    }

    private CustomTimelineAdapter getSentAdapter() {
        ParseQuery<Challenge> sentQuery = new ParseQuery<Challenge>(
                Challenge.class);
        sentQuery.include("UserTo");
        sentQuery.include("UserFrom");
        sentQuery.whereEqualTo("UserFrom", mCurrentuser);
        return new CustomTimelineAdapter(getActivity(), sentQuery);
    }

    private void updateChallenges() {
        Log.e("androidruntime", "updatechallenges");
        CustomTimelineAdapter adapter = null;
        switch (mFilterState) {
        case ALL:
            // mAllQuery doesn't exist until friends query is finished
            if (mAllUsers != null) {
                adapter = getAllAdapter();
            }
            break;
        case RECEIVED:
            adapter = getReceivedAdapter();
            break;
        case SENT:
            adapter = getSentAdapter();
            break;
        }
        if (adapter != null) {
            adapter.addOnQueryLoadListener(new OnQueryLoadListener<Challenge>() {

                @Override
                public void onLoaded(List<Challenge> objects, Exception e) {
                    Log.e("androidruntime", "loaded");

                }

                @Override
                public void onLoading() {
                    Log.e("androidruntime", "loading");

                }

            });
            mListFrame.removeAllViews();
            ListView listView = new ListView(getActivity());
            mListFrame.addView(listView);
            listView.setAdapter(adapter);

        }
    }
}

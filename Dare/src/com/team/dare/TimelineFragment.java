package com.team.dare;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.team.dare.model.Challenge;

public class TimelineFragment extends Fragment {

    private static final String TAG = "SentFragment";

    private static enum Filter {
        ALL, RECEIVED, SENT
    }

    private Filter filterState = Filter.ALL;

    private ParseUser mCurrentuser;
    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_timeline, null);
        mListView = (ListView) view.findViewById(R.id.listViewTimeline);

        mCurrentuser = ParseUser.getCurrentUser();

        setupFilterButtons(view);

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
                if (view.equals(all)) {
                    filterState = Filter.ALL;
                    all.setTextColor(Color.RED);
                } else if (view.equals(received)) {
                    filterState = Filter.RECEIVED;
                    received.setTextColor(Color.RED);
                } else if (view.equals(sent)) {
                    filterState = Filter.SENT;
                    sent.setTextColor(Color.RED);
                }
                updateChallenges();
            }
        };
        all.setOnClickListener(filterClickListener);
        received.setOnClickListener(filterClickListener);
        sent.setOnClickListener(filterClickListener);
        all.setTextColor(Color.RED);
    }

    private void updateChallenges() {
        switch (filterState) {
        ParseQuery<Challenge> query;
        case ALL:
            populateFriendsTimeline();
            break;
        case RECEIVED:
            query = new ParseQuery<Challenge>(
                    Challenge.class);
            query.include("UserTo");
            query.include("UserFrom");
            query.whereEqualTo("UserTo", mCurrentuser);
            populateWithQuery(query);
            break;
        case SENT:
            query = new ParseQuery<Challenge>(
                    Challenge.class);
            query.include("UserTo");
            query.include("UserFrom");
            query.whereEqualTo("UserFrom", mCurrentuser);
            populateWithQuery(query);
            break;
        }
    }

    private void populateWithQuery(ParseQuery<Challenge> query) {
        CustomTimelineAdapter adapter = new CustomTimelineAdapter(
                getActivity(), query);
        mListView.setAdapter(adapter);
    }

    private void populateFriendsTimeline() {
        Request req = Request.newMyFriendsRequest(
                ParseFacebookUtils.getSession(),
                new Request.GraphUserListCallback() {
                    // TODO: in query add currentUser also.
                    @Override
                    public void onCompleted(List<GraphUser> users,
                            Response response) {
                        if (users != null) {
                            List<String> friendsList = new ArrayList<String>();
                            for (GraphUser user : users) {
                                friendsList.add(user.getId());
                            }

                            // Construct a ParseUser query that will find
                            // friends whose
                            // facebook IDs are contained in the current user's
                            // friend list.
                            ParseQuery<ParseUser> friendQuery = ParseQuery
                                    .getUserQuery();
                            friendQuery.whereContainedIn("facebookID",
                                    friendsList);

                            // findObjects will return a list of ParseUsers that
                            // are friends with the current user
                            try {
                                List<ParseUser> friendUsers = friendQuery
                                        .find();
                                // another query for finding challenge objects
                                // where
                                // users are friends
                                populateWithQuery(new ParseQuery<Challenge>(
                                        "Challenge").whereContainedIn(
                                        "UserFrom", friendUsers));
                            } catch (ParseException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                });
        req.executeAsync();
    }
}

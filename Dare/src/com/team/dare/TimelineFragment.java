
package com.team.dare;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
    private ParseUser mCurrentuser;
    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_timeline, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        mListView = (ListView) getView().findViewById(R.id.listViewTimeline);
        // load the parse user
        mCurrentuser = ParseUser.getCurrentUser();
        // get all challenges created by user
        if (mCurrentuser != null) {
            createChallengeTimeline();
        } else {
            // TODO: add this fragment to backstack
            // TODO: go back to login activity
        }
    }

    public void createChallengeTimeline() {
        Request req = Request.newMyFriendsRequest(
                ParseFacebookUtils.getSession(),
                new Request.GraphUserListCallback() {

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
                                ParseQuery<Challenge> query = new ParseQuery<Challenge>(
                                        "Challenge");
                                query.whereContainedIn("UserFrom", friendUsers);
                                CustomTimelineAdapter adapter = new CustomTimelineAdapter(
                                        getActivity(), query);
                                // get list view and add adapter to it.
                                mListView.setAdapter(adapter);
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

package com.team.dare;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.team.dare.model.Challenge;

public class SentFragment extends Fragment {

    private static final String TAG = "SentFragment";
    private ParseUser mCurrentuser;
    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_sent, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        mListView = (ListView) getView().findViewById(R.id.listViewSent);
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
        // create a custom adapter for the list
        ParseQuery<Challenge> query = new ParseQuery<Challenge>(Challenge.class);
        query.include("UserTo");
        query.include("UserFrom");
        query.whereEqualTo("UserFrom", mCurrentuser);
        CustomTimelineAdapter adapter = new CustomTimelineAdapter(
                getActivity(), query);
        // get list view and add adapter to it.
        mListView.setAdapter(adapter);
    }
}

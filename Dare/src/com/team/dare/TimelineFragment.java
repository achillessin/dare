
package com.team.dare;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseQueryAdapter.OnQueryLoadListener;
import com.parse.ParseUser;
import com.team.dare.model.Challenge;

public abstract class TimelineFragment extends Fragment {

    protected ParseUser mCurrentuser;

    protected ListView mListView;

    protected TextView mAllText;
    protected TextView mReceivedText;
    protected TextView mSentText;

    private ProgressDialog mProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_timeline, null);
        mListView = (ListView) view.findViewById(R.id.challenge_list);
        mAllText = (TextView) view.findViewById(R.id.timeline_showall);
        mReceivedText = (TextView) view
                .findViewById(R.id.timeline_showreceived);
        mSentText = (TextView) view.findViewById(R.id.timeline_showsent);
        mCurrentuser = ParseUser.getCurrentUser();
        mProgress = new ProgressDialog(getActivity());
        mProgress.setTitle("Loading");
        setupButtons();
        setupList();
        initializeState();
        if (!waitForAdapter()) {
            setupAdapter();
        }
        return view;
    }

    protected abstract void initializeState();

    protected abstract CustomTimelineAdapter getAdapter();

    protected boolean waitForAdapter() {
        return false;
    }

    protected void setupAdapter() {
        CustomTimelineAdapter adapter = getAdapter();
        adapter.addOnQueryLoadListener(new OnQueryLoadListener<Challenge>() {
            @Override
            public void onLoaded(List<Challenge> objects, Exception e) {
                mProgress.dismiss();
            }

            @Override
            public void onLoading() {
                mProgress.show();
            }
        });
        mListView.setAdapter(adapter);
    }

    private void setupButtons() {
        mAllText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToFragment(new TimelineAllFragment());
            }
        });
        mReceivedText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToFragment(new TimelineReceivedFragment());
            }
        });
        mSentText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToFragment(new TimelineSentFragment());
            }
        });
    }

    private void switchToFragment(Fragment fragment) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_container, fragment);
        transaction.commit();
    }

    private void setupList() {
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                Challenge c = (Challenge) parent.getItemAtPosition(position);
                // start ChallengeExpandedActivity
                Intent intent = new Intent(getActivity(),
                        ChallengeExpandedActivity.class);
                intent.putExtra(ChallengeExpandedActivity.KEY_CHALLENGE_ID,
                        c.getObjectId());
                getActivity().startActivity(intent);
            }
        });
    }
}

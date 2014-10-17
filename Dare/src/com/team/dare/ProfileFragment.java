package com.team.dare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.team.dare.model.Challenge;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_settings, null);
        // setup recevied challenge score
        setUpRecvChallengesScore(view);
        // setup sent challenge score
        setUpSentChallengesScore(view);
        // setup profile picture
        setupProfile(view);
        // setup buttons
        setupButtons(view);
        return view;
    }

    private void setupButtons(View rootView) {
        Button logout = (Button) rootView.findViewById(R.id.logoutButton);
        logout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onLogout();
            }
        });
    }

    private void setupProfile(View rootView) {
        final ProfilePictureView imageview = (ProfilePictureView) rootView
                .findViewById(R.id.imageviewProfilePicture);
        imageview.setProfileId(ParseUser.getCurrentUser().getString(
                "facebookID"));
        final TextView profileName = (TextView) rootView
                .findViewById(R.id.textviewName);
        Request req = Request.newMeRequest(ParseFacebookUtils.getSession(),
                new Request.GraphUserCallback() {

                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        if (user != null) {
                            profileName.setText(user.getName().toString());
                        }
                    }
                });
        req.executeAsync();
    }

    private void setUpRecvChallengesScore(View rootView) {
        // get UI element
        final TextView challengesRecv = (TextView) rootView
                .findViewById(R.id.textviewChallengesReceived);
        final TextView challengesCompleted = (TextView) rootView
                .findViewById(R.id.textviewChallengesReceivedCompleted);
        // get count
        ParseQuery<Challenge> query = new ParseQuery<Challenge>(Challenge.class);
        query.whereEqualTo("UserTo", ParseUser.getCurrentUser());
        query.countInBackground(new CountCallback() {

            @Override
            public void done(int arg0, ParseException arg1) {
                if (arg1 == null) {
                    challengesRecv.setText(Integer.toString(arg0));
                } else {
                    Log.e(TAG, "Failed to count challenges:"
                            + arg1.getMessage().toString());
                }
            }
        });
        // get count
        ParseQuery<Challenge> query2 = new ParseQuery<Challenge>(
                Challenge.class);
        query2.whereEqualTo("UserTo", ParseUser.getCurrentUser());
        query2.whereEqualTo("Completed", true);
        query2.countInBackground(new CountCallback() {

            @Override
            public void done(int arg0, ParseException arg1) {
                if (arg1 == null) {
                    challengesCompleted.setText(Integer.toString(arg0));
                } else {
                    Log.e(TAG, "Failed to count challenges:"
                            + arg1.getMessage().toString());
                }
            }
        });
    }

    private void setUpSentChallengesScore(View rootView) {
        // get UI element
        final TextView challengesSent = (TextView) rootView
                .findViewById(R.id.textviewChallengesSent);
        final TextView challengesCompleted = (TextView) rootView
                .findViewById(R.id.textviewChallengesSentCompleted);
        // get count
        ParseQuery<Challenge> query = new ParseQuery<Challenge>(Challenge.class);
        query.whereEqualTo("UserFrom", ParseUser.getCurrentUser());
        query.countInBackground(new CountCallback() {

            @Override
            public void done(int arg0, ParseException arg1) {
                if (arg1 == null) {
                    challengesSent.setText(Integer.toString(arg0));
                } else {
                    Log.e(TAG, "Failed to count challenges:"
                            + arg1.getMessage().toString());
                }
            }
        });
        // get count
        ParseQuery<Challenge> query2 = new ParseQuery<Challenge>(
                Challenge.class);
        query2.whereEqualTo("UserFrom", ParseUser.getCurrentUser());
        query2.whereEqualTo("Completed", true);
        query2.countInBackground(new CountCallback() {

            @Override
            public void done(int arg0, ParseException arg1) {
                if (arg1 == null) {
                    challengesCompleted.setText(Integer.toString(arg0));
                } else {
                    Log.e(TAG, "Failed to count challenges:"
                            + arg1.getMessage().toString());
                }
            }
        });
    }

    public void onLogout() {
        ParseUser.logOut();
        Session session = Session.getActiveSession();
        if (session == null) {
            session = new Session(getActivity());
            Session.setActiveSession(session);
        }
        session.closeAndClearTokenInformation();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}

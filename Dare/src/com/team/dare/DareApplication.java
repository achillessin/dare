
package com.team.dare;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.team.dare.model.Challenge;
import com.team.dare.model.Comment;
import com.team.dare.model.Like;

public class DareApplication extends Application {
    static final String TAG = "DareApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this, getString(R.string.ParseApplicationID),
                getString(R.string.ParseClientID));

        // /register all subclasses
        ParseObject.registerSubclass(Challenge.class);
        ParseObject.registerSubclass(Like.class);
        ParseObject.registerSubclass(Comment.class);
        // TODO: add app permissions here
        ParseFacebookUtils.initialize(getString(R.string.app_id));

    }
}

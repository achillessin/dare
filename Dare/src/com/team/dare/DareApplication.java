
package com.team.dare;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;

public class DareApplication extends Application {
    static final String TAG = "DareApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this, getString(R.string.ParseApplicationID),
                getString(R.string.ParseClientID));

        // TODO: add app permissions here
        ParseFacebookUtils.initialize(getString(R.string.app_id));

    }
}

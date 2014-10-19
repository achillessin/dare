
package com.team.dare.utils;

import java.util.ArrayList;
import java.util.List;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.FindCallback;
import com.parse.ParseFacebookUtils;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class FacebookUtils {

    public static void getFriends(final FindCallback<ParseUser> callback) {
        com.facebook.Session session = ParseFacebookUtils.getSession();
        if (session != null && session.isOpened()) {
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
                                ParseQuery<ParseUser> friendQuery = ParseUser.getQuery();
                                friendQuery.whereContainedIn("facebookID",
                                        friendsList);
                                friendQuery.findInBackground(callback);
                            }
                        }
                    });
            req.executeAsync();
        }
    }
}


package com.dare.utils;

import com.facebook.Request;
import com.parse.ParseFacebookUtils;

public class FacebookUtils {

    public static void getFriendList(Request.GraphUserListCallback callback) {
        com.facebook.Session session = ParseFacebookUtils.getSession();
        if (session != null && session.isOpened()) {
            Request req = Request.newMyFriendsRequest(
                    ParseFacebookUtils.getSession(), callback);
            req.executeAsync();
        }
    }
}

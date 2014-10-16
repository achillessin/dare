package com.team.dare.model;

import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Like")
public class Like extends ParseObject {
    public Like() {
    }

    public Like setUser(ParseUser user) {
        put("User", user);
        return this;
    }

    public Like setChallenge(Challenge challenge) {
        put("Challenge", challenge);
        return this;
    }

    public ParseUser getUser() {
        return getParseUser("User");
    }

    public static void getChallengeLikes(Challenge challenge,
            FindCallback<Like> callback) {
        new ParseQuery<Like>(Like.class).whereEqualTo("Challenge", challenge)
                .findInBackground(callback);
    }

    public static void likeChallenge(final Challenge challenge) {
        // first check for existing like since double likes are not allowed
        new ParseQuery<Like>(Like.class).whereEqualTo("Challenge", challenge)
                .whereEqualTo("User", ParseUser.getCurrentUser())
                .findInBackground(new FindCallback<Like>() {
                    @Override
                    public void done(List<Like> likes, ParseException e) {
                        if (likes.isEmpty()) {
                            new Like().setChallenge(challenge)
                                    .setUser(ParseUser.getCurrentUser())
                                    .saveEventually();
                        }
                    }
                });
    }

    public static void unlikeChallenge(final Challenge challenge) {
        new ParseQuery<Like>(Like.class).whereEqualTo("Challenge", challenge)
                .whereEqualTo("User", ParseUser.getCurrentUser())
                .findInBackground(new FindCallback<Like>() {
                    @Override
                    public void done(List<Like> likes, ParseException e) {
                        for (Like l : likes) {
                            l.deleteEventually();
                        }
                    }
                });
    }
}

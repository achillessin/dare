
package com.team.dare.model;

import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Comment")
public class Comment extends ParseObject {
    public Comment() {
    }

    public Comment setUser(ParseUser user) {
        put("User", user);
        return this;
    }

    public Comment setChallenge(Challenge challenge) {
        put("Challenge", challenge);
        return this;
    }

    public Comment setText(String text) {
        put("Text", text);
        return this;
    }

    public ParseUser getUser() {
        return getParseUser("User");
    }

    public String getText() {
        return getString("Text");
    }

    public String getAuthor() {
        FacebookInfo fbInfo = (FacebookInfo) getUser().getParseObject("facebookInfo");
        if (fbInfo == null) {
            return "Unknown User";
        }
        try {
            fbInfo = fbInfo.fetchIfNeeded();
            String firstName = fbInfo.getString("firstName");
            String lastName = fbInfo.getString("lastName");
            return firstName + " " + lastName;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "Unknown User";
    }

    public static void getChallengeComments(Challenge challenge,
            FindCallback<Comment> callback) {
        ParseQuery<Comment> query = new ParseQuery<Comment>(Comment.class)
                .whereEqualTo("Challenge", challenge);
        query.include("User");
        query.findInBackground(callback);
    }

    public static void getNumChallengeComments(Challenge challenge,
            CountCallback callback) {
        new ParseQuery<Comment>(Comment.class).whereEqualTo("Challenge",
                challenge).countInBackground(callback);
    }

    public static void addComment(final Challenge challenge, String text) {
        new Comment().setChallenge(challenge)
                .setUser(ParseUser.getCurrentUser()).setText(text)
                .saveEventually();
    }
}

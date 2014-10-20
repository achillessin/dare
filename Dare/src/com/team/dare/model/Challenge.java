package com.team.dare.model;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Challenge")
public class Challenge extends ParseObject {

    private static final String RESPONSE_STATUS_ACCEPTED = "accepted";
    private static final String RESPONSE_STATUS_DECLINED = "declined";
    private static final String RESPONSE_STATUS_UNKNOWN = "unknown";

    public enum RESPONSE_STATUS {
        ACCEPTED, DECLINED, UNKNOWN
    }

    public Challenge() {

    }

    public ParseUser getUserFrom() {
        return getParseUser("UserFrom");
    }

    public void setUserFrom(ParseUser p) {
        put("UserFrom", p);
    }

    public ParseUser getUserTo() {
        return getParseUser("UserTo");
    }

    public void setUserTo(ParseUser p) {
        put("UserTo", p);
    }

    public boolean getCompleted() {
        return getBoolean("Completed");
    }

    public void setCompleted(boolean state) {
        put("Completed", state);
    }

    public String getChallengeText() {
        return getString("ChallengeText");
    }

    public void setChallengeText(String text) {
        put("ChallengeText", text);
    }

    public String getChallengeTitle() {
        return getString("ChallengeTitle");
    }

    public void setChallengeTitle(String text) {
        put("ChallengeTitle", text);
    }

    public RESPONSE_STATUS getResponseStatus() {
        RESPONSE_STATUS stat = null;
        String status = getString("responsestatus");
        if (status.equals(RESPONSE_STATUS_ACCEPTED)) {
            stat = RESPONSE_STATUS.ACCEPTED;
        } else if (status.equals(RESPONSE_STATUS_DECLINED)) {
            stat = RESPONSE_STATUS.DECLINED;
        }
        if (status.equals(RESPONSE_STATUS_UNKNOWN)) {
            stat = RESPONSE_STATUS.UNKNOWN;
        }
        return stat;
    }

    public void setResponseStatus(RESPONSE_STATUS status) {
        String stat;
        switch (status) {
        case ACCEPTED:
            stat = RESPONSE_STATUS_ACCEPTED;
            break;
        case DECLINED:
            stat = RESPONSE_STATUS_DECLINED;
            break;
        default:
            stat = RESPONSE_STATUS_UNKNOWN;
            break;
        }
        put("responsestatus", stat);
    }

    public String getResponseText() {
        return getString("responsetext");
    }

    public void setResponseText(String text) {
        put("responsetext", text);
    }

    public static void getChallenge(String parseID,
            FindCallback<Challenge> callback) {
        ParseQuery<Challenge> query = new ParseQuery<Challenge>(Challenge.class)
                .whereEqualTo("objectId", parseID);
        query.include("UserFrom");
        query.include("UserTo");
        query.findInBackground(callback);

    }
}

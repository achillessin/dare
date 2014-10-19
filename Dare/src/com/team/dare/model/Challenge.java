package com.team.dare.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Challenge")
public class Challenge extends ParseObject {

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

    public boolean getIsAccepted() {
        return getBoolean("isaccepted");
    }

    public void setIsAccepted(boolean status) {
        put("isaccepted", status);
    }

    public String getResponseText() {
        return getString("responsetext");
    }

    public void setResponseText(String text) {
        put("responsetext", text);
    }
}

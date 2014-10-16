package com.team.dare.model;

import com.parse.GetDataCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

@ParseClassName("Challenge")
public class Challenge extends ParseObject {

    private static final String PICTURE_FILENAME = "picture";
    private static final String PICTURE_FILE_EXT = ".jpg";
    private static final String VIDEO_FILENAME = "video";
    private static final String VIDEO_FILE_EXT = ".mp4";

    public enum FILE_TYPE {
        PICTURE_FILE, VIDEO_FILE
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

    // NOTE: ParseFile is only data about the file
    // You have to request the file from the server
    public ParseFile getVideo() {
        return getParseFile("Video");
    }

    // NOTE: setting a parse file only saves the metadata
    // about the file in the parse object.
    // You have to separately save the file to parse.
    public void setVideo(ParseFile p) {
        put("Video", p);
    }

    // NOTE: ParseFile is only data about the file
    // You have to request the file from the server
    public ParseFile getPicture() {
        return getParseFile("Picture");
    }

    // NOTE: setting a parse file only saves the metadata
    // about the file in the parse object.
    // You have to separately save the file to parse.
    public void setPicture(ParseFile p) {
        put("Picture", p);
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

    public void saveFileToServerHelper(byte[] fileData, FILE_TYPE type,
            final FileLoadSaveListener listener) {
        ParseFile file = null;
        switch (type) {
        case PICTURE_FILE:
            file = new ParseFile(PICTURE_FILENAME + PICTURE_FILE_EXT, fileData);
            break;
        case VIDEO_FILE:
            file = new ParseFile(VIDEO_FILENAME + VIDEO_FILE_EXT, fileData);
            break;
        }
        if (file != null) {
            file.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    listener.onSaveDone();
                }
            }, new ProgressCallback() {
                @Override
                public void done(Integer percentDone) {
                    // Update your progress spinner here. percentDone will be
                    // between 0 and 100.
                    listener.onProgress(percentDone);
                }
            });
        }
    }

    public void getFileFromServerHelper(ParseFile file,
            final FileLoadSaveListener listener) {
        file.getDataInBackground(new GetDataCallback() {

            @Override
            public void done(byte[] arg0, ParseException arg1) {
                if (arg1 == null) {
                    listener.onLoadDone(arg0);
                } else {
                    listener.onError(arg1);
                }

            }
        }, new ProgressCallback() {

            @Override
            public void done(Integer arg0) {
                listener.onProgress(arg0);
            }
        });
    }
}

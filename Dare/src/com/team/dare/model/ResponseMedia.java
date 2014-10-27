package com.team.dare.model;

import com.parse.GetDataCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

@ParseClassName("ResponseMedia")
public class ResponseMedia extends ParseObject {

    private static final String FILE_TYPE_IMAGE = "image";
    private static final String FILE_TYPE_VIDEO = "video";

    public enum FILE_TYPE {
        IMAGE_FILE, VIDEO_FILE, UNKNOWN
    }

    public ResponseMedia() {

    }

    public Challenge getChallenge() {
        return (Challenge) getParseObject("Challenge");
    }

    public void setChallenge(Challenge c) {
        put("Challenge", c);
    }

    public void setFileThumbnail(ParseFile f) {
        put("thumbnail", f);
    }

    public ParseFile getFileThumbnail() {
        return getParseFile("thumbnail");
    }

    public FILE_TYPE getFileType() {
        String type = getString("filetype");
        if (type.equals(FILE_TYPE_IMAGE)) {
            return FILE_TYPE.IMAGE_FILE;
        } else if (type.equals(FILE_TYPE_VIDEO)) {
            return FILE_TYPE.VIDEO_FILE;
        }
        return FILE_TYPE.UNKNOWN;
    }

    public void setFileType(FILE_TYPE t) {
        switch (t) {
        case IMAGE_FILE:
            put("filetype", FILE_TYPE_IMAGE);
            break;
        case VIDEO_FILE:
            put("filetype", FILE_TYPE_VIDEO);
            break;
        }
    }

    // NOTE: ParseFile is only data about the file
    // You have to request the file from the server
    public ParseFile getFile() {
        return getParseFile("file");
    }

    // NOTE: setting a parse file only saves the metadata
    // about the file in the parse object.
    // You have to separately save the file to parse.
    public void setFile(ParseFile f) {
        put("file", f);
    }

    static public void saveFileToServerHelper(ParseFile file,
            final FileLoadSaveListener listener) {
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

    static public void getFileFromServerHelper(ParseFile file,
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

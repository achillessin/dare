package com.team.dare.model;

import com.parse.ParseException;

public interface FileLoadSaveListener {

    public void onSaveDone();

    public void onLoadDone(byte[] data);

    public void onProgress(int percentageDone);

    public void onError(ParseException e);
}

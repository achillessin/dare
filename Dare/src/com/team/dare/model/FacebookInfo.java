
package com.team.dare.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.facebook.model.GraphUser;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

@ParseClassName("FacebookInfo")
public class FacebookInfo extends ParseObject {

    public FacebookInfo() {

    }

    public void initialize(GraphUser guser) {
        put("firstName", guser.getFirstName());
        put("lastName", guser.getLastName());
        try {
            URL imgUrl = new URL("https://graph.facebook.com/" + guser.getId() +
                    "/picture?type=large");
            new DownloadImageToParseTask().execute(imgUrl);
        } catch (MalformedURLException e) {
            saveEventually();
        }
    }

    private class DownloadImageToParseTask extends AsyncTask<URL, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(URL... imgUrl) {
            try {
                Bitmap b = BitmapFactory.decodeStream(imgUrl[0].openConnection()
                        .getInputStream());
                return b;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                // Convert it to bytestream
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Compress image to lower quality scale 1 - 100
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] profilePictureBytes = stream.toByteArray();
                final ParseFile file = new ParseFile("picture.jpg", profilePictureBytes);
                file.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        put("profilePicture", file);
                        saveEventually();
                    }
                });
            }
        }
    }
}

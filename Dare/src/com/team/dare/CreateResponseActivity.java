package com.team.dare;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.team.dare.model.Challenge;
import com.team.dare.model.ResponseMedia;

public class CreateResponseActivity extends Activity {

    private static final String TAG = "CreateResponseActivity";
    public static final String KEY_CHALLENGE_ID = "key.challenge.id";
    private static final int REQUEST_CODE_SELECT_IMAGE = 0;
    private static final int REQUEST_CODE_SELECT_VIDEO = 1;

    // array to hold response media
    ArrayList<MediaData> mMediaArray;
    // UI
    FrameLayout mFramelayout;
    // challenge ID
    private String mChallengeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_response);

        mMediaArray = new ArrayList<MediaData>();
        mFramelayout = (FrameLayout) findViewById(R.id.framelayoutMedia);

        Intent intent = getIntent();
        mChallengeID = intent.getStringExtra(KEY_CHALLENGE_ID);
        if (mChallengeID == null) {
            Log.e(TAG, "No challenge id provided in the intent.");
            finish();
        }
    }

    public String getPath(Uri contentUri, Context context) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null,
                    null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // create a thumbnail
    private Bitmap getImageThumbail(Uri file) {
        String tempPath = getPath(file, this);
        BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bm, 512, 384);
        return thumbnail;
    }

    private Bitmap getVideoThumbnail(Uri file) {
        String filepath = getPath(file, this);
        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(filepath,
                MediaStore.Video.Thumbnails.MINI_KIND);
        return thumbnail;
    }

    private void showMediaThumbnail(MediaData media) {
        if (mFramelayout != null) {
            if (media.mediaThumbnail != null) {
                ImageView iv = new ImageView(this);
                iv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));
                iv.setImageBitmap(media.mediaThumbnail);
                mFramelayout.addView(iv);
            }
        }
    }

    // Intent to load media
    public void onGetMedia(View v) {
        final CharSequence[] DialogItems = { "Use Existing Image",
                "Use Existing Video" };
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Choose Media");
        dialogBuilder.setItems(DialogItems,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (DialogItems[which].equals("Use Existing Image")) {
                            Intent intent = new Intent(
                                    Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent,
                                    "Select Image"), REQUEST_CODE_SELECT_IMAGE);
                        } else if (DialogItems[which]
                                .equals("Use Existing Video")) {
                            Intent intent = new Intent(
                                    Intent.ACTION_GET_CONTENT);
                            intent.setType("video/*");
                            startActivityForResult(Intent.createChooser(intent,
                                    "Select Video"), REQUEST_CODE_SELECT_VIDEO);
                        }
                    }
                });
        dialogBuilder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SELECT_IMAGE) {
                MediaData media = new MediaData();
                media.mediaType = ResponseMedia.FILE_TYPE.IMAGE_FILE;
                // get selected file URI
                media.mediaFile = data.getData();
                // create a thumbnail
                media.mediaThumbnail = getImageThumbail(media.mediaFile);
                // add to array
                this.mMediaArray.add(media);
                // show media
                showMediaThumbnail(media);
            } else if (requestCode == REQUEST_CODE_SELECT_VIDEO) {
                MediaData media = new MediaData();
                media.mediaType = ResponseMedia.FILE_TYPE.VIDEO_FILE;
                // get selected file URI
                media.mediaFile = data.getData();
                // create a thumbnail
                media.mediaThumbnail = getVideoThumbnail(media.mediaFile);
                // add to array
                this.mMediaArray.add(media);
                // show media
                showMediaThumbnail(media);
            }
        }
    }

    public void onGetGeoLocation(View v) {

    }

    // save response ( dont forget to save a thumbnail )
    public void onSave(View v) {
        Challenge.getChallenge(mChallengeID, new FindCallback<Challenge>() {

            @Override
            public void done(List<Challenge> objects, ParseException e) {
                if (objects == null || objects.size() == 0) {
                    Log.e(TAG, "No challeng found for given ID.");
                    finish();
                }
                // save data from edittext
                EditText et = (EditText) findViewById(R.id.edittextResponse);
                String text = et.getText().toString();
                // save media array in parseobject
                for (int i = 0; i < mMediaArray.size(); i++) {
                    ResponseMedia respMedia = new ResponseMedia();
                    Challenge c = objects.get(0);
                    c.setResponseText(text);
                    c.saveInBackground();
                    respMedia.setChallenge(c);
                    respMedia.setFileType(mMediaArray.get(i).mediaType);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    mMediaArray.get(i).mediaThumbnail.compress(
                            Bitmap.CompressFormat.JPEG, 100, stream);
                    ParseFile fileThumbnail = new ParseFile(stream
                            .toByteArray(), "thumbnail");
                    fileThumbnail.saveInBackground();
                    respMedia.setFileThumbnail(fileThumbnail);

                    String path = getPath(mMediaArray.get(i).mediaFile,
                            CreateResponseActivity.this);
                    File file = new File(path);
                    try {
                        byte[] fileData = IOUtils
                                .toByteArray(new FileInputStream(file));
                        ParseFile pFile = new ParseFile(fileData, "media");
                        pFile.saveInBackground();
                        respMedia.setFile(pFile);
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    ParseACL acl = new ParseACL();
                    acl.setPublicReadAccess(true);
                    acl.setWriteAccess(ParseUser.getCurrentUser(), true);
                    respMedia.setACL(acl);
                    respMedia.saveInBackground();
                }
                // return to calling activity
                returnToCallingActivity();
            }
        });
    }

    public void onCancel(View v) {
        // return to calling activity
        returnToCallingActivity();
    }

    private void returnToCallingActivity() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_response, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class MediaData {
        public Uri mediaFile;
        public Bitmap mediaThumbnail;
        public ResponseMedia.FILE_TYPE mediaType;

        public MediaData() {

        }
    }
}

package com.team.dare;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.team.dare.model.ResponseMedia;

public class CreateResponseActivity extends Activity {

    private static final int REQUEST_CODE_SELECT_IMAGE = 0;
    private static final int REQUEST_CODE_SELECT_VIDEO = 1;

    // array to hold response media
    ArrayList<MediaData> mMediaArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_response);

        mMediaArray = new ArrayList<MediaData>();
    }

    public String getPath(Uri uri, Activity activity) {
        String[] projection = { MediaColumns.DATA };
        Cursor cursor = activity
                .managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
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
                            intent.setType("images/*");
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
        // save data from edittext
        EditText e = (EditText) findViewById(R.id.edittextResponse);
        String text = e.getText().toString();
        // save media array in parseobject
        for (int i = 0; i < mMediaArray.size(); i++) {
            ResponseMedia respMedia = new ResponseMedia();
        }
        // return to calling activity
        returnToCallingActivity();
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

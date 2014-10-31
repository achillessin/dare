
package com.team.dare;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.team.dare.model.Comment;

public class CommentAdapter extends ArrayAdapter<Comment> {

    Context mContext;
    List<Comment> mComments;

    public CommentAdapter(Context context, int textViewResourceId,
            List<Comment> comments) {
        super(context, textViewResourceId, comments);
        mContext = context;
        mComments = comments;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommentHolder commentHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            commentHolder = new CommentHolder();
            convertView = inflater.inflate(R.layout.comment, parent, false);
            commentHolder.author = (TextView) convertView
                    .findViewById(R.id.author);
            commentHolder.comment = (TextView) convertView
                    .findViewById(R.id.comment);
            commentHolder.picture = (ProfilePictureView) convertView
                    .findViewById(R.id.icon);
            convertView.setTag(commentHolder);
        } else {
            commentHolder = (CommentHolder) convertView.getTag();
        }
        commentHolder.author.setText(mComments.get(position).getAuthor());
        commentHolder.comment.setText(mComments.get(position).getText());
        String profileId = mComments.get(position).getUser()
                .getString("facebookID");
        commentHolder.picture.setProfileId(profileId);
        return convertView;
    }

    class CommentHolder {
        TextView author;
        TextView comment;
        ProfilePictureView picture;
    }
}

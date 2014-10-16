package com.team.dare;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

public class CustomFriendListAdapter extends ArrayAdapter<GraphUser> {
    private final Context context;
    private final List<GraphUser> values;

    public CustomFriendListAdapter(Context context, List<GraphUser> values) {
        super(context, R.layout.layout_friendlist_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.layout_friendlist_item,
                parent, false);
        GraphUser user = values.get(position);
        TextView textView = (TextView) rowView
                .findViewById(R.id.textviewProfileName);
        ProfilePictureView imageView = (ProfilePictureView) rowView
                .findViewById(R.id.imageviewProfilePicture);
        textView.setText(user.getName().toString());
        imageView.setProfileId(user.getId());
        return rowView;
    }
}

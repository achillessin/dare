package com.dare;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.ParseFacebookUtils;

public class CreateDareActivity extends Activity {

	List<GraphUser> userFriendList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_dare);
	}

	public void onGetFriendList(View v) {

		com.facebook.Session session = ParseFacebookUtils.getSession();
		if (session != null && session.isOpened()) {
			Request req = Request.newMyFriendsRequest(
					ParseFacebookUtils.getSession(),
					new Request.GraphUserListCallback() {

						@Override
						public void onCompleted(List<GraphUser> users,
								Response response) {
							// TODO Auto-generated method stub
							userFriendList = users;
							Toast.makeText(CreateDareActivity.this,
									"Got friend list", Toast.LENGTH_LONG)
									.show();
						}
					});
		} else {
			Toast.makeText(this, "you are not logged in", Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_dare, menu);
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
}

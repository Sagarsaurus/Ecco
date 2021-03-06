package com.application.ecco;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CreateUserActivity extends ActionBarActivity {
	private static final String EXTRA_MESSAGE = "com.application.ecco.MESSAGE";
	EditText username, password, email, phone;
	private boolean toSwitch=false;
	private String toSend="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_user);
		username = (EditText)findViewById(R.id.username);
		password = (EditText)findViewById(R.id.password);
		email = (EditText)findViewById(R.id.email);
		phone = (EditText)findViewById(R.id.phone);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_user, menu);
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
	
	public void postAndCreateUser(View view) {
		AsyncHttpClient client = new AsyncHttpClient();
    	RequestParams params = new RequestParams();
    	params.put("username", username.getText().toString());
    	params.put("password", MainActivity.md5(password.getText().toString()));
    	params.put("email", email.getText().toString());
    	params.put("phone", phone.getText().toString());
    	
    	client.post("http://ecco.herokuapp.com/api/createUser", params, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(int statuscode, Header[] headers, byte[] response,
					Throwable arg3) {
				toSwitch=false;
				String s = new String(response);
				Context c = getApplicationContext();
				int duration = Toast.LENGTH_SHORT;
				try {
					JSONObject json = new JSONObject(s);
					Toast.makeText(c, (CharSequence) json.get("error"), duration).show();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onSuccess(int statuscode, Header[] headers, byte[] response) {
				toSwitch=true;
				toSend=new String(response);
				Toast.makeText(getApplicationContext(), "Successfully Created User!", Toast.LENGTH_SHORT).show();
			}
    	});
    	if(toSwitch) {
	    	Intent intent = new Intent(this, HomeActivity.class);
	    	JSONObject json;
			try {
				json = new JSONObject(toSend);
		    	intent.putExtra(EXTRA_MESSAGE, json.get("_id").toString());
		    	startActivity(intent);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
	}
}

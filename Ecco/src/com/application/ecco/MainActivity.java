package com.application.ecco;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.*;



public class MainActivity extends ActionBarActivity {
	EditText username;
	EditText password;
	Button login;
	final static String EXTRA_MESSAGE = "com.application.ecco.MESSAGE";
	private boolean toSwitch=false;
	private String toSend="";
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		username = (EditText)findViewById(R.id.editText1);
		password = (EditText)findViewById(R.id.editText2);
		login = (Button)findViewById(R.id.button1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
    
    public void login(View view) {
    	AsyncHttpClient client = new AsyncHttpClient();
    	RequestParams params = new RequestParams();
    	params.put("username", username.getText().toString());
    	params.put("password", password.getText().toString());
    	client.post("http://ecco.herokuapp.com/api/login", params, new AsyncHttpResponseHandler() {
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
    
    public void createUser(View view) {
    	Intent intent = new Intent(this, CreateUserActivity.class);
    	startActivity(intent);
    }
}

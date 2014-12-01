package com.application.ecco;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
	static String userID = "";
	static String userName = "";
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
    
    public static final String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
     
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
     
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    
    public void login(View view) throws InterruptedException, ExecutionException, JSONException {
    	AsyncClient client = new AsyncClient();
    	String response = client.execute(username.getText().toString(), MainActivity.md5(password.getText().toString())).get();
    	JSONObject json = new JSONObject(response);
    	System.out.println(response);
    	if(!response.contains("error")) {
	    	Intent intent = new Intent(this, HomeActivity.class);
	    	this.userID=json.get("_id").toString();
	    	this.userName = username.getText().toString();
	    	startActivity(intent);
    	}
    	
    	else {
    		Toast.makeText(getApplicationContext(), json.get("error").toString(), Toast.LENGTH_SHORT).show();
    	}
    	/*AsyncHttpClient client = new AsyncHttpClient();
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
		    	this.userID=json.get("_id").toString();
		    	startActivity(intent);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}*/
    }
    
    public void createUser(View view) {
    	Intent intent = new Intent(this, CreateUserActivity.class);
    	startActivity(intent);
    }
    
    private static class AsyncClient extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair("username", params[0]));
			list.add(new BasicNameValuePair("password", params[1]));
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost post = new HttpPost("http://ecco.herokuapp.com/api/login");
			try {
				post.setEntity(new UrlEncodedFormEntity(list));
				HttpResponse response = httpclient.execute(post);
				String json = EntityUtils.toString(response.getEntity());
				return json;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
	}
    
    public void openMap(View view) {
    	Intent intent = new Intent(this, Map.class);
    	startActivity(intent);
    }
    
    public void openSharingMap(View view) {
    	Intent intent = new Intent(this, ShareLocationActivity.class);
    	startActivity(intent);
    }
}

package com.application.ecco;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.android.gms.maps.MapView;
import com.loopj.android.http.*;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class HomeActivity extends FragmentActivity implements ActionBar.TabListener {
	final static String EXTRA_MESSAGE = "com.application.ecco.MESSAGE";
	JSONObject json;
	ActionBar bar = null;
	private static Context c = null;
	static String shareToUserID="";
	static String otherUserID = "";
	static double lat = 0;
	static double lng = 0;
	static String friendshipUserID="";
	static int floor = 0;
	public static boolean placeOverlay = false;
	
	PagerAdapter adapter;
	ViewPager pager;
	
	String[] title = { "News Feed", "Send Friend Request", "Pending Requests", "Friends" };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		adapter = new PagerAdapter(getSupportFragmentManager());
		
		bar = getActionBar();
		
		bar.setHomeButtonEnabled(false);
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		pager = (ViewPager)findViewById(R.id.pager);
		pager.setAdapter(adapter);
		pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				bar.setSelectedNavigationItem(position);
			}
		});
		
		for (String name : title) {
			bar.addTab(bar.newTab().setText(name)
					.setTabListener((TabListener) this));
		}
		
		HomeActivity.c = getApplicationContext();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
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
	
	public static class PagerAdapter extends FragmentPagerAdapter {

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int index) {
			switch (index) {
			case 0:
				return new NewsFeedFragment();
			case 1:
				return new ProfileFragment();
			case 2:
				return new RequestListFragment();
			case 3: 
				return new FriendFragment();
			//case 4: 
			//	return new ShareLocationFragment();
			}
			return null;
		}

		@Override
		public int getCount() {
			return 4;
		}
		
	}
	
	public static class ProfileFragment extends Fragment implements View.OnClickListener {
		View root;
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			final View root = inflater.inflate
					(R.layout.user_profile, container, false);
			root.findViewById(R.id.button1)
				.setOnClickListener(this);
			this.root = root;
			return root;
		}

		@Override
		public void onClick(View v) {
			AsyncHttpClient client = new AsyncHttpClient();
	    	RequestParams params = new RequestParams();
	    	params.put("userID", MainActivity.userID);
	    	EditText text = (EditText)root.findViewById(R.id.sendFriendRequest);
	    	params.put("requestedUsername", text.getText().toString());
	    	client.post("http://ecco.herokuapp.com/api/sendFriendRequest", params, new AsyncHttpResponseHandler() {
				@Override
				public void onFailure(int statuscode, Header[] headers, byte[] response,
						Throwable arg3) {
					String s = new String(response);
					try {
						JSONObject json = new JSONObject(s);
						Toast.makeText(HomeActivity.c, (CharSequence) json.get("error"), Toast.LENGTH_SHORT).show();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void onSuccess(int statuscode, Header[] headers, byte[] response) {
					Toast.makeText(HomeActivity.c, "Request sent successfully!", Toast.LENGTH_SHORT).show();
				}
	    	});
			
		}
		
	}
	
	public static class RequestListFragment extends ListFragment {
		JSONArray tryTwo;
		String[] requests;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View root = inflater.inflate
					(R.layout.request_list, container, false);
			AsyncClient client = new AsyncClient();
			try {
				tryTwo = client.execute(MainActivity.userID).get();
				ArrayList<String> arraylist = new ArrayList<String>();
				for(int i = 0; i < tryTwo.length(); i++) {
					JSONObject object = tryTwo.getJSONObject(i);
					if(object.getString("friendshipStatus").equals("0") && !object.getString("userID").equals(MainActivity.userID)) {
						if(!object.getString("username").equals(MainActivity.userName)) {
							arraylist.add(object.getString("username"));
						}
					}
				}
				
				Object[] names = arraylist.toArray();
				requests = new String[names.length];
				for(int i = 0; i < requests.length; i++) {
					requests[i] = (String) names[i];
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.request_list, android.R.id.empty, requests);
				setListAdapter(adapter);
				//root.setListAdapter(adapter);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return root;
		}
		
		
		public void onListItemClick(ListView l, View v, int position, long id) {
			String username = requests[position];
			for(int i = 0; i < tryTwo.length(); i++) {
				try {
					JSONObject json = tryTwo.getJSONObject(i);
					if(json.get("username").equals(username)) {
						if(json.get("userID").equals(MainActivity.userID)) {
							HomeActivity.otherUserID = (String) json.get("requestedID");
						}
						else {
							HomeActivity.otherUserID = (String) json.get("userID");
						}
						requests=null;
						Intent decisionIntent = new Intent(getActivity(), DecisionActivity.class);
				    	getActivity().startActivity(decisionIntent);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		@Override
		public void onPause() {
			super.onPause();
			AsyncClient client = new AsyncClient();
			try {
				client.execute(MainActivity.userID).get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		}
	}
	
	
	public static class FriendFragment extends ListFragment {
		JSONArray tryTwo;
		String[] friends;
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View root = inflater.inflate
					(R.layout.friend_list, container, false);
			AsyncClient client = new AsyncClient();
			try {
				//JSONArray tryOne = client.execute(MainActivity.userID).get();
				tryTwo = client.execute(MainActivity.userID).get();
				ArrayList<String> arraylist = new ArrayList<String>();
				for(int i = 0; i < tryTwo.length(); i++) {
					JSONObject object = tryTwo.getJSONObject(i);
					if(object.getString("friendshipStatus").equals("1")) {
						arraylist.add(object.getString("username"));
					}
				}
				
				Object[] names = arraylist.toArray();
				friends = new String[names.length];
				for(int i = 0; i < friends.length; i++) {
					friends[i] = (String) names[i];
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.friend_list, android.R.id.empty, friends);
				setListAdapter(adapter);

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return root;
		}
		
		public void onListItemClick(ListView l, View v, int position, long id) {
			String username = friends[position];
			for(int i = 0; i < tryTwo.length(); i++) {
				try {
					JSONObject json = tryTwo.getJSONObject(i);
					if(json.get("username").equals(username)) {
						if(json.get("userID").equals(MainActivity.userID)) {
							shareToUserID = (String) json.get("requestedID");
							friendshipUserID = MainActivity.userID;
						}
						
						else {
							shareToUserID = (String) json.get("userID");
							friendshipUserID = MainActivity.userID;
						}
						friends=null;
						Intent shareIntent = new Intent(getActivity(), ShareLocationActivity.class);
					    getActivity().startActivity(shareIntent);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		}

	}
	
	public static class NewsFeedFragment extends ListFragment {
		String[] shares;
		JSONArray locations;
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View root = inflater.inflate
					(R.layout.news_feed, container, false);
			AsyncLocationClient client = new AsyncLocationClient();
			try {
				//JSONArray tryOne = client.execute(MainActivity.userID).get();
				locations = client.execute(MainActivity.userID).get();
				ArrayList<String> arraylist = new ArrayList<String>();
				for(int i = 0; i < locations.length(); i++) {
					JSONObject curr = locations.getJSONObject(i);
					String user = curr.getString("senderUsername");
					String building = curr.getString("building");
					String floor = curr.getString("floor");
					arraylist.add("User: "+user+" Building: "+building+" Floor: "+floor);
				}
				
				Object[] names = arraylist.toArray();
				shares = new String[names.length];
				for(int i = 0; i < names.length; i++) {
					shares[i] = (String) names[i];
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.news_feed, android.R.id.empty, shares);
				setListAdapter(adapter);

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return root;
			
		}
		
		public void onListItemClick(ListView l, View v, int position, long id) {
			try {
				JSONObject json = locations.getJSONObject(position);
				HomeActivity.lat = Double.parseDouble((String) json.get("lat"));
				HomeActivity.lng = Double.parseDouble((String) json.get("lng"));
				String floor = json.getString("floor");
				if(floor.equals("1") || floor.equals("2") || floor.equals("3")) {
					HomeActivity.floor = Integer.parseInt(floor);
					HomeActivity.placeOverlay=true;
				}
				
				else {
					HomeActivity.placeOverlay=false;
				}
				Intent i = new Intent(getActivity(), Map.class);
				startActivity(i);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public static class ShareLocationFragment extends Fragment {
		private MapView map = null;
		//private MyLocationOverlay me = null;
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			//View root = inflater.inflate
					//(R.layout.request_list, container, false);
			
			return (new FrameLayout(getActivity()));
			
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
		    super.onActivityCreated(savedInstanceState);
		    map=new MapView(getActivity());
		    map.setClickable(true);
		    ((ViewGroup)getView()).addView(map);

		}
	}
	
	public static void sendRequest(View view) {
		AsyncHttpClient client = new AsyncHttpClient();
    	RequestParams params = new RequestParams();
    	params.put("userID", MainActivity.userID);
    	EditText text = (EditText)view.findViewById(R.id.sendFriendRequest);
    	params.put("requestedUsername", text.getText().toString());
    	client.post("http://ecco.herokuapp.com/api/sendFriendRequest", params, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(int statuscode, Header[] headers, byte[] response,
					Throwable arg3) {
				String s = new String(response);
				try {
					JSONObject json = new JSONObject(s);
					Toast.makeText(HomeActivity.c, (CharSequence) json.get("error"), Toast.LENGTH_SHORT).show();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onSuccess(int statuscode, Header[] headers, byte[] response) {
				Toast.makeText(HomeActivity.c, "Request sent successfully!", Toast.LENGTH_SHORT).show();
			}

    	});
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// go to the view
		pager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}
	
	private static class AsyncClient extends AsyncTask<String, String, JSONArray> {

		@Override
		protected JSONArray doInBackground(String... params) {
			// TODO Auto-generated method stub
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair("userID", params[0]));
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost post = new HttpPost("http://ecco.herokuapp.com/api/getFriends");
			try {
				post.setEntity(new UrlEncodedFormEntity(list));
				HttpResponse response = httpclient.execute(post);
				String json = EntityUtils.toString(response.getEntity());
				JSONArray array = new JSONArray(json);
				return array;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
	}
	
	private static class AsyncLocationClient extends AsyncTask<String, String, JSONArray> {

		@Override
		protected JSONArray doInBackground(String... params) {
			// TODO Auto-generated method stub
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair("userID", params[0]));
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost post = new HttpPost("http://ecco.herokuapp.com/api/listSharedLocations");
			try {
				post.setEntity(new UrlEncodedFormEntity(list));
				HttpResponse response = httpclient.execute(post);
				String json = EntityUtils.toString(response.getEntity());
				JSONArray array = new JSONArray(json);
				return array;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
	}


}

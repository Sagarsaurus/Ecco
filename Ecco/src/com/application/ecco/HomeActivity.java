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
	
	public static class ProfileFragment extends Fragment {
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			final View root = inflater.inflate
					(R.layout.user_profile, container, false);
			root.findViewById(R.id.button1)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View root) {
						// methods need to be static
						 sendRequest(root);
					}
				});
			return root;
		}
		
	}
	
	public static class RequestListFragment extends ListFragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View root = inflater.inflate
					(R.layout.request_list, container, false);
			AsyncClient client = new AsyncClient();
			try {
				//JSONArray tryOne = client.execute(MainActivity.userID).get();
				JSONArray tryTwo = client.execute(MainActivity.userID).get();
				ArrayList<String> arraylist = new ArrayList<String>();
				for(int i = 0; i < tryTwo.length(); i++) {
					JSONObject object = tryTwo.getJSONObject(i);
					if(object.getString("friendshipStatus").equals("0")) {
						if(!object.getString("username").equals(MainActivity.userName)) {
							arraylist.add(object.getString("username"));
						}
					}
				}
				
				Object[] names = arraylist.toArray();
				String[] arr = new String[names.length];
				for(int i = 0; i < arr.length; i++) {
					arr[i] = (String) names[i];
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.request_list, android.R.id.empty, arr);
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
	}
	
	
	public static class FriendFragment extends ListFragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View root = inflater.inflate
					(R.layout.friend_list, container, false);
			AsyncClient client = new AsyncClient();
			try {
				//JSONArray tryOne = client.execute(MainActivity.userID).get();
				JSONArray tryTwo = client.execute(MainActivity.userID).get();
				ArrayList<String> arraylist = new ArrayList<String>();
				for(int i = 0; i < tryTwo.length(); i++) {
					JSONObject object = tryTwo.getJSONObject(i);
					if(object.getString("friendshipStatus").equals("1")) {
						if(!object.getString("username").equals(MainActivity.userName)) {
							arraylist.add(object.getString("username"));
						}
					}
				}
				
				Object[] names = arraylist.toArray();
				String[] arr = new String[names.length];
				for(int i = 0; i < arr.length; i++) {
					arr[i] = (String) names[i];
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.friend_list, android.R.id.empty, arr);
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
	}
	
	public static class NewsFeedFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View root = inflater.inflate
					(R.layout.request_list, container, false);
			return root;
			
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
    	System.out.println(text);
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
				for(int i = 0; i < array.length(); i++) {
					System.out.println(array.getString(i));
				}
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

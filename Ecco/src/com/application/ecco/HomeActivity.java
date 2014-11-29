package com.application.ecco;

import com.loopj.android.http.*;
import org.apache.http.Header;
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
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class HomeActivity extends FragmentActivity implements ActionBar.TabListener {
	final static String EXTRA_MESSAGE = "com.application.ecco.MESSAGE";
	JSONObject json;
	
	PagerAdapter adapter;
	ViewPager pager;
	
	String[] title = { "Send Friend Request", "Pending Requests", "Friends", "Location Services" };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		adapter = new PagerAdapter(getSupportFragmentManager());
		
		final ActionBar bar = getActionBar();
		
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
				return new ProfileFragment();
			case 1:
				return new RequestListFragment();
			}
			return null;
		}

		@Override
		public int getCount() {
			return 2;
		}
		
	}
	
	public static class ProfileFragment extends Fragment {
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View root = inflater.inflate
					(R.layout.user_profile, container, false);
			root.findViewById(R.id.button1)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// methods need to be static
						// sendRequest(v);
					}
				});
			return root;
		}
		
	}
	
	public static class RequestListFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View root = inflater.inflate
					(R.layout.request_list, container, false);
			return root;
		}
	}
	
	public void sendRequest(View view) {
		Intent intent = getIntent();
		String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
		AsyncHttpClient client = new AsyncHttpClient();
    	RequestParams params = new RequestParams();
    	params.put("userID", message);
    	EditText text = (EditText)findViewById(R.id.sendFriendRequest);
    	params.put("requestedUsername", text.getText().toString());
    	client.post("http://ecco.herokuapp.com/api/sendFriendRequest", params, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(int statuscode, Header[] headers, byte[] response,
					Throwable arg3) {
				String s = new String(response);
				try {
					json = new JSONObject(s);
					Toast.makeText(getApplicationContext(), (CharSequence) json.get("error"), Toast.LENGTH_SHORT).show();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onSuccess(int statuscode, Header[] headers, byte[] response) {
				Toast.makeText(getApplicationContext(), "Request sent successfully!", Toast.LENGTH_SHORT).show();
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

}

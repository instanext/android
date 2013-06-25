package com.instanexapi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.SharedPreferences;
import android.view.Menu;
import android.widget.ArrayAdapter;

public class CompanyListActivity extends ListActivity {

	private CompanyListTask mCompanyTask = null;

	/**
	 * PREFS_NAME setting for user prefs.
	 */
	public static final String PREFS_NAME = "InstanexapiFile";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.company_list_layout_temp);
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	    String apiToken = settings.getString("ApiToken", ""); 
	    String accessKey = settings.getString("AccessKey", "52029fdc-55c7-4a5c-82d0-a17800d03ef9"); //getBoolean("AccessKey", false);
	    String serverURL = settings.getString("serverURL", "http://api.instanexdev.com");
	    String secretKey = settings.getString("secretKey", "LMsRKlmz+IlLbJR7adp0KAaWYwONkMggHZ0L0aktU0A=");
	    
		mCompanyTask = new CompanyListTask();
		mCompanyTask.execute(apiToken, accessKey, serverURL, secretKey);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.company_list_layout_temp, menu);
		return true;
	}
	
	public class CompanyListTask extends AsyncTask<String, Void, String[]> {

		@Override
		protected String[] doInBackground(String... params) {
			
			String apitoken = params[0];
			String accessKey = params[1];
			String serverURL = params[2];
			String secretKey = params[3];
			
			String[] list = null;
			InstanexAccess api = new InstanexAccess(accessKey, secretKey, apitoken, serverURL);
			list = api.getCompanies();
			return list;
		}
		
		@Override
		protected void onPostExecute(final String[] list) {
			mCompanyTask = null;
			
			
			setListAdapter(new ArrayAdapter<String>(CompanyListActivity.this, R.layout.list_item, R.id.label, list));
	        
		}

		@Override
		protected void onCancelled() {
			mCompanyTask = null;
		}
		
	}
}

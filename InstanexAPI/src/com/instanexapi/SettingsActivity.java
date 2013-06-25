package com.instanexapi;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class SettingsActivity extends Activity {

	public static final String PREFS_NAME = "InstanexapiFile";
	
	private EditText appKeyEdit; // = (EditText)findViewById(R.id.appKeyEditText);
	private EditText serverURLEdit; // = (EditText)findViewById(R.id.serverURLEditText);
	private EditText secretKeyEdit; // = (EditText)findViewById(R.id.secretKeyEditText);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	    String accessKey = settings.getString("AccessKey", ""); //getBoolean("AccessKey", false);
	    String serverURL = settings.getString("serverURL", "https://api.instanexdev.com");
	    String secretKey = settings.getString("secretKey", "");
	    appKeyEdit = (EditText)findViewById(R.id.appKeyEditText);
	    serverURLEdit = (EditText)findViewById(R.id.serverURLEditText);
	    secretKeyEdit = (EditText)findViewById(R.id.secretKeyEditText);
	    appKeyEdit.setText(accessKey);
	    serverURLEdit.setText(serverURL);
	    secretKeyEdit.setText(secretKey);
		
		
		findViewById(R.id.saveSettingsButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view)
			{
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		        SharedPreferences.Editor editor = settings.edit();
		        editor.putString("AccessKey", appKeyEdit.getText().toString());
		        editor.putString("serverURL", serverURLEdit.getText().toString());
		        editor.putString("secretKey", secretKeyEdit.getText().toString());
		        // Commit the edits!
		        editor.commit();
			      
				Intent intent = new Intent(view.getContext(), FullscreenActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.id.settings_layout, menu);
		return true;
	}

}

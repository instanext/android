package com.instanexapi;

import org.json.JSONObject;

import com.instanexapi.SettingsActivity;
import com.instanexapi.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * PREFS_NAME setting for user prefs.
	 */
	public static final String PREFS_NAME = "InstanexapiFile";
	
	private String apitoken = "";
	
	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_fullscreen);

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.mainLinearLayout);

		setLabels();
		
		findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(view.getContext(), LoginActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
		
		findViewById(R.id.getCompanies).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(v.getContext(), CompanyListActivity.class);
				startActivity(intent);
			}
		});
		
		findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		        SharedPreferences.Editor editor = settings.edit();
		        editor.putString("ApiToken", "");
		        // Commit the edits!
		        editor.commit();
		        
		        ((TextView)findViewById(R.id.userIDtextView)).setText("");
		    	((TextView)findViewById(R.id.userNameTextView)).setText("");
		    	((TextView)findViewById(R.id.firstNameTextView)).setText("");
		    	((TextView)findViewById(R.id.lastNameTextView)).setText("");
			}
		});
		
		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.dummy_button).setOnClickListener(
				mDelayHideTouchListener);
	}

	/**
	 * 
	 */
	private void setLabels() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		apitoken = settings.getString("ApiToken", ""); 
	    
	    if (apitoken != "") {
	    	//set the labels
	    	try {
	    	
	    	String base64result = apitoken.split("\\.")[1]; // strList.get(1);
			JSONObject loginKeys = new JSONObject(new String(Base64.decode(base64result, 0)));
	        String UserID = loginKeys.getString("http://schemas.instanext.services.api/identity/claims/userid");
	        String UserName = loginKeys.getString("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/name");
	        String FirstName = loginKeys.getString("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname");
	        String LastName = loginKeys.getString("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname");
	    	
	    	((TextView)findViewById(R.id.userIDtextView)).setText(UserID);
	    	((TextView)findViewById(R.id.userNameTextView)).setText(UserName);
	    	((TextView)findViewById(R.id.firstNameTextView)).setText(FirstName);
	    	((TextView)findViewById(R.id.lastNameTextView)).setText(LastName);
	    	//userIDLabel.setText(UserID);
	    	}
	    	catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    }
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setLabels();
	}
	//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		startActivity(new Intent(this., this));
//	}
	
	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnClickListener mDelayHideTouchListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (AUTO_HIDE) {
				//Intent intent = new Intent(this, );
				Intent intent = new Intent(view.getContext(), SettingsActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			//return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
}

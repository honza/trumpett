package com.honza.fishwire;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Init extends Activity {
	
	/**
	 * This is the entry point to our application.
	 * It loads the preferences to see if the user has already set up their
	 * account. If not, they will be instructed to do so. 
	 * 
	 * If their account has been set up, a new activity will be launched to 
	 * view content from Twitter.
	 * 
	 */
	
	public final String PREFERENCES = "trumpett";
	public String user_key = null;
	public String user_secret = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SharedPreferences settings = getSharedPreferences(PREFERENCES, 0);
        user_key = settings.getString("user_key", null);
        user_secret = settings.getString("user_secret", null);
         
        if (user_key == null){
        	// We need the user to authenticate. Display screen to ask them to do so.
        	setContentView(R.layout.setup_screen);
        	Button connect_btn = (Button)findViewById(R.id.connect_btn);
        	connect_btn.setOnClickListener(startAuth);
        } else {
        	Intent a = new Intent(Init.this, Home.class);
        	startActivity(a);
        }
        
    }
    
    private OnClickListener startAuth = new OnClickListener(){

		@Override
		public void onClick(View v) {
			Intent i = new Intent(Init.this, com.honza.fishwire.OAuth.class);
			startActivityForResult(i, 1);
			
		}
		 
	 };
}
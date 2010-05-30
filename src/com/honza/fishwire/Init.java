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
	
	public final String PREFERENCES = "Fishwire";
	public String user_key = null;
	public String user_secret = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("honza", "init activity");
        SharedPreferences settings = getSharedPreferences(PREFERENCES, 0);
        user_key = settings.getString("user_key", null);
        user_secret = settings.getString("user_secret", null);
        
        /* This is to support the xml preferences activity thing */
        
        SharedPreferences x = PreferenceManager.getDefaultSharedPreferences(this);
        
        String hm = x.getString("pref_dialog", "nic");
        Log.v("honza", hm);
        
         
        if (user_key == null){
        	setContentView(R.layout.home_tab_empty);
        	Button connect_btn = (Button)findViewById(R.id.connect_btn);
        	connect_btn.setOnClickListener(startAuth);
        } else {
        	Intent a = new Intent(Init.this, Fishwire.class);
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
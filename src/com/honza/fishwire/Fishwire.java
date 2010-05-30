package com.honza.fishwire;

import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class Fishwire extends TabActivity {
	
	
	/* Some helpers */
	
	public final int MENU_SETTINGS = 1;
	public final int MENU_REFRESH = 2;
	public final int MENU_EXIT = 3;

	private boolean started = false;
	public static RemoteServiceConnection conn = null;
	public static TweetServiceInterface remoteService;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        Log.v("honza", "Top of Fishwire on create...");
        startService();
        Log.v("honza", "After startservice...");
       
        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        intent = new Intent().setClass(Fishwire.this, HomeTab.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("home").setIndicator("Home",
                          res.getDrawable(R.drawable.ic_tabs_fishwire))
                      .setContent(intent);
        tabHost.addTab(spec);
        

        // Do the same for the other tabs
        intent = new Intent().setClass(Fishwire.this, com.honza.fishwire.RepliesTab.class);
        spec = tabHost.newTabSpec("replies").setIndicator("Replies",
                          res.getDrawable(R.drawable.ic_tabs_replies))
                      .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(Fishwire.this, com.honza.fishwire.ComposeTab.class);
        spec = tabHost.newTabSpec("compose").setIndicator("Compose",
                          res.getDrawable(R.drawable.ic_tabs_fishwire))
                      .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
        
    }
    
    private void startService(){
    	if (started){
    		Log.v("honza", "service already started");
    	} else {
    		Intent i = new Intent();
   			i.setClassName("com.honza.fishwire", "com.honza.fishwire.TweetService");
   			startService(i);
   			started = true;
   			Log.v("honza", "service started...");
    	}
    	
    }
    
    private void bindService(){
    	if(conn == null) {
			conn = new RemoteServiceConnection();
			Intent i = new Intent();
			i.setClassName("com.honza.fishwire", "com.honza.fishwire.TweetService");
			bindService(i, conn, Context.BIND_AUTO_CREATE);
			Log.v("honza", "service bound" );
			if (remoteService == null){
				Log.v("honza", "remote is null in bind()");
			} else {
				Log.v("honza", "remote is NOT null in bind()");
			}
		} else {
			Log.v("honza", "service cannot be bound");
		}
    }
    private void invokeService() {
		if(conn == null) {
			Log.v("honza", "conn is null");
		} else {
			try {
				if (remoteService == null){
					Log.v("honza", "remoteService is null");
				}
				int counter = remoteService.getCounter();
				  
				  Log.v("honza", "Counter value: "+Integer.toString( counter ) );
				 
			} catch (RemoteException re) {
				
			}
		}
	}
    
    
    
    
    class RemoteServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName className, 
			IBinder boundService ) {
          remoteService = TweetServiceInterface.Stub.asInterface((IBinder)boundService);
          Log.v("honza", "in onServiceConnected()" );
        }

        public void onServiceDisconnected(ComponentName className) {
          remoteService = null;
		   Log.d( getClass().getSimpleName(), "onServiceDisconnected" );
        }
    };
 

    /* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_SETTINGS, 0, "Settings");
        menu.add(0, MENU_REFRESH, 0, "Refresh");
        menu.add(0, MENU_EXIT, 0, "Quit");
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_SETTINGS:
            Intent settings = new Intent(Fishwire.this, SettingsScreen.class);
            startActivity(settings);
            return true;
        case MENU_REFRESH:
            // write refresh code
        	invokeService();
            return true;
        case MENU_EXIT:
            // write exit code
            return true;
        }
        return false;
    }
    
    
    
}
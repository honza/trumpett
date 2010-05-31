package com.honza.fishwire;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeTab extends Activity {
	
	public static final String PREFERENCES = "Fishwire";
	public String user_key = "";
	public String user_secret = "";
	public long last_id = 0;
	public List<Message> messageList = null;
	public Handler handler = new Handler();
	public int delay = 5000;
	private MessageRowAdapter adapter;
	private MessageFetcher fetcher;
	
	private TweetService mTweetService;
	private ServiceConnection mConnection = new ServiceConnection(){
		public void onServiceConnected(ComponentName className, IBinder service){
			mTweetService = ((TweetService.LocalBinder)service).getService();
		}
		public void onServiceDisconnected(ComponentName className){
			mTweetService = null;
		}
	};
	
	private final Runnable runnable = new Runnable() {
        public void run() {
            Log.v("honza", "timer");
            Log.v("honza", "Checking if there are queued messages...");
            UpdateTimeline updater = new UpdateTimeline();
            Boolean newStuff = false;
            updater.execute(HomeTab.this.last_id);
            
            if (newStuff == true){
            	HomeTab.this.adapter.notifyDataSetChanged();
            	
            }
            
            /* Comment out the following line to prevent looping */
            
            HomeTab.this.handler.postDelayed(HomeTab.this.runnable, delay);
        }
    };
		
	/* End of declarations */
	
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        Log.v("honza", "Top of HomeTab onCreate...");
	        SharedPreferences settings = getSharedPreferences(PREFERENCES, 0);
	        user_key = settings.getString("user_key", null);
	        user_secret = settings.getString("user_secret", null);
	        
	        if (user_key == null){
	        	setContentView(R.layout.home_tab_empty);
	        	Button connect_btn = (Button)findViewById(R.id.connect_btn);
	        	connect_btn.setOnClickListener(startAuth);
	        } else {
	        	setContentView(R.layout.home_tab);
	        	doStart();
	        } 
	   
	        Button a = (Button)findViewById(R.id.invoke);
	        a.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					//invokeService();
					
				}
	        	
	        });
			
	
	    }
	 
	 private OnClickListener startAuth = new OnClickListener(){

		@Override
		public void onClick(View v) {
			Intent i = new Intent(HomeTab.this, com.honza.fishwire.OAuth.class);
			startActivityForResult(i, 1);
		}
		 
	 };
	 protected void start(Intent intent) {
	    	this.startActivityForResult(intent,1);
	    }
	 protected void onActivityResult(int requestCode, int resultCode, Intent data){
		 Log.v("honza", "in on activity result");
		 
	 }
	 
	 public void startTweetService(){
		 startService(new Intent(HomeTab.this, TweetService.class));
		 bindTweetService();
	 }
	 public void bindTweetService(){
		 Log.v("honza", "bindTweetService()");
		 getApplicationContext().bindService(new Intent(HomeTab.this, TweetService.class), mConnection, Context.BIND_AUTO_CREATE);
	 }
	 
	 public void doStart(){
		 
		 	Log.v("honza", "doStart");
		 	startTweetService();
		 	
		 	messageList = new ArrayList<Message>();
	        fetcher = new MessageFetcher("home", user_key, user_secret);
	        messageList = fetcher.getMessages(0);
	        if (messageList.size() != 0){
		        Message m = messageList.get(0);
		        last_id = m.id;
		        Log.v("honza", "last id: " + last_id);
	        } 
	        adapter = new MessageRowAdapter(this, messageList);
	        
	        ListView lv = (ListView)findViewById(R.id.list);
	        lv.setAdapter(adapter);
	        
	        Log.v("honza", "before timer");
	       
	        handler.postDelayed(runnable, delay);
	 }
	 
	 public class UpdateTimeline extends AsyncTask<Long, Void, Boolean> {

		
		protected Boolean doInBackground(Long... last_id) {
			Log.v("honza", "this is inside of doinbackground");
			
			/*
			 * This is being rewritten.
			 * Old: Message fetcher to get tweets from Twitter.
			 * New: Checking with out TweetService to see if it collected any new tweets.
			 * 
			 */
			
			if (mTweetService == null){
				Log.v("honza", "mTweetService is null");
			} else {
				Log.v("honza", "mTWeetService not null");
			}
			
			List<Message> newMessages = mTweetService.getMessages();
			if (newMessages == null){
				Log.v("honza", "newMessages is null");
				return false;
			}
        	int size = newMessages.size();
        	Message m;
        	if (size != 0){
        		if (size == 1){
        			Log.v("honza", "1 new queued message...");
        			m = newMessages.get(0);
        			HomeTab.this.messageList.add(0, m);
        		} else {
        			Log.v("honza", Integer.toString(size) + " new messages...");
        			m = newMessages.get(size - 1);
        			for (int i = 0; i < size; i++){
        				Message a = newMessages.get(i);
        				HomeTab.this.messageList.add(0, a);
        			}
        		}
        		HomeTab.this.last_id = m.id;
        		return true;
        	} else {
        		Log.v("honza", "No messages in the queue...");
        		return false;
        	}			
			
		}
		 
	 }
}

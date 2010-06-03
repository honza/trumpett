package com.honza.fishwire;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class TweetService extends Service {
	
	private Handler serviceHandler;
	private Task myTask = new Task();
	public long last_id = 0L;
	public int minutes = 5;
	public int delay = minutes*60*1000;
		
	public static final String PREFERENCES = "Fishwire";
	public String user_key = "";
	public String user_secret = "";
	public List<Message> messageList = null;
	public Handler handler = new Handler();
	
	private MessageFetcher fetcher;
	
	public List<Message> getMessages(){
		return messageList;
	}
	
	public void resetMessages(){
		messageList = null;
	}
	
	@Override
	public IBinder onBind(Intent i) {
		Log.d(getClass().getSimpleName(), "onBind()");
		return mBinder;
	}
	
	public class LocalBinder extends Binder {
		TweetService getService(){
			return TweetService.this;
		}
	}

	private final IBinder mBinder = new LocalBinder();
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.d(getClass().getSimpleName(),"onCreate()");
		
		SharedPreferences settings = getSharedPreferences(PREFERENCES, 0);
        user_key = settings.getString("user_key", null);
        user_secret = settings.getString("user_secret", null);
        if (user_key != null){
        	Log.v("honza", "user key in service:" + user_key);
        } else {
        	Log.v("honza", "user key is null");
        }
		
		messageList = new ArrayList<Message>();
        fetcher = new MessageFetcher("home", user_key, user_secret);
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		serviceHandler.removeCallbacks(myTask);
		serviceHandler = null;
		Log.d(getClass().getSimpleName(),"onDestroy()");
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		serviceHandler = new Handler();
		serviceHandler.postDelayed(myTask, delay);
		Log.d(getClass().getSimpleName(), "onStart()");
	}
	
	class Task implements Runnable {
		public void run() {
			Log.v("honza", "***********************");
			Log.v("honza", "Task in a service");
			//List<Message> newMessages = fetcher.getMessages(last_id);
			if (messageList == null){
				messageList = new ArrayList<Message>();
			} 
			messageList = fetcher.getMessages(last_id);
			Log.v("honza", "fetcher finished");
        	int size = messageList.size();
        	Message m;
        	if (size != 0){
        		m = messageList.get(0);
        		last_id = m.id;
        		Log.v("honza", "Last Id in task: " + Long.toString(last_id));
        	} else {
        		Log.v("honza", "no new messages");
        	}
            
            /* Comment out the following line to prevent looping */
            
            serviceHandler.postDelayed(this, delay);
            Log.v("honza", "*******************");
		}
	}
	
	
}

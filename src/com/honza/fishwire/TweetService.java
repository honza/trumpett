package com.honza.fishwire;

import java.util.ArrayList;
import java.util.List;

import com.honza.fishwire.HomeTab.UpdateTimeline;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class TweetService extends Service {
	
	private Handler serviceHandler;
	private int counter;
	private Task myTask = new Task();
	public long last_id = 0L;
	public int delay = 5000;
	
	
	
	public static final String PREFERENCES = "Fishwire";
	public String user_key = "";
	public String user_secret = "";
	public List<Message> messageList = null;
	public Handler handler = new Handler();
	
	private MessageRowAdapter adapter;
	private MessageFetcher fetcher;
	

	@Override
	public IBinder onBind(Intent arg0) {
		Log.d(getClass().getSimpleName(), "onBind()");
		return myRemoteServiceStub;
	}

	private TweetServiceInterface.Stub myRemoteServiceStub = new TweetServiceInterface.Stub() {
		
		/* Here is where you define remote interface methods */
		
		public int getCounter() throws RemoteException {
			return counter;
		}
		
		public List<Message> getMessages() throws RemoteException {
			return messageList;
		}
		
		public void resetMessages() throws RemoteException {
			messageList = null;
		}
	};
	
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
		serviceHandler.postDelayed(myTask, 1000L);
		Log.d(getClass().getSimpleName(), "onStart()");
	}
	
	class Task implements Runnable {
		public void run() {
			/* ++counter;
			serviceHandler.postDelayed(this,1000L);
			Log.v("honza", "Counter: " + Integer.toString(counter));
			*/
			Log.v("honza", "Task in a service");
			Log.v("honza", "API: " + fetcher.api);
			Log.v("honza", "Key: " + fetcher.key);
			Log.v("honza", "Secret: " + fetcher.secret);
			List<Message> newMessages = fetcher.getMessages(last_id);
			Log.v("honza", "fetcher finished");
        	int size = newMessages.size();
        	Message m;
        	if (size != 0){
        		if (size == 1){
        			m = newMessages.get(0);
        			messageList.add(0, m);
        		} else {
        			m = newMessages.get(size - 1);
        			for (int i = 0; i < size; i++){
        				Message a = newMessages.get(i);
        				messageList.add(0, a);
        				
        			}
        		}
        		last_id = m.id;
        		Log.v("honza", "Last Id in task: " + Long.toString(last_id));
        	} else {
        		Log.v("honza", "no new messages");
        		
        	}
            
            /* Comment out the following line to prevent looping */
            
            serviceHandler.postDelayed(this, delay);
		}
	}
	
	
}

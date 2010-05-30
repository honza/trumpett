package com.honza.fishwire;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ComposeTab extends Activity {
	
	public String PREFERENCES = "Fishwire";
	public String user_key = null;
	public String user_secret = null;
	
	
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.compose_tab);
	        Button send = (Button)findViewById(R.id.send_tweet_button);
	        send.setOnClickListener(sendTweet);
	        SharedPreferences settings = getSharedPreferences(PREFERENCES, 0);
	        user_key = settings.getString("user_key", null);
	        user_secret = settings.getString("user_secret", null);
	    }
	 
	 public OnClickListener sendTweet = new OnClickListener(){
		@Override
		public void onClick(View v) {
			Log.v("honza", "in onclicklistener");
			SendTweet sender = new SendTweet();
			EditText tweetBox = (EditText) findViewById(R.id.tweet);
			String tweet = tweetBox.getText().toString();
			Log.v("honza", "just before execute");
			sender.execute(tweet);
			tweetBox.setText("");
			// etc
			
		}
	 };
	 
	 public class SendTweet extends AsyncTask<String, Void, Void> {
		 
		 private ProgressDialog dialog;
		 
		 protected void onPreExecute(){
			  
			 dialog = ProgressDialog.show(ComposeTab.this,
						"", "Sending tweet...", true);
		 }

		 
		 
		@Override
		protected Void doInBackground(String... params) {
			MessageFetcher fetcher = new MessageFetcher("update", user_key, user_secret);
			// get text, pass it to fetcher.update(tweet)
			
			fetcher.sendUpdate(params[0]);
			return null;
			
		}
		
		protected void onPostExecute(Void v){
			 dialog.dismiss();
		 }
		 
	 }

}

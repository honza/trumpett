package com.honza.fishwire;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class OAuth extends Activity {
	
	private CommonsHttpOAuthConsumer httpOauthConsumer;
	private OAuthProvider httpOauthprovider;
	public final static String consumerKey = 		"bbvYJL1YbvGLQThmNZDQg";
	public final static String consumerSecret = 	"173ePedWitCkZbLCHG6wR0xrtbhrw9igchzPtGS1Xc";
	private final String CALLBACKURL = "fishwire://twitt";
    /*
     * 
     * OnCreate method for class
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        Log.v("honza", "oauth oncreate");
        
        doOauth();
    }
    /**
	 * Opens the browser using signpost jar with application specific 
	 * consumerkey and consumerSecret.
	 */
	private void doOauth() {
		Log.v("honza", "doOauth top");
		try {
			
			httpOauthConsumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
			httpOauthprovider = new DefaultOAuthProvider("http://twitter.com/oauth/request_token",
												"http://twitter.com/oauth/access_token",
												"http://twitter.com/oauth/authorize");
			String authUrl = httpOauthprovider.retrieveRequestToken(httpOauthConsumer, CALLBACKURL);
			this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));
		} catch (Exception e) {
			Log.v("honza", e.getMessage());
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	
	/**
	* After use authorizes this is the function where we get back callbac with
	* user specific token and secret token. You might want to store this token
	* for future use. 
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		
		super.onNewIntent(intent);

		Uri uri = intent.getData();
		//Check if you got NewIntent event due to Twitter Call back only
		if (uri != null && uri.toString().startsWith(CALLBACKURL)) {

			String verifier = uri.getQueryParameter(oauth.signpost.OAuth.OAUTH_VERIFIER);

			try {
				// this will populate token and token_secret in consumer
				
				httpOauthprovider.retrieveAccessToken(httpOauthConsumer, verifier);
				String userKey = httpOauthConsumer.getToken();
				String userSecret = httpOauthConsumer.getTokenSecret();
				Log.v("honza", "key: " + userKey);
				Log.v("honza", "secret:" + userSecret);
				// Save user_key and user_secret in user preferences and return
				
				SharedPreferences settings = getBaseContext().getSharedPreferences("Fishwire", 0);
			    SharedPreferences.Editor editor = settings.edit();
			    editor.putString("user_key", userKey);
			    editor.putString("user_secret", userSecret);
			    Boolean a;
			      // Don't forget to commit your edits!!!
			    a = editor.commit();
			    if (a == true){
			    	Log.v("honza", "a is true");
			    } else {
			    	Log.v("honza", "a is false");
			    }
			    
			    
			    
				Intent returnIntent = new Intent();
			    Bundle creds = new Bundle();
			    creds.putString("key", userKey);
			    creds.putString("secret", userSecret);
			    
			    returnIntent.putExtra("auth", creds);
			    setResult(RESULT_OK, returnIntent);
			    Intent m = new Intent(OAuth.this, Fishwire.class);
			    startActivity(m);
			    this.finish();
			    Log.v("honza", "right after finish");
			    finish();
			    Log.v("honza", "after thisless finish");
			} catch(Exception e){
				Log.v("honza", e.getMessage());
			}
			
				
				/*
				 * Try to post a message
				 * 
				 * The following works
				 * 
				 */
			    
			    /*
				HttpParams parameters = new BasicHttpParams();
				HttpProtocolParams.setVersion(parameters, HttpVersion.HTTP_1_1);
				HttpProtocolParams.setContentCharset(parameters, HTTP.DEFAULT_CONTENT_CHARSET);
				HttpProtocolParams.setUseExpectContinue(parameters, false);
				HttpConnectionParams.setTcpNoDelay(parameters, true);
				HttpConnectionParams.setSocketBufferSize(parameters, 8192);

				SchemeRegistry schReg = new SchemeRegistry();
				schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
				ClientConnectionManager tsccm = new ThreadSafeClientConnManager(parameters, schReg);
				HttpClient mClient = new DefaultHttpClient(tsccm, parameters);
				
				*/
				
				/*
				 * The following can be modified to fetch latest tweets.
				 * Instead of HttpPost use HttpGet, etc.
				 * 
				 */
				
				/*
				HttpPost post = new HttpPost("http://twitter.com/statuses/update.json");
				LinkedList<BasicNameValuePair> out = new LinkedList<BasicNameValuePair>();
				out.add(new BasicNameValuePair("status", "test"));
				post.setEntity(new UrlEncodedFormEntity(out, HTTP.UTF_8));
				post.setParams(getParams());
				// sign the request to authenticate
				httpOauthConsumer.sign(post);
				//String response = mClient.execute(post, new BasicResponseHandler());
				//Log.v("honza", "response: " +response);
				*/
				
				/*
				 * Get user home timeline
				 * 
				 */
				/*
				HttpGet get = new HttpGet("http://api.twitter.com/version/statuses/home_timeline.json");
				//LinkedList<BasicNameValuePair> out = new LinkedList<BasicNameValuePair>();
				//out.add(new BasicNameValuePair("status", "test"));
				//post.setEntity(new UrlEncodedFormEntity(out, HTTP.UTF_8));
				get.setParams(getParams());
				// sign the request to authenticate
				httpOauthConsumer.sign(get);
				String responsex = mClient.execute(get, new BasicResponseHandler());
				Log.v("honza", "response2: " +responsex);
				JSONArray array = new JSONArray(responsex);
				
				if(array != null) {
					try {
						for(int i = 0; i < array.length(); ++i) {
							JSONObject status = array.getJSONObject(i);
							UserStatus s = new UserStatus(status);
							Log.v("honza", s.mStatus.optString("text"));
							
						}
						

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} 
				
				
				
			}
			catch(Exception e){
				Log.d("", e.getMessage());
			}
	
	}
	}
	
	private class UserStatus {

		public JSONObject mStatus;
		public JSONObject mUser;
	
		public UserStatus(JSONObject status) throws JSONException {

			mStatus = status;
			mUser = status.getJSONObject("user");
		}
	}
	
	public HttpParams getParams() {
		// Tweak further as needed for your app
		HttpParams params = new BasicHttpParams();
		// set this to false, or else you'll get an Expectation Failed: error
		HttpProtocolParams.setUseExpectContinue(params, false);
		return params;
	}
	*/
			}
}
}
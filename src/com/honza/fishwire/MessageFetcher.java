package com.honza.fishwire;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class MessageFetcher {

	public String api;
	public List<Message> messages = null;
	public String key = "";
	public String secret = "";
	public CommonsHttpOAuthConsumer httpOauthConsumer;
	
	public final static String consumerKey = "bbvYJL1YbvGLQThmNZDQg";
	public final static String consumerSecret = "173ePedWitCkZbLCHG6wR0xrtbhrw9igchzPtGS1Xc";
	

	public MessageFetcher(String type, String key, String secret){
		Log.v("honza", "message fetcher init");
		Log.v("honza", "key:" + key);
		Log.v("honza", "secret: " + secret);
		if (type == "home"){
			this.api = "http://api.twitter.com/1/statuses/home_timeline.json";
		} 
		if (type == "update"){
			this.api = "http://api.twitter.com/1/statuses/update.json";
		}
		this.key = key;
		this.secret = secret;
		Log.v("honza", "api: " + api);
		httpOauthConsumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
		
		httpOauthConsumer.setTokenWithSecret(key, secret);
		
	}


	public List<Message> getMessages(long id){
		Log.v("honza", "get messages...");
		List<Message> messages = new ArrayList<Message>();
		try {
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
		
		Log.v("honza", "id as receiver in getMSG " + id);
		HttpGet get;
		if (id == 0){
			get = new HttpGet(this.api);
		} else {
			String ur = "http://api.twitter.com/1/statuses/home_timeline.json?since_id=" + Long.toString(id);
			Log.v("honza", "URL: " + ur);
			get = new HttpGet(ur);
		}
		get.setParams(getParams());
		
		// sign the request to authenticate
		httpOauthConsumer.sign(get);
		Log.v("honza", "just before execute...");
		String responsex = mClient.execute(get, new BasicResponseHandler());
		Log.v("honza", "response2: " +responsex);
		JSONArray array = new JSONArray(responsex);
		
		if(array != null) {
			try {
				for(int i = 0; i < array.length(); ++i) {
					JSONObject status = array.getJSONObject(i);
					Message m = new Message(status);
					Log.v("honza", m.user + " " + m.id);
					messages.add(i, m);
										
				}
				

			} catch (JSONException e) {
				e.printStackTrace();
			}
		} 
		
		} catch(Exception e){
			Log.d("Twitter", e.getMessage());
		}
		
		return messages;

	}
	
	public void sendUpdate(String tweet){
		
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
		
		HttpPost post = new HttpPost("http://twitter.com/statuses/update.json");
		LinkedList<BasicNameValuePair> out = new LinkedList<BasicNameValuePair>();
		out.add(new BasicNameValuePair("status", tweet));
		try {
			
			post.setEntity(new UrlEncodedFormEntity(out, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		post.setParams(getParams());
		// sign the request to authenticate
		try {
			httpOauthConsumer.sign(post);
		} catch (OAuthMessageSignerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String response = null;
		try {
			response = mClient.execute(post, new BasicResponseHandler());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.v("honza", "response: " +response);
	}
	
	public HttpParams getParams() {
		// Tweak further as needed for your app
		HttpParams params = new BasicHttpParams();
		// set this to false, or else you'll get an Expectation Failed: error
		HttpProtocolParams.setUseExpectContinue(params, false);
		return params;
	}
}

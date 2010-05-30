package com.honza.fishwire;

import junit.framework.Assert;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;


public class FishAuth extends Activity {

private String CONSUMER_KEY = "bbvYJL1YbvGLQThmNZDQg";
private String CONSUMER_SECRET = "173ePedWitCkZbLCHG6wR0xrtbhrw9igchzPtGS1Xc";
//private String CALLBACK_URL = "fishwire://twitter";
private static final Uri CALLBACK_URI = Uri.parse("fishwire://twitt");
private OAuthConsumer consumer = new DefaultOAuthConsumer(
        CONSUMER_KEY, CONSUMER_SECRET);
private OAuthProvider provider = new DefaultOAuthProvider(
        "http://twitter.com/oauth/request_token", "http://twitter.com/oauth/access_token",
"http://twitter.com/oauth/authorize");


public static final String USER_TOKEN = "user_token";
public static final String USER_SECRET = "user_secret";
public static final String REQUEST_TOKEN = "request_token";
public static final String REQUEST_SECRET = "request_secret";

public void onCreate(Bundle icicle) {
	super.onCreate(icicle);
	setTitle("Auth");
	
	
	provider.setOAuth10a(true);
	
	try {
        // This is really important. If you were able to register your real callback Uri with Twitter, and not some fake Uri
        // like I registered when I wrote this example, you need to send null as the callback Uri in this function call. Then
        // Twitter will correctly process your callback redirection
String authUrl = provider.retrieveRequestToken(consumer, CALLBACK_URI.toString());


//saveRequestInformation(mSettings, mConsumer.getToken(), mConsumer.getTokenSecret());
	this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));
	} catch (OAuthMessageSignerException e) {
		e.printStackTrace();
	} catch (OAuthNotAuthorizedException e) {
		e.printStackTrace();
	} catch (OAuthExpectationFailedException e) {
		e.printStackTrace();
	} catch (OAuthCommunicationException e) {
		e.printStackTrace();
	}

	
}

@Override
protected void onResume() {
	super.onResume();
	Log.v("honza", "... and we've entered on resume.");
	Uri uri = getIntent().getData();
	//Log.v("honza", uri.toString());
	if (uri != null && CALLBACK_URI.getScheme().equals(uri.getScheme())) {
		String token =  null;
		String secret = null;
		Intent i = new Intent(this, FishAuth.class); // Currently, how we get back to main activity.

		try {
			if(!(token == null || secret == null)) {
				consumer.setTokenWithSecret(token, secret);
			}
			String otoken = uri.getQueryParameter(OAuth.OAUTH_TOKEN);
			String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
			
			if (otoken != null){
				Log.v("honza", "otoken: " + otoken);
			} else {
				Log.v("honza", "it's null");
			}
			
			String m = consumer.getToken();
			if (m != null){
				Log.v("honza", "m: " + m);
			}

			// We send out and save the request token, but the secret is not the same as the verifier
			// Apparently, the verifier is decoded to get the secret, which is then compared - crafty
			// This is a sanity check which should never fail - hence the assertion
			Assert.assertEquals(otoken, consumer.getToken());

			// This is the moment of truth - we could throw here
			provider.retrieveAccessToken(consumer, verifier);
			// Now we can retrieve the goodies
			token = consumer.getToken();
			secret = consumer.getTokenSecret();
			//OAUTH.saveAuthInformation(mSettings, token, secret);
			// Clear the request stuff, now that we have the real thing
			//sOAUTH.saveRequestInformation(mSettings, null, null);
			Log.v("honza", "token: " + token);
			Log.v("honza", "secret:" + secret);
			i.putExtra(USER_TOKEN, token);
			i.putExtra(USER_SECRET, secret);
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthNotAuthorizedException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		} finally {
			startActivity(i); // we either authenticated and have the extras or not, but we're going back
			finish();
		}
	}
}



};


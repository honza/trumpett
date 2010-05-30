package com.honza.fishwire;

import org.json.JSONException;
import org.json.JSONObject;

public class Message {
	String body;
	String user;
	Long id; // message id
	
	
	public Message(JSONObject status) throws JSONException{
		JSONObject mStatus = status;
		JSONObject mUser = status.getJSONObject("user");
		
		user = mUser.optString("name", "none");
		body = status.optString("text", "empty");
		id = mStatus.optLong("id", -1);
	}
}

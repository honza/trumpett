package com.honza.fishwire;

interface TweetServiceInterface {

	int getCounter();
	void resetMessages();
	List<RemoteMessage> getMessages();
}

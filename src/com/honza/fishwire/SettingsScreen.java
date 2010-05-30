package com.honza.fishwire;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

public class SettingsScreen extends PreferenceActivity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.settings_screen);
		//Get the custom preference
		Log.v("honza", "inside of settings screen");

	}
	
}
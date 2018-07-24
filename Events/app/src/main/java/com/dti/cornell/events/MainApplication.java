package com.dti.cornell.events;

import android.app.Application;

import com.dti.cornell.events.utils.Data;
import com.dti.cornell.events.utils.Internet;
import com.dti.cornell.events.utils.SettingsUtil;

import net.danlew.android.joda.JodaTimeAndroid;

public class MainApplication extends Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();

		//set up static things
		JodaTimeAndroid.init(this);
		SettingsUtil.createSingleton(this);
		Internet.createRequestQueue(this);
		Data.createDummyData();
	}
}

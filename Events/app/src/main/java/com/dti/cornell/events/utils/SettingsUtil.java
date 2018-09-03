package com.dti.cornell.events.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.models.Organization;

import org.joda.time.DateTime;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SettingsUtil
{
	public static SettingsUtil SINGLETON;
	private final SharedPreferences settings;

	//keys
	private static final String TIMESTAMP = "timestamp";
	private static final String EVENTS = "events";
	private static final String ORGANIZATIONS = "organizations";
	private static final String FIRST_RUN = "firstRun";

	private SettingsUtil(Context context)
	{
		settings = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public static void createSingleton(Context context)
	{
		if (SINGLETON == null)
			SINGLETON = new SettingsUtil(context);
	}

	public String getTimestamp()
	{
		//default = oldest possible time
		return settings.getString(TIMESTAMP, new DateTime(0).toString(Internet.TIME_FORMAT));
	}
	public void setTimestamp(String timestamp)
	{
		settings.edit()
				.putString(TIMESTAMP, timestamp)
				.apply();
	}

	public Map<Integer, Event> getEvents()
	{
		Set<String> eventStrings = settings.getStringSet(EVENTS, Collections.<String>emptySet());
		Map<Integer, Event> events = new HashMap<>(eventStrings.size());
		for (String eventString : eventStrings)
		{
			Event event = Event.fromString(eventString);
			events.put(event.id, event);
		}
		return events;
	}

	public void setEvents(Map<Integer, Event> events)
	{
		setStringSet(new HashSet<>(events.values()), EVENTS);
	}

	public Map<Integer, Organization> getOrganizations()
	{
		Set<String> orgStrings = settings.getStringSet(ORGANIZATIONS, Collections.<String>emptySet());
		Map<Integer, Organization> organizations = new HashMap<>(orgStrings.size());
		for (String orgString : orgStrings)
		{
			Organization org = Organization.fromString(orgString);
			organizations.put(org.id, org);
		}
		return organizations;
	}

	public void setOrganizations(Map<Integer, Organization> organizations)
	{
		setStringSet(new HashSet<>(organizations.values()), ORGANIZATIONS);
	}

	public boolean getFirstRun()
	{
		return settings.getBoolean(FIRST_RUN, true);
	}
	public void setFirstRun()
	{
		settings.edit()
				.putBoolean(FIRST_RUN, false)
				.apply();
	}


	private void setStringSet(Set<?> set, String key)
	{
		Set<String> strings = new HashSet<>(set.size());
		for (Object o : set)
			strings.add(o.toString());

		settings.edit()
				.putStringSet(key, strings)
				.apply();
	}
}

package com.dti.cornell.events.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.models.Organization;

import org.joda.time.DateTime;

import java.util.ArrayList;
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
	private static final String NAME = "name";
	private static final String EMAIL = "email";
	private static final String TOKEN = "token";
	private static final String IMAGE_URL = "imageURL";

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

	public String getName()
	{
		return settings.getString(NAME, null);
	}

	public void setName(String name)
	{
		settings.edit()
				.putString(NAME, name)
				.apply();
	}

	public String getEmail()
	{
		return settings.getString(EMAIL, null);
	}

	public void setEmail(String email)
	{
		settings.edit()
				.putString(EMAIL, email)
				.apply();
	}

	public String getToken()
	{
		return settings.getString(TOKEN, null);
	}
	public void setToken(String token)
	{
		settings.edit()
				.putString(TOKEN, token)
				.apply();
	}

	public String getImageUrl()
	{
		return settings.getString(IMAGE_URL, null);
	}

	public void setImageUrl(String imageUrl)
	{
		settings.edit()
				.putString(IMAGE_URL, imageUrl)
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

	public static void loadTags(Context context){
		String toBeDecoded = PreferenceManager.getDefaultSharedPreferences(context).getString("TAG_STRING", "");
		TagUtil.tagIDsAndImportance = TagUtil.decodeTagIDs(toBeDecoded);
		TagUtil.tagsInterested = new ArrayList<>();
		for(Integer eventID : TagUtil.tagIDsAndImportance.keySet()){
			TagUtil.tagsInterested.add(eventID);
		}
		TagUtil.tagsLoaded = true;
	}

	public static void saveTags(Context context){
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString("TAG_STRING", OrganizationUtil.encodeOrganizationIDs()).apply();
	}

	public static void loadOrganizations(Context context){
		String toBeDecoded = PreferenceManager.getDefaultSharedPreferences(context).getString("ORGANIZATION_STRING", "");
		OrganizationUtil.followedOrganizations = OrganizationUtil.decodeTagIDs(toBeDecoded);
		OrganizationUtil.organizationsLoaded = true;
	}

	public static void saveFollowedOrganizations(Context context){
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString("ORGANIZATION_STRING", OrganizationUtil.encodeOrganizationIDs()).apply();
	}

}

package com.dti.cornell.events.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.models.Organization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SettingsUtil
{
	public static SettingsUtil SINGLETON;
	private final SharedPreferences settings;

	//local data holders
	Map<Integer, Event> events = new HashMap<>();

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
//		return settings.getString(TIMESTAMP, new DateTime(0).toString(Internet.TIME_FORMAT));
		return "2017-02-19T01:43:40.753131-05:00";
	}
	public void setTimestamp(String timestamp)
	{
		settings.edit()
				.putString(TIMESTAMP, timestamp)
				.apply();
	}

	public Map<Integer, Event> getEvents()
	{
		if(Data.events().isEmpty()){
			loadEvents();
		}
		return Data.eventForID;
//		Set<String> eventStrings = settings.getStringSet(EVENTS, Collections.<String>emptySet());
//		Map<Integer, Event> events = new HashMap<>(eventStrings.size());
//		for (String eventString : eventStrings)
//		{
//			Event event = Event.fromString(eventString);
//			events.put(event.id, event);
//		}
//		return events;
	}

	public void saveEvents(List<Event> events)
	{
		HashSet<String> eventsStrings = new HashSet<String>();
		for(Event e : events){
			eventsStrings.add(e.toString());
		}
		Log.e("SETTINGSUTIL", "HERE ARE STRINGS --> " + eventsStrings.toString());
		setStringSet(eventsStrings, EVENTS);
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



	public void loadEvents(){
		Set<String> eventStringSet = settings.getStringSet(EVENTS, new HashSet<>());
		Set<Event> events = eventStringSet.stream().map((val) -> Event.fromString(val)).collect(Collectors.toSet());
		for(Event e : events){
			Log.e("SETTINGSUTIL", "EVENT LOADED with ID: " + e.id);
			Data.eventForID.put(e.id, e);
		}
		if(events.size() > 0){
			Data.emitEventUpdate();
		}
	}


	public void loadTags(){
		//String toBeDecoded = PreferenceManager.getDefaultSharedPreferences(context).getString("TAG_STRING", "");
		SharedPreferences sp = settings;
		String toBeDecoded = sp.getString("TAG_STRING", "DEFAULT");
		Log.e("TAG STRING ENCODED", toBeDecoded);
		if(toBeDecoded.equalsIgnoreCase("DEFAULT")){
			Log.e("SettingsUtil Tags", "Tags are null!");
			toBeDecoded = "";
		}
		TagUtil.tagIDsAndImportance = TagUtil.decodeTagIDs(toBeDecoded);
		TagUtil.tagsInterested = new ArrayList<>();
		for(Integer eventID : TagUtil.tagIDsAndImportance.keySet()){
			TagUtil.tagsInterested.add(eventID);
		}
		TagUtil.tagsLoaded = true;
	}

	public void loadOrganizations(){
		SharedPreferences sp = settings;
		String toBeDecoded = sp.getString("ORGANIZATION_STRING", "DEFAULT");
		Log.e("ORG STRING ENCODED", toBeDecoded);
		if(toBeDecoded.equalsIgnoreCase("DEFAULT")){
			Log.e("loadOrgs", "TOBEDECODED IS NULL");
			toBeDecoded = "";
		}
		OrganizationUtil.followedOrganizations = OrganizationUtil.decodeTagIDs(toBeDecoded);
		OrganizationUtil.organizationsLoaded = true;
	}

	public void loadAttendance(){
		//String toBeDecoded = PreferenceManager.getDefaultSharedPreferences(context).getString("ATTENDANCE_STRING", "");
		SharedPreferences sp = settings;
		String toBeDecoded = sp.getString("ATTENDANCE_STRING", "DEFAULT");
		Log.e("ATT STRING ENCODED", toBeDecoded);
		if(toBeDecoded.equalsIgnoreCase("DEFAULT")){
			Log.e("loadAttendance", "TOBEDECODED IS NULL");
			toBeDecoded = "";
		}
//		EventUtil.interestedEvents = EventUtil.decodeEventIDs(toBeDecoded, ATTENDANCE.INTERESTED);
//		EventUtil.goingEvents = EventUtil.decodeEventIDs(toBeDecoded, ATTENDANCE.GOING);
		EventUtil.allAttendanceEvents = EventUtil.decodeEventIDs(toBeDecoded);
		EventUtil.removeDuplicates();
		EventUtil.attendanceLoaded = true;
	}

	public void saveTags(){
		SharedPreferences.Editor e = settings.edit();
		e.putString("TAG_STRING", TagUtil.encodeTagIDs());
		e.commit();
		//PreferenceManager.getDefaultSharedPreferences(context).edit().putString("TAG_STRING", TagUtil.encodeTagIDs()).commit();
	}

	public void saveFollowedOrganizations(){
//		SharedPreferences settings;
//		settings = context.getSharedPreferences("ORGANIZATION_STRING", Context.MODE_PRIVATE);
//		settings.edit().putString(OrganizationUtil.encodeOrganizationIDs(), null).apply();
		Log.e("OrgEncodeCheck", OrganizationUtil.encodeOrganizationIDs());
		SharedPreferences.Editor e = settings.edit();
		e.putString("ORGANIZATION_STRING", OrganizationUtil.encodeOrganizationIDs());
		e.commit();
	}

	public void saveAttendance(){
		Log.e("EventEncodeCheck", EventUtil.encodeEventIDs());
		SharedPreferences.Editor e = settings.edit();
		e.putString("ATTENDANCE_STRING", EventUtil.encodeEventIDs());
		e.commit();
		//PreferenceManager.getDefaultSharedPreferences(context).edit().putString("ATTENDANCE_STRING", EventUtil.encodeEventIDs()).commit();
	}

}

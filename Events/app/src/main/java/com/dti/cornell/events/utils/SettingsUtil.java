package com.dti.cornell.events.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.models.Location;
import com.dti.cornell.events.models.Organization;
import com.dti.cornell.events.models.Settings;

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
	private Settings settingsObject;

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
	private static final String SIGN_IN_TOKEN = "signInToken";
	private static final String IMAGE_URL = "imageURL";
	private static final String NOTIFICATION_TIME_BEFORE_EVENT = "notifTimeBeforeEvent";
	private static final String SETTINGS = "settingsObject";
	private static final String LOCATIONS = "locations";

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

	public Settings getSettings(){
		if(settingsObject == null){
			String settingsString = settings.getString(SETTINGS, "null");
			if(settingsString.equalsIgnoreCase("null")){
				return new Settings("15 Minutes Before", true);
			}
			this.settingsObject = Settings.fromString(settingsString);
			return settingsObject;
		} else {
			return settingsObject;
		}
	}

	public void setSettings(Settings settingsObject){
		this.settingsObject = settingsObject;
		settings.edit().putString(SETTINGS, settingsObject.toString()).apply();
	}

	public String getNotificationTimeBeforeEvent()
	{
		if(this.settingsObject == null){
			return this.getSettings().notifyMeTime;
		}
		return settingsObject.notifyMeTime;
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

	public String getSignInToken()
	{
		return settings.getString(SIGN_IN_TOKEN, null);
	}

	public void setSignInToken(String token)
	{
		settings.edit()
				.putString(SIGN_IN_TOKEN, token)
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


	// LOADING & SAVING FROM MAINACTIVITY

	public void loadSettings(){
		String settingsString = settings.getString("SETTINGS", "15 Minutes Before>>true");
		Settings loadedSettings = Settings.fromString(settingsString);
		this.settingsObject = loadedSettings;
	}

	public void saveSettings(Settings s){
		settings.edit().putString("SETTINGS", s.toString()).apply();
	}

	public void saveTagsObjs(){
		String tagsString = TagUtil.encodeTags();
		settings.edit().putString("TAGOBJ", tagsString).apply();
	}

	public void loadTagsObj(){
		String encodedTag = settings.getString("TAGOBJ", "");
		if(encodedTag.isEmpty()){
			return;
		}
		TagUtil.decodeTags(encodedTag);
	}

	public void saveOrgs()
	{
		HashSet<String> orgStrings = new HashSet<String>();
		for(Organization o : Data.organizationForID.values()){
			orgStrings.add(o.toString());
		}
		setStringSet(orgStrings, ORGANIZATIONS);
	}

	public void loadOrgs(){
		Set<String> orgStringSet = settings.getStringSet(ORGANIZATIONS, new HashSet<>());
		Set<Organization> orgs = orgStringSet.stream().map((val) -> Organization.fromString(val)).collect(Collectors.toSet());
		for(Organization o : orgs){
			Data.organizationForID.put(o.id, o);
		}
	}

	public void saveLocations()
	{
		HashSet<String> locStrings = new HashSet<String>();
		for(Location l : Data.locationForID.values()){
			locStrings.add(l.toString());
		}
		setStringSet(locStrings, LOCATIONS);
	}

	public void loadLocations(){
		Set<String> locStringSet = settings.getStringSet(LOCATIONS, new HashSet<>());
		Set<Location> locs = locStringSet.stream().map((val) -> Location.fromString(val)).collect(Collectors.toSet());
		for(Location l : locs){
			Data.locationForID.put(l.id, l);
		}
	}


	public void loadEvents(){
		Set<String> eventStringSet = settings.getStringSet(EVENTS, new HashSet<>());
		Set<Event> events = eventStringSet.stream().map((val) -> Event.fromString(val)).collect(Collectors.toSet());
		for(Event e : events){
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

	public void loadFollowedOrganizations(){
		SharedPreferences sp = settings;
		String toBeDecoded = sp.getString("ORGANIZATION_STRING", "DEFAULT");
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
		SharedPreferences.Editor e = settings.edit();
		e.putString("ORGANIZATION_STRING", OrganizationUtil.encodeOrganizationIDs());
		e.commit();
	}

	public void saveAttendance(){
		SharedPreferences.Editor e = settings.edit();
		e.putString("ATTENDANCE_STRING", EventUtil.encodeEventIDs());
		e.commit();
		//PreferenceManager.getDefaultSharedPreferences(context).edit().putString("ATTENDANCE_STRING", EventUtil.encodeEventIDs()).commit();
	}

	public static void doSave(){
		SettingsUtil.SINGLETON.saveTags();
		SettingsUtil.SINGLETON.saveFollowedOrganizations();
		SettingsUtil.SINGLETON.saveOrgs();
		SettingsUtil.SINGLETON.saveAttendance();
		SettingsUtil.SINGLETON.saveLocations();
		SettingsUtil.SINGLETON.saveTagsObjs();
		SettingsUtil.SINGLETON.saveEvents(Data.events());
		SettingsUtil.SINGLETON.saveSettings(SettingsUtil.SINGLETON.getSettings());
	}

	public static void doLoad(){
		SettingsUtil.SINGLETON.loadSettings();
		SettingsUtil.SINGLETON.loadLocations();
		SettingsUtil.SINGLETON.loadOrgs();
		SettingsUtil.SINGLETON.loadTagsObj();
		if(!TagUtil.tagsLoaded){
			SettingsUtil.SINGLETON.loadTags();
		}
		if(!OrganizationUtil.organizationsLoaded){
			SettingsUtil.SINGLETON.loadFollowedOrganizations();
		}
		if(!EventUtil.attendanceLoaded){
			SettingsUtil.SINGLETON.loadAttendance();
		}
	}

}

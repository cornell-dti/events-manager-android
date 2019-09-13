package com.dti.cornell.events.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.dti.cornell.events.models.Event;
import com.dti.cornell.events.models.Location;
import com.dti.cornell.events.models.Organization;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Data
{
	public static final Map<Integer, Event> eventForID = new HashMap<>();
	public static final Map<Integer, String> tagForID = new HashMap<>();
	public static final Map<Integer, Organization> organizationForID = new HashMap<>();
	public static final Map<Integer, String> mediaForID = new HashMap<>();
	public static final Map<String, Bitmap> bitmapForURL = new HashMap<>();
	public static final Map<Integer, Location> locationForID = new HashMap<>();
	public static DateTime selectedDate;

	public static final int NUM_DAYS_IN_FEED = 30;
	public static final ImmutableList<DateTime> DATES;

	static
	{
		DateTime now = DateTime.now();
		List<DateTime> dates = new ArrayList<>(NUM_DAYS_IN_FEED);
		for (int i = 0; i < NUM_DAYS_IN_FEED; i++)
			dates.add(now.plusDays(i));
		DATES = ImmutableList.copyOf(dates);

		selectedDate = now;
	}

	public void init(){
		mediaForID.put(-1, "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/No_image_available.svg/1024px-No_image_available.svg.png");
	}

	public static void getData(){
		Internet.getEventFeed();

		Log.e("DATA OUTPUT", eventForID.toString());
		Log.e("DATA OUTPUT", SettingsUtil.SINGLETON.getEvents().toString());
	}

	public static void createDummyData()
	{
		//TODO remove when API is available
		tagForID.put(1, "#Tag1");
		tagForID.put(2, "#davidLovesHashtags!");
		tagForID.put(3, "#halp");
		tagForID.put(4, "#kornell");

		Organization organization = new Organization(1, "Cornell Design & Tech Initiative and more stuff blah blah blah", "Hi there howdy do yall. This is an extremely long, grandiloquent, pontificate description designed for the sole singular purpose of testing limits of descriptions", 1, ImmutableList.of(1, 2, 3, 4, 5, 6, 7), ImmutableList.of("User id"), ImmutableList.of(1,2,3,4), "www.google.com", "dc788@cornell.edu");
		organizationForID.put(organization.id, organization);

		for (int i = 1; i <= DATES.size(); i++)
		{
			Event event = new Event(i, DATES.get(i-1), DATES.get(i-1).plusDays(i).plusMinutes(i*30), "I’m just trying to see what it would look like if there was like an extra long title blah", "The simple yet courageous #metoo hashtag campaign has emerged as a rallying cry for people everywhere who have survived sexual assault and sexual harassment", "Goldwin Smith Hall Room 202", "ChIJndqRYRqC0IkR9J8bgk3mDvU", ImmutableList.of("User id"), "http://imgur.com/", 1, ImmutableList.of(1, 2, 3, 4), 160);
			eventForID.put(i, event);
		}
	}


	public static List<Event> events()
	{
		List<Event> events = Lists.newArrayList(eventForID.values());
		Collections.sort(events);
		return events;
	}

	public static List<Organization> organizations()
	{
		List<Organization> organizations = Lists.newArrayList(organizationForID.values());
		Collections.sort(organizations);
		return organizations;
	}

	public static List<String> tags()
	{
		List<String> tags = Lists.newArrayList(tagForID.values());
		Collections.sort(tags);
		return tags;
	}

	public static Event getEventFromID(int eventID){
		return eventForID.get(eventID);
	}

	public static boolean equalsSelectedDate(DateTime dateTime)
	{
		return DateTimeComparator.getDateOnlyInstance().compare(dateTime, selectedDate) == 0;
	}



	static List<DataUpdateListener> listeners = new ArrayList<>();

	public interface DataUpdateListener{
		void eventUpdate(List<Event> e);
		void orgUpdate(List<Organization> o);
		void tagUpdate(List<String> t);
	}

	public static void registerListener(DataUpdateListener d){
		listeners.add(d);
	}

	public static void unregisterListener(DataUpdateListener d){
		listeners.remove(d);
	}

	public static void emitEventUpdate(){
		for(DataUpdateListener d : listeners){
			d.eventUpdate(events());
		}
	}

	public static void emitOrgUpdate(){
		for(DataUpdateListener d : listeners){
			d.orgUpdate(organizations());
		}
	}

	public static void emitTagUpdate(){
		for(DataUpdateListener d : listeners){
			d.tagUpdate(tags());
		}
	}


}

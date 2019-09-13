package com.dti.cornell.events.models;

import android.text.TextUtils;

import com.dti.cornell.events.utils.ToStringUtil;
import com.google.common.collect.ImmutableList;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;

/**
 * Created by jaggerbrulato on 2/27/18.
 */

public class Event implements Comparable<Event>
{
	public final int id;
	public final DateTime startTime;
	public final DateTime endTime;
	public final String title;
	public final String description;
	public final String location;
	public final String googlePlaceID;
	public final ImmutableList<String> participantIDs;
	public int numAttendees;
	public final String pictureID;
	public final int organizerID;
	public final ImmutableList<Integer> tagIDs;
	private static final String TAG = Event.class.getSimpleName();

	public Event(int id, DateTime startTime, DateTime endTime, String title, String description, String location, String googlePlaceID, ImmutableList<String> participantIDs, String pictureID, int organizerID, ImmutableList<Integer> tagIDs
	, int numAttendees)
	{
		this.id = id;
		this.startTime = startTime;
		this.endTime = endTime;
		this.title = title;
		this.description = description;
		this.location = location;
		this.googlePlaceID = googlePlaceID;
		this.participantIDs = participantIDs;
		this.pictureID = pictureID;
		this.organizerID = organizerID;
		this.tagIDs = tagIDs;
		this.numAttendees = numAttendees;
	}
	
	public String toString()
	{
		return id + ToStringUtil.FIELD_SEPARATOR +
				startTime + ToStringUtil.FIELD_SEPARATOR +
				endTime + ToStringUtil.FIELD_SEPARATOR +
				title + ToStringUtil.FIELD_SEPARATOR +
				description + ToStringUtil.FIELD_SEPARATOR +
				location + ToStringUtil.FIELD_SEPARATOR +
				googlePlaceID + ToStringUtil.FIELD_SEPARATOR +
				TextUtils.join(ToStringUtil.ARRAY_SEPARATOR, participantIDs) + ToStringUtil.FIELD_SEPARATOR +
				pictureID + ToStringUtil.FIELD_SEPARATOR +
				organizerID + ToStringUtil.FIELD_SEPARATOR +
				numAttendees + ToStringUtil.FIELD_SEPARATOR +
				TextUtils.join(ToStringUtil.ARRAY_SEPARATOR, tagIDs);
	}

	public static Event fromString(String input)
	{
		String[] values = input.split(ToStringUtil.FIELD_SEPARATOR);
		int id = Integer.valueOf(values[0]);
		DateTime start = DateTime.parse(values[1]);
		DateTime end = DateTime.parse(values[2]);
		String title = values[3];
		String description = values[4];
		String location = values[5];
		String placeID = values[6];
		ImmutableList<String> participantIDs = ToStringUtil.listFromString(values[7]);
		String pictureID = values[8];
		int organizerID = Integer.valueOf(values[9]);
		int numAttendees = Integer.valueOf(values[10]);
		ImmutableList<Integer> tagIDs;
		if(values.length > 11){
			 tagIDs = ToStringUtil.intListFromString(values[11]);
		} else {
			tagIDs = ImmutableList.of();
		}
		return new Event(id, start, end, title, description, location, placeID, participantIDs, pictureID, organizerID, tagIDs, numAttendees);
	}

	public static Event fromJSON(JSONObject json) throws JSONException
	{
		int id = json.getInt("pk");
		String title = json.getString("name");
		String description = json.getString("description");
		int organizerID = json.getInt("organizer");
		return null;
		//TODO
	}

	@Override
	public int compareTo(@NonNull Event o)
	{
		return startTime.compareTo(o.startTime);
	}
}

package com.dti.cornell.events.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;

public class EventItem implements Comparable<EventItem>
{
	@Nullable
	public final Event event;
	@Nullable
	public final DateTime subheader;

	public EventItem(Event event)
	{
		this.event = event;
		subheader = null;
	}
	public EventItem(DateTime subheader)
	{
		this.subheader = subheader;
		event = null;
	}

	@Override
	public int compareTo(@NonNull EventItem eventItem)
	{
		return DateTimeComparator.getDateOnlyInstance()
				.compare(event != null ? event.startTime : subheader,
						eventItem.event != null ? eventItem.event.startTime : eventItem.subheader);
	}
}

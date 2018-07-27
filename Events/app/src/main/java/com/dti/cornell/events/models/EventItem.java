package com.dti.cornell.events.models;

import android.support.annotation.Nullable;

public class EventItem
{
	@Nullable
	public final Event event;
	@Nullable
	public final String subheader;

	public EventItem(Event event)
	{
		this.event = event;
		subheader = null;
	}
	public EventItem(String subheader)
	{
		this.subheader = subheader;
		event = null;
	}
}

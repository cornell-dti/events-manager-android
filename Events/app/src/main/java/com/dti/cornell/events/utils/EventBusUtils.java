package com.dti.cornell.events.utils;

import com.dti.cornell.events.models.Organization;
import com.google.common.eventbus.EventBus;

public class EventBusUtils
{
	public static final EventBus SINGLETON = new EventBus();

	public static final class DateChanged
	{
		public final String sender;
		public DateChanged(String sender)
		{
			this.sender = sender;
		}
	}
	public static final class OrganizationSelected
	{
		public final Organization organization;
		public final int position;
		public OrganizationSelected(Organization organization, int position)
		{
			this.organization = organization;
			this.position = position;
		}
	}
	public static final class TagSelected
	{
		public final int tagID;
		public final int position;

		public TagSelected(int tagID, int position)
		{
			this.tagID = tagID;
			this.position = position;
		}
	}
	public static final class SearchChanged
	{
		public final String text;

		public SearchChanged(String text) {
			this.text = text;
		}
	}
}

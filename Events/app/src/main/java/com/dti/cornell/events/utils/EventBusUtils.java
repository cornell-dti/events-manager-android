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
}

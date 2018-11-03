package com.dti.cornell.events.utils;

import com.dti.cornell.events.models.Organization;
import com.google.common.eventbus.EventBus;

import org.joda.time.DateTime;

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
		public final DateTime date;

		public SearchChanged(String text, DateTime date) {
			this.text = text;
			this.date = date;
		}
	}

//	public static final class SearchDateChanged
//	{
//		public final DateTime searchDate;
//
//		public SearchDateChanged(DateTime searchDate) {
//			this.searchDate = searchDate;
//		}
//	}

	public static final class MainActivityScrolled
	{
		public final int scrollY;

		public MainActivityScrolled(int scrollY) {
			this.scrollY = scrollY;
		}
	}

	public static final class OrganizationPickedSearch
	{
		public final Organization organization;
		public OrganizationPickedSearch(Organization org){
			organization = org;
		}
	}

	public static final class TagPickedSearch
	{
		public final String tag;
		public TagPickedSearch(String tag){
			this.tag = tag;
		}
	}

}

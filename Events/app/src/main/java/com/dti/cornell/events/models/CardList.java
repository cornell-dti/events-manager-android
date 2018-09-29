package com.dti.cornell.events.models;

import android.support.annotation.StringRes;

import java.util.List;

public class CardList
{
	@StringRes
	public int section;
	public String sectionText = "";
	public final boolean showSeeMore;
	public final List<Event> events;

	public CardList(int section, boolean showSeeMore, List<Event> events)
	{
		this.section = section;
		this.showSeeMore = showSeeMore;
		this.events = events;
	}

	public CardList(String section, boolean showSeeMore, List<Event> events)
	{
		this.sectionText = section;
		this.showSeeMore = showSeeMore;
		this.events = events;
	}

}

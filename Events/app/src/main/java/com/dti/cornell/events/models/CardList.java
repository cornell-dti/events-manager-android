package com.dti.cornell.events.models;

import android.support.annotation.StringRes;

import com.dti.cornell.events.R;

import java.util.List;

public class CardList
{
	@StringRes
	public static int section;
	public static String sectionText = "";
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

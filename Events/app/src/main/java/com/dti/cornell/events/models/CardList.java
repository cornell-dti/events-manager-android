package com.dti.cornell.events.models;

import android.content.res.Resources;

import java.util.List;

import androidx.annotation.StringRes;

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

	public String getSectionText(){
		if(sectionText == null){
			return Resources.getSystem().getString(section);
		} else {
			return sectionText;
		}
	}

}
